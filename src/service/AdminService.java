package service;

import dto.PassengerResponseDto;
import exception.DataBaseException;
import model.*;
import repository.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AdminService {
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;
    private final WaitingListRepository waitingListRepository;

    public AdminService(TrainRepository trainRepository, BookingRepository bookingRepository, PassengerRepository passengerRepository, SeatRepository seatRepository, WaitingListRepository waitingListRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.seatRepository = seatRepository;
        this.trainRepository = trainRepository;
        this.waitingListRepository = waitingListRepository;
    }

    public List<Train> getAllTrains() throws DataBaseException {
        return trainRepository.getAllTrains();
    }

    public List<PassengerResponseDto> showAllPassengersInTrain(int trainId, int window) throws DataBaseException {
        List<Booking> bookings = bookingRepository.getAllBookingsByTrainId(trainId);
        List<Passenger> passengers = new ArrayList<>();
        for (Booking b : bookings) {
            passengers.addAll(passengerRepository.getPassengerByBookingPnr(b.getPnr()));
        }
        List<PassengerResponseDto> passengerResponse = new ArrayList<>();
        for (Passenger p : passengers) {
            Booking booking = bookingRepository.getBookingByPnr(p.getPnr());
            if(p.getStatus().equals(PassengerStatus.CANCELLED)) continue;
            int seatNo;
            if (p.getStatus().equals(PassengerStatus.CNF)) {
                seatNo = seatRepository.getSeatById(p.getSeatId()).getSeatNo();
            } else if (p.getStatus().equals(PassengerStatus.RAC)) {
                seatNo = waitingListRepository.getRacNo(trainId, p.getPassengerId());
            } else {
                seatNo = waitingListRepository.getWlNo(trainId, p.getPassengerId());
            }
            passengerResponse.add(
                    new PassengerResponseDto(
                            p.getPassengerId(),
                            p.getName(),
                            p.getAge(),
                            p.getStatus(),
                            seatNo,
                            booking.getFrom(),
                            booking.getTo()
                            )
            );
        }
        passengerResponse.sort(Comparator.comparing(PassengerResponseDto::getpId));
        int start = Math.min(passengerResponse.size(), (window * 5));
        int end = Math.min(passengerResponse.size(), ((window + 1) * 5) );
        if (end - start >= 1) {
            return new ArrayList<>(passengerResponse.subList(start, end));
        }
        return null;
    }
}
