package service;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import dto.PassengerResponseDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.*;
import repository.*;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;

public class BookingService {
    private long pnr = 4000000000L;
    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final WaitingListRepository waitingListRepository;

    public BookingService(TrainRepository trainRepository, SeatRepository seatRepository, BookingRepository bookingRepository, PassengerRepository passengerRepository, WaitingListRepository waitingListRepository) {
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.waitingListRepository = waitingListRepository;
    }

    public List<Train> getAllTrains() throws DataBaseException {
        return trainRepository.getAllTrains();
    }

    public String getAvailableSeats(int trainId) throws DataBaseException {
        int cnfSeats = seatRepository.getAvailableSeats(trainId).size();
        if (cnfSeats > 0) return "CNF "+cnfSeats;
        int racNo = waitingListRepository.getAvailableRac(trainId);
        if (racNo > 0) return "RAC "+racNo;
        int wlNo = waitingListRepository.getAvailableWl(trainId);
        if (wlNo > 0) return "WL "+wlNo;
        return "Regret No More booking";
    }

    public BookingResponseDto bookTickets(int trainId, String from, String to, List<PassengerRequestDto> passengers) throws DataBaseException {

        if (passengers == null || passengers.isEmpty()) throw new InvalidInputException("No Passengers Provided");


        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
        int totalPassengers = passengers.size();
        int cnfAvail = availableSeats.size();
        int cnfCnt = Math.min(cnfAvail, totalPassengers);
        int racAvail = waitingListRepository.getAvailableRac(trainId);
        int wlAvail = waitingListRepository.getAvailableWl(trainId);
        int currentCanBook = cnfAvail + racAvail + wlAvail;
        if (passengers.size() > currentCanBook) throw new SeatNotFoundException("Only "+currentCanBook+" Seats left please Enter "+currentCanBook+" passengers.");

        String pnr = generatePNR();
        Booking currentBooking = new Booking(0, pnr, from, to, trainId, SessionStorage.getCurrentUser().getUserId(), BookingStatus.ACTIVE);
        bookingRepository.addBooking(currentBooking);

        List<Passenger> selectedPassengers = new ArrayList<>();
        List<Integer> cnfSeatIds = new ArrayList<>();

        for (int i=0; i<cnfCnt; i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);
//             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
            cnfSeatIds.add(curSeat.getSeatId());
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
//             passengerRepository.addPassenger(cnfPassenger);
             selectedPassengers.add(cnfPassenger);
         }
        if (!cnfSeatIds.isEmpty()) {
            seatRepository.updateAllSeatStatus(cnfSeatIds, SeatStatus.BOOKED);
        }
         if (cnfAvail < totalPassengers) {
             List<Passenger> wlPassengers = updateWaitingQueue(cnfCnt, passengers, trainId, pnr, racAvail, wlAvail);
             selectedPassengers.addAll(wlPassengers);
         }

        if (!selectedPassengers.isEmpty()) {
            passengerRepository.addAllPassenger(selectedPassengers);
        }

         List<PassengerResponseDto> addedPassengers = gatherPassengerDetails(pnr, trainId);
         return new BookingResponseDto(pnr, from, to, trainRepository.getTrainById(trainId), addedPassengers.size(), addedPassengers, currentBooking.getStatus());
    }

    public List<Booking> getAllBookings() throws DataBaseException {
        return bookingRepository.getBookingByUser(SessionStorage.getCurrentUser().getUserId());
    }

    public BookingResponseDto getBookingDetails(String pnr) throws DataBaseException {
        Booking curBooking = bookingRepository.getBookingByPnr(pnr);
        if (curBooking == null) throw new ItemNotFoundException("No bookings found for this Account");
        Train curTrain = trainRepository.getTrainById(curBooking.getTrainId());
        if (curTrain == null) throw new ItemNotFoundException("No Train found for this trainId"+curBooking.getTrainId());
        List<PassengerResponseDto> curPassengers = gatherPassengerDetails(pnr, curTrain.getTrainId());
        return new BookingResponseDto(pnr, curBooking.getFrom(), curBooking.getTo(), curTrain, curPassengers.size(), curPassengers, curBooking.getStatus());
    }

    private List<Passenger> updateWaitingQueue(int index, List<PassengerRequestDto> passengers, int trainId, String pnr, int racAvail, int wlAvail) {

        List<Passenger> waitingPassengers = new ArrayList<>();

        for (int i = index; i < passengers.size(); i++) {
            PassengerRequestDto curPassengerReq = passengers.get(i);
            Passenger curPassenger;
            if (racAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.RAC, pnr,-1);
//                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingPassengers.add(curPassenger);
//                waitingListRepository.addRac(trainId, curPassenger);
                racAvail--;
            } else if(wlAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.WL, pnr, -1);
//                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingPassengers.add(curPassenger);
//                waitingListRepository.addWl(trainId, curPassenger);
                wlAvail--;
            } else {
                throw new SeatNotFoundException("Unexpected Error Occurs");
            }
        }
        List<Passenger> racPassengers = waitingPassengers.stream().filter(k->k.getStatus() == PassengerStatus.RAC).toList();
        List<Passenger> wlPassengers = waitingPassengers.stream().filter(k->k.getStatus() == PassengerStatus.WL).toList();

        if (!racPassengers.isEmpty()) {
            waitingListRepository.addAllRac(trainId, racPassengers);
        }
        if (!wlPassengers.isEmpty()) {
            waitingListRepository.addAllWl(trainId, wlPassengers);
        }

       return waitingPassengers;
    }
    public List<PassengerResponseDto> gatherPassengerDetails(String pnr, int trainId) throws DataBaseException {
        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(pnr);
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();
        for (Passenger p : passengers) {
//            System.out.println("current passenger status"+p.getStatus());
            if (p.getStatus() == PassengerStatus.CANCELLED) continue;
            int seatNo;
            if (p.getStatus() == PassengerStatus.CNF){
                seatNo = seatRepository.getSeatById(p.getSeatId()).getSeatNo();
            } else {
                seatNo = waitingListRepository.getRacNo(trainId, p.getPassengerId());
                if (seatNo == -1) {
                    seatNo = waitingListRepository.getWlNo(trainId, p.getPassengerId());
                }
            }
            passengerResponseDtos.add(
                    new PassengerResponseDto(p.getPassengerId(), p.getName(), p.getAge(), p.getStatus(), seatNo)
            );
        }
        return passengerResponseDtos;
    }
    private String generatePNR() throws DataBaseException {
        pnr += bookingRepository.getTotalNoRows();
        return String.valueOf(pnr);
    }

    public List<Booking> getActiveBookings() throws DataBaseException {
        return bookingRepository.getActiveBookings(SessionStorage.getCurrentUser().getUserId());
    }
}
