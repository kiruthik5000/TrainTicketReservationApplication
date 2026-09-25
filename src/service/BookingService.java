package service;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import dto.PassengerResponseDto;
import exception.SeatNotFoundException;
import exception.UnAuthorizedAccessException;
import exception.UserNotFoundException;
import model.*;
import repository.*;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        User currentUser = SessionStorage.getCurrentUser();
        if (currentUser == null) throw new UnAuthorizedAccessException("User must login to book tickets");
        String pnr = generatePNR();
        List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
        for (int i=0; i<Math.min(availableSeats.size(), passengers.size()); i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);
             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
             passengerRepository.addPassenger(cnfPassenger);
         }
         if (availableSeats.size() < passengers.size()) {
             updateWaitingQueue(availableSeats.size(), passengers, trainId, pnr);
         }
         Booking currentBooking = new Booking(pnr, from, to, trainId, currentUser.getUserId(), BookingStatus.ACTIVE);
         bookingRepository.addBooking(currentBooking);
         List<PassengerResponseDto> addedPassengers = gatherPassengerDetails(pnr, trainId);
         return new BookingResponseDto(pnr, from, to, trainRepository.getTrainById(trainId), passengers.size(), addedPassengers, currentBooking.getStatus());
    }

    public List<Booking> getAllBookings() {
        User currentUser = SessionStorage.getCurrentUser();
        if (currentUser == null) throw new UserNotFoundException("User must login to view their bookings");
        return bookingRepository.getBookingByUser(currentUser.getUserId());
    }

    public BookingResponseDto getBookingDetails(String pnr) {
        Booking curBooking = bookingRepository.getBookingById(pnr);
        Train curTrain = trainRepository.getTrainById(curBooking.getTrainId());
        List<PassengerResponseDto> curPassengers = gatherPassengerDetails(pnr, curTrain.getTrainId());
        return new BookingResponseDto(pnr, curBooking.getFrom(), curBooking.getTo(), curTrain, curPassengers.size(), curPassengers, curBooking.getStatus());
    }

    private void updateWaitingQueue(int index, List<PassengerRequestDto> passengers, int trainId, String pnr) {
        int racAvail = waitingListRepository.getAvailableRac(trainId);
        int wlAvail = waitingListRepository.getAvailableWl(trainId);
        int waitingAvailable = racAvail + wlAvail;
        if (passengers.size() - index > waitingAvailable) throw new SeatNotFoundException("No seats available for booking");
        for (int i = index; i < Math.min(passengers.size(), waitingAvailable); i++) {
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
                throw new SeatNotFoundException("Seats are full");
            }

        }
    }
    public List<PassengerResponseDto> gatherPassengerDetails(String pnr, int trainId) {
        List<Passenger> passengers = passengerRepository.getPassengerByBooking(pnr);
        List<PassengerResponseDto> passengerResponseDtos = new ArrayList<>();
        for (Passenger p : passengers) {
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
        while (true) {
            StringBuilder sb = new StringBuilder();
            sb.append(4);
            Random rand = new Random();
            for (int i = 0; i < 9; i++) {
                int nextChar;
                if (i < 4) {
                    nextChar = rand.nextInt(5, 10);
                } else {
                    nextChar = rand.nextInt(0, 10);
                }
                sb.append(nextChar);
            }
            String currentPNR = sb.toString();
            if (!bookingRepository.isMatchPnr(currentPNR)) return currentPNR;
        }
    }
}
