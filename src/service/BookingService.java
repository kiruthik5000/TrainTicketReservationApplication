package service;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import model.*;
import repository.*;

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
        int racNo = waitingListRepository.getRACNo(trainId);
        if (racNo != -1) return "RAC "+racNo;
        int wlNo = waitingListRepository.getWLNo(trainId);
        if (wlNo != -1) return "WL "+wlNo;
        return "Regret No More booking";
    }

    public BookingResponseDto bookTickets(int trainId, String from, String to, List<PassengerRequestDto> passengers) {
        String pnr = generatePNR();
         List<Seat> availableSeats = seatRepository.getAvailableSeats(trainId);
         List<Passenger> cnfPassengers = new ArrayList<>();
         for (int i=0; i<availableSeats.size(); i++) {
             PassengerRequestDto curPassenger = passengers.get(i);
             Seat curSeat = availableSeats.get(i);
             seatRepository.updateStatus(curSeat.getSeatId(), SeatStatus.BOOKED);
             Passenger cnfPassenger = new Passenger(0, curPassenger.getName(), curPassenger.getAge(), PassengerStatus.CNF, pnr, curSeat.getSeatId());
             passengerRepository.addPassenger(cnfPassenger);
         }
         if (availableSeats.size() < passengers.size()) {
             updateWaitingQueue(availableSeats.size(), passengers);
         }
         return new BookingResponseDto(pnr, from, to, trainRepository.getTrainById(trainId), passengers.size());
    }

    private void updateWaitingQueue(int )

    public static String generatePNR() {
        StringBuilder sb = new StringBuilder();
        sb.append(4);
        Random rand = new Random();
        for (int i=0; i<9; i++) {
            int nextChar;
            if (i < 4) {
                nextChar = rand.nextInt(5, 10);
            } else {
                nextChar = rand.nextInt(0, 10);
            }
            sb.append(nextChar);
        }
        return sb.toString();
    }
}
