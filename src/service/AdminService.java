package service;

import dto.PassengerResponseDto;
import model.*;
import repository.*;

import java.util.ArrayList;
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

    public List<Train> getAllTrains() {
        return trainRepository.getAllTrains();
    }

    public List<PassengerResponseDto> showAllPassengersInTrain(int trainId) {
        List<Booking> bookings = bookingRepository.getAllBookingsByTrainId(trainId);
        List<Passenger> passengers = new ArrayList<>();
        for (Booking b : bookings) {
            passengers.addAll(passengerRepository.getPassengerByBooking(b.getBookingId()));
        }
        List<PassengerResponseDto> passengerResponse = new ArrayList<>();
        for (Passenger p : passengers) {
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
                            seatNo
                            )
            );
        }
        return passengerResponse;
    }
}
