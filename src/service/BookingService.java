package service;

import dto.*;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.*;
import repository.*;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
//        if (from.getSequenceNo() >= to.getSequenceNo()) throw new IllegalArgumentException("No Trains Operating through this route");
//        List<Train> trains = trainRepository.getAllTrains();
        List<Integer> trainIds = findTrains(from, to);
        if (trainIds.isEmpty()) throw new IllegalArgumentException("No Trains Operating through this route");
        List<TrainResponseDto> trainResponse = new ArrayList<>();
        for (int id : trainIds) {
            Train curTrain = trainRepository.getTrainById(id);
            trainResponse.add(new TrainResponseDto(curTrain, getAvailableSeats(id, from, to)));
        }
        return trainResponse;
    }

    public SeatAvailabilityDto getAvailableSeats(int trainId, Station from, Station to) throws DataBaseException {
        int mask = calculateMask(from.getSequenceNo(), to.getSequenceNo());
        int cnfSeats = seatRepository.getAvailableSeats(trainId, mask).size();
        if (cnfSeats > 0) return new SeatAvailabilityDto("CNF", cnfSeats);
        int racNo = waitingListRepository.getAvailableRac(trainId);
        if (racNo > 0) return new SeatAvailabilityDto("RAC ", racNo);
        int wlNo = waitingListRepository.getAvailableWl(trainId);
        if (wlNo > 0) return new SeatAvailabilityDto("WL ", wlNo);
        return new SeatAvailabilityDto("REGRET", -1);
    }

    public BookingResponseDto bookTickets(int trainId, Station from, Station to, List<PassengerRequestDto> passengers) throws DataBaseException {

        if (passengers == null || passengers.isEmpty()) throw new InvalidInputException("No Passengers Provided");

        int requiredMask = calculateMask(from.getSequenceNo(), to.getSequenceNo());

        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId, requiredMask);
        int totalPassengers = passengers.size();
        int cnfAvail = availableSeats.size();
        int cnfCnt = Math.min(cnfAvail, totalPassengers);

        int racAvail = waitingListRepository.getAvailableRac(trainId);
        int wlAvail = waitingListRepository.getAvailableWl(trainId);

        int currentCanBook = cnfAvail + racAvail + wlAvail;
        if (passengers.size() > currentCanBook) throw new SeatNotFoundException("Only "+currentCanBook+" Seats left please Enter "+currentCanBook+" passengers.");

        String pnr = generatePNR();
        Booking currentBooking = new Booking(0, pnr, from.getStationCode(), to.getStationCode(), trainId, SessionStorage.getCurrentUser().getUserId(), BookingStatus.ACTIVE);
        bookingRepository.addBooking(currentBooking);

        List<Passenger> selectedPassengers = new ArrayList<>();
        List<SeatUpdateRequestDto> seatUpdateRequestDtos = new ArrayList<>();

        for (int i=0; i<cnfCnt; i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);
             int newMask = curSeat.getMask() ^ requiredMask;
//             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
             seatUpdateRequestDtos.add(new SeatUpdateRequestDto(curSeat.getSeatId(), newMask));
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
            waitingListRepository.addAllRac(trainId, racPassengers);
        }
        if (!wlPassengers.isEmpty()) {
            waitingListRepository.addAllWl(trainId, wlPassengers);
        }

         List<PassengerResponseDto> addedPassengers = gatherPassengerDetails(currentBooking, trainId);
         return new BookingResponseDto(pnr, from.getStationCode(), to.getStationCode(), trainRepository.getTrainById(trainId), addedPassengers.size(), addedPassengers, currentBooking.getStatus());
    }

    public List<Booking> getAllBookings() throws DataBaseException {
        return bookingRepository.getBookingByUser(SessionStorage.getCurrentUser().getUserId());
    }

    public BookingResponseDto getBookingDetails(String pnr) throws DataBaseException {
        Booking curBooking = bookingRepository.getBookingByPnr(pnr);
        if (curBooking == null) throw new ItemNotFoundException("No bookings found for this Account");
        if (curBooking.getStatus().equals(BookingStatus.CANCELLED)) throw new IllegalArgumentException("This booking is cancelled");
        Train curTrain = trainRepository.getTrainById(curBooking.getTrainId());
        if (curTrain == null) throw new ItemNotFoundException("No Train found for this trainId"+curBooking.getTrainId());
        List<PassengerResponseDto> curPassengers = gatherPassengerDetails(curBooking, curTrain.getTrainId());
        return new BookingResponseDto(pnr, curBooking.getFrom(), curBooking.getTo(), curTrain, curPassengers.size(), curPassengers, curBooking.getStatus());
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

    private int calculateMask(int fromSeq, int toSeq) {
        long mask = 0L;
        for (int i=fromSeq; i<toSeq; i++) {
            mask |= (1L << (i - 1));
        }
        return (int) mask;
    }

    private List<Integer> findTrains(Station from, Station to) {
        List<Integer> result = new ArrayList<>();
        if (from == null || to == null) return result;

        Map<Integer, Integer> fromSeq = stationRepository.getTrainSequences(from);
        Map<Integer, Integer> toSeq = stationRepository.getTrainSequences(to);
        if (fromSeq == null || toSeq == null) return result;

        for (Map.Entry<Integer, Integer> e : fromSeq.entrySet()) {
            Integer toNo = toSeq.get(e.getKey());             // same train stops at 'to'?
            if (toNo != null && e.getValue() < toNo) {        // and in the right direction
                result.add(e.getKey());
            }
        }
        return result;
    }
}
