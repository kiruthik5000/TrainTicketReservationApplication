package service;

import dto.*;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.*;
import repository.*;
import utils.SessionStorage;

import java.util.*;

public class BookingService {

    private final TrainRepository trainRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final WaitingListRepository waitingListRepository;
    private final StationRepository stationRepository;

    public BookingService(TrainRepository trainRepository,
                          SeatRepository seatRepository,
                          BookingRepository bookingRepository,
                          PassengerRepository passengerRepository,
                          WaitingListRepository waitingListRepository,
                          StationRepository stationRepository) {
        this.trainRepository = trainRepository;
        this.seatRepository = seatRepository;
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.waitingListRepository = waitingListRepository;
        this.stationRepository = stationRepository;
    }

    public List<TrainResponseDto> getAllTrains(Station from, Station to) throws DataBaseException {
        List<Integer> trainIds = findTrains(from, to);
        if (trainIds.isEmpty()) throw new ItemNotFoundException("No Trains Operating through this route");
        List<TrainResponseDto> trainResponse = new ArrayList<>();
        for (int id : trainIds) {
            Train curTrain = trainRepository.getTrainById(id);
            trainResponse.add(new TrainResponseDto(curTrain, getAvailableSeats(id, from, to), from, to));
        }
        return trainResponse;
    }

    public List<SeatAvailabilityDto> getAvailableSeats(int trainId, Station from, Station to) throws DataBaseException {
        List<SeatAvailabilityDto> availabilityDtos = new ArrayList<>();
        for (SeatType e : new SeatType[]{SeatType.SLEEPER, SeatType.AC}) {
            availabilityDtos.add(getAvailableSeats(trainId, e, from, to));
        }
        return availabilityDtos;
    }

    public BookingResponseDto bookTickets(int trainId, SeatType sc, Station from, Station to, List<PassengerRequestDto> passengers) throws DataBaseException {

        if (passengers == null || passengers.isEmpty()) throw new InvalidInputException("No Passengers Provided");


        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId, sc, from.getSequenceNo(), to.getSequenceNo());
        int totalPassengers = passengers.size();
        int cnfAvail = availableSeats.size();
        int cnfCnt = Math.min(cnfAvail, totalPassengers);

        int racAvail = waitingListRepository.getAvailableRac(trainId, sc);
        int wlAvail = waitingListRepository.getAvailableWl(trainId, sc);

        int currentCanBook = cnfAvail + racAvail + wlAvail;
        if (passengers.size() > currentCanBook) throw new SeatNotFoundException("Only "+currentCanBook+" Seats left please Enter "+currentCanBook+" passengers.");

        String pnr = generatePNR();
        Booking currentBooking = new Booking(0, pnr, from.getStationCode(), to.getStationCode(), trainId, SessionStorage.getCurrentUser().getUserId(), BookingStatus.ACTIVE, sc);
        bookingRepository.addBooking(currentBooking);

