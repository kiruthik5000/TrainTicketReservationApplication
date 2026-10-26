package service;

import dto.PassengerResponseDto;
import exception.DataBaseException;
import model.*;
import repository.*;

import java.util.*;

public class AdminService {
    private final TrainRepository trainRepository;
    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final SeatRepository seatRepository;
    private final WaitingListRepository waitingListRepository;

    private final Map<SeatType, List<PassengerResponseDto>> passengerResponseDtoCache;

    public AdminService(TrainRepository trainRepository, BookingRepository bookingRepository, PassengerRepository passengerRepository, SeatRepository seatRepository, WaitingListRepository waitingListRepository) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.seatRepository = seatRepository;
        this.trainRepository = trainRepository;
        this.waitingListRepository = waitingListRepository;
        this.passengerResponseDtoCache = new HashMap<>();
    }

    public List<Train> getAllTrains() throws DataBaseException {
        return trainRepository.getAllTrains();
    }

    public void createCache(int trainId) throws DataBaseException {
        for (SeatType type: SeatType.values()) {
            List<Booking> bookings = bookingRepository.getAllBookingsByTrainId(trainId);
            List<Passenger> passengers = new ArrayList<>();
            for (Booking b : bookings) {
                if (b.getSeatType().equals(type)) {
                    passengers.addAll(passengerRepository.getPassengerByBookingPnr(b.getPnr()));
                }
            }
            List<PassengerResponseDto> passengerResponse = new ArrayList<>();
            for (Passenger p : passengers) {
                Booking booking = bookingRepository.getBookingByPnr(p.getPnr());
                if (p.getStatus().equals(PassengerStatus.CANCELLED)) continue;
                int seatNo;
                if (p.getStatus().equals(PassengerStatus.CNF)) {
                    seatNo = seatRepository.getSeatById(p.getSeatId()).getSeatNo();
                } else if (p.getStatus().equals(PassengerStatus.RAC)) {
                    seatNo = waitingListRepository.getRacNo(trainId, type, p.getPassengerId());
                } else {
                    seatNo = waitingListRepository.getWlNo(trainId, type, p.getPassengerId());
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
            passengerResponseDtoCache.computeIfAbsent(type, k -> new ArrayList<>()).addAll(passengerResponse);
        }
    }

    public List<PassengerResponseDto> showAllPassengersInTrain(SeatType seatType, int trainId, int window) throws DataBaseException {
        if (passengerResponseDtoCache.isEmpty()) {
            createCache(trainId);
        }
        int start = Math.min(passengerResponseDtoCache.get(seatType).size(), (window * 5));
        int end = Math.min(passengerResponseDtoCache.get(seatType).size(), ((window + 1) * 5) );
        if (end - start >= 1) {
            return new ArrayList<>(passengerResponseDtoCache.get(seatType).subList(start, end));
        }
        return null;
    }
}
