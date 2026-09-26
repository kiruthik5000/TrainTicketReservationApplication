package service;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import dto.PassengerResponseDto;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.*;
import repository.*;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;

public class BookingService {
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

    public List<Train> getAllTrains() {
        return trainRepository.getAllTrains();
    }

    public String getAvailableSeats(int trainId) {
        int cnfSeats = seatRepository.getAvailableSeats(trainId).size();
        if (cnfSeats > 0) return "CNF "+cnfSeats;
        int racNo = waitingListRepository.getRACAvailabilityNo(trainId);
        if (racNo != -1) return "RAC "+racNo;
        int wlNo = waitingListRepository.getWLNAvailability(trainId);
        if (wlNo != -1) return "WL "+wlNo;
        return "Regret No More booking";
    }

    public BookingResponseDto bookTickets(int trainId, String from, String to, List<PassengerRequestDto> passengers) {

        if (passengers == null || passengers.isEmpty()) throw new InvalidInputException("No Passengers Provided");


        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
        int totalPassengers = passengers.size();
        int cnfAvail = availableSeats.size();
        int cnfCnt = Math.min(cnfAvail, totalPassengers);
        int racAvail = waitingListRepository.getAvailableRac(trainId);
        int wlAvail = waitingListRepository.getAvailableWl(trainId);
        if (passengers.size() > (cnfAvail + racAvail + wlAvail)) throw new SeatNotFoundException("Regret No more Seats in this train");

        String pnr = generatePNR();

        for (int i=0; i<cnfCnt; i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);
             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
             passengerRepository.addPassenger(cnfPassenger);
         }

         if (cnfAvail < totalPassengers) {
             updateWaitingQueue(cnfCnt, passengers, trainId, pnr, racAvail, wlAvail);
         }
         Booking currentBooking = new Booking(pnr, from, to, trainId, SessionStorage.getCurrentUser().getUserId(), BookingStatus.ACTIVE);
         bookingRepository.addBooking(currentBooking);

         List<PassengerResponseDto> addedPassengers = gatherPassengerDetails(pnr, trainId);
         return new BookingResponseDto(pnr, from, to, trainRepository.getTrainById(trainId), addedPassengers.size(), addedPassengers, currentBooking.getStatus());
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.getBookingByUser(SessionStorage.getCurrentUser().getUserId());
    }

    public BookingResponseDto getBookingDetails(String pnr) {
        Booking curBooking = bookingRepository.getBookingById(pnr);
        if (curBooking == null) throw new ItemNotFoundException("No bookings found for this Account");
        Train curTrain = trainRepository.getTrainById(curBooking.getTrainId());
        if (curTrain == null) throw new ItemNotFoundException("No Train found for this trainId"+curBooking.getTrainId());
        List<PassengerResponseDto> curPassengers = gatherPassengerDetails(pnr, curTrain.getTrainId());
        return new BookingResponseDto(pnr, curBooking.getFrom(), curBooking.getTo(), curTrain, curPassengers.size(), curPassengers, curBooking.getStatus());
    }

    private void updateWaitingQueue(int index, List<PassengerRequestDto> passengers, int trainId, String pnr, int racAvail, int wlAvail) {

        for (int i = index; i < passengers.size(); i++) {
            PassengerRequestDto curPassengerReq = passengers.get(i);
            Passenger curPassenger;
            if (racAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.RAC, pnr,-1);
                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingListRepository.addRac(trainId, curPassenger.getPassengerId());
                racAvail--;
            } else if(wlAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.WL, pnr, -1);
                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingListRepository.addWl(trainId, curPassenger.getPassengerId());
                wlAvail--;
            } else {
                throw new SeatNotFoundException("Unexpected Error Occurs");
            }

        }
    }
    public List<PassengerResponseDto> gatherPassengerDetails(String pnr, int trainId) {
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getStatus().equals(PassengerStatus.CANCELLED)) continue;
            int seatNo;
            if (p.getSeatId() != -1){
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
    private String generatePNR() {
        long basePnr = 4000000000L;
        return String.valueOf(basePnr++);
    }
}