        List<Passenger> selectedPassengers = new ArrayList<>();
        List<SeatUpdateRequestDto> seatUpdateRequestDtos = new ArrayList<>();
        boolean[] curBooking = new boolean[3];
        for (int i=from.getSequenceNo(); i<to.getSequenceNo(); i++) {
            curBooking[i - 1] = true;
        }
        for (int i=0; i<cnfCnt; i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);

//             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
             seatUpdateRequestDtos.add(new SeatUpdateRequestDto(curSeat.getSeatId(), curBooking.clone()));
//             passengerRepository.addPassenger(cnfPassenger);
             selectedPassengers.add(cnfPassenger);
         }
        if (!seatUpdateRequestDtos.isEmpty()) {
            seatRepository.updateAllSeatStatus(seatUpdateRequestDtos, SeatStatus.BOOKED);
        }
         if (cnfAvail < totalPassengers) {
             List<Passenger> wlPassengers = updateWaitingQueue(cnfCnt, passengers, pnr, racAvail, wlAvail);
             selectedPassengers.addAll(wlPassengers);
         }
        if (!selectedPassengers.isEmpty()) {
            passengerRepository.addAllPassenger(selectedPassengers);
        }

        List<Passenger> racPassengers = selectedPassengers.stream().filter(k->k.getStatus() == PassengerStatus.RAC).toList();
        List<Passenger> wlPassengers = selectedPassengers.stream().filter(k->k.getStatus() == PassengerStatus.WL).toList();

        if (!racPassengers.isEmpty()) {
            waitingListRepository.addAllRac(trainId, sc, racPassengers);
        }
        if (!wlPassengers.isEmpty()) {
            waitingListRepository.addAllWl(trainId, sc, wlPassengers);
        }

         List<PassengerResponseDto> addedPassengers = gatherPassengerDetails(currentBooking, trainId);
         return new BookingResponseDto(pnr, trainRepository.getTrainById(trainId), addedPassengers.size(), addedPassengers, currentBooking.getStatus(), sc);
    }

    public List<Booking> getAllBookings() throws DataBaseException {
        return bookingRepository.getBookingByUser(SessionStorage.getCurrentUser().getUserId());
    }

    public BookingResponseDto getBookingDetails(String pnr) throws DataBaseException {
        Booking curBooking = bookingRepository.getBookingByPnr(pnr);
        if (curBooking == null) throw new ItemNotFoundException("No bookings found for this Account");
//        if (curBooking.getStatus().equals(BookingStatus.CANCELLED)) throw new IllegalArgumentException("This booking is canceled");
        Train curTrain = trainRepository.getTrainById(curBooking.getTrainId());
        if (curTrain == null) throw new ItemNotFoundException("No Train found for this trainId"+curBooking.getTrainId());
        List<PassengerResponseDto> curPassengers = gatherPassengerDetails(curBooking, curTrain.getTrainId());
        return new BookingResponseDto(pnr, curTrain, curPassengers.size(), curPassengers, curBooking.getStatus(), curBooking.getSeatType());
    }

    private List<Passenger> updateWaitingQueue(int index, List<PassengerRequestDto> passengers, String pnr, int racAvail, int wlAvail) {

        List<Passenger> waitingPassengers = new ArrayList<>();

        for (int i = index; i < passengers.size(); i++) {
            PassengerRequestDto curPassengerReq = passengers.get(i);
            Passenger curPassenger;
            if (racAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.RAC, pnr, -1);
//                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingPassengers.add(curPassenger);
//                waitingListRepository.addRac(trainId, curPassenger);
                racAvail--;
            } else if (wlAvail > 0) {
                curPassenger = new Passenger(0, curPassengerReq.getName(), curPassengerReq.getAge(), PassengerStatus.WL, pnr, -1);
//                curPassenger = passengerRepository.addPassenger(curPassenger);
                waitingPassengers.add(curPassenger);
//                waitingListRepository.addWl(trainId, curPassenger);
                wlAvail--;
            } else {
                throw new SeatNotFoundException("Unexpected Error Occurs");
            }
        }

       return waitingPassengers;
    }
    public List<PassengerResponseDto> gatherPassengerDetails(Booking booking, int trainId) throws DataBaseException {
        List<Passenger> passengers = passengerRepository.getPassengerByBookingPnr(booking.getPnr());
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();
        for (Passenger p : passengers) {
//            System.out.println("current passenger status"+p.getStatus());
//            if (p.getStatus() == PassengerStatus.CANCELLED) continue;
            int seatNo;
            if (p.getStatus() == PassengerStatus.CNF){
                seatNo = seatRepository.getSeatById(p.getSeatId()).getSeatNo();
            } else {
                seatNo = waitingListRepository.getRacNo(trainId, booking.getSeatType(),p.getPassengerId());
                if (seatNo == -1) {
                    seatNo = waitingListRepository.getWlNo(trainId, booking.getSeatType(), p.getPassengerId());
                }
            }
            passengerResponseDtos.add(
                    new PassengerResponseDto(p.getPassengerId(), p.getName(), p.getAge(), p.getStatus(), seatNo, booking.getFrom(), booking.getTo())
            );
        }
        return passengerResponseDtos;
    }
    private String generatePNR() throws DataBaseException {
        long pnr = bookingRepository.getTotalNoRows() + 4000000000L;
        return String.valueOf(pnr);
    }

    public List<Booking> getActiveBookings() throws DataBaseException {
        List<Booking> bookingList =  bookingRepository.getActiveBookings(SessionStorage.getCurrentUser().getUserId());
        if (bookingList.isEmpty()) throw new DataBaseException("No Active Bookings Found");
        return bookingList;
    }

    public Station getStation(String stationCode) {
        return stationRepository.getStationByCode(stationCode);
    }

    private List<Integer> findTrains(Station from, Station to) {
        List<Integer> result = new ArrayList<>();
        if (from == null || to == null) return result;

        Map<Integer, Integer> fromSeq = stationRepository.getTrainSequences(from);
        Map<Integer, Integer> toSeq = stationRepository.getTrainSequences(to);
        if (fromSeq == null || toSeq == null) return result;

        for (Map.Entry<Integer, Integer> e : fromSeq.entrySet()) {
            Integer toNo = toSeq.get(e.getKey());
            if (toNo != null && e.getValue() < toNo) {
                result.add(e.getKey());
            }
        }
        return result;
    }
    public SeatAvailabilityDto getAvailableSeats(int trainId, SeatType sc, Station from, Station to) throws DataBaseException {
        int cnf = seatRepository.getAvailableSeats(trainId, sc, from.getSequenceNo(), to.getSequenceNo()).size();
        int rac = waitingListRepository.getAvailableRac(trainId, sc);
        int wl  = waitingListRepository.getAvailableWl(trainId, sc);

        if (cnf > 0) return new SeatAvailabilityDto(sc, "CNF", cnf);
        if (rac > 0) return new SeatAvailabilityDto(sc,"RAC", rac);
        if (wl  > 0) return new SeatAvailabilityDto(sc,"WL", wl);
        if (rac == -1 && wl == -1) return null;        // train doesn't offer this class
        return new SeatAvailabilityDto(sc,"REGRET", -1);
    }
}
