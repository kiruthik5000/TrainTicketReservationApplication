package repository.Implementation.InMemory;

import dto.PassengerSeatIdDto;
import model.Passenger;
import model.PassengerStatus;
import repository.PassengerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPassengerRepository implements PassengerRepository {
    private int nextPassengerId = 1;
    private final Map<Integer, Passenger> passengerMap;

    public InMemoryPassengerRepository() {
        passengerMap = new HashMap<>();
        passengerMap.put(1, new Passenger(1, "admin", 25, PassengerStatus.CNF,"123456789", 1));
    }

    public List<Passenger> getPassengerByBookingPnr(String pnr) {
        return passengerMap
                .values()
                .stream()
                .filter(k -> k.getPnr().equals(pnr))
                .toList();
    }

    @Override
    public void addAllPassenger(List<Passenger> passengers) {
        for (Passenger p : passengers) {
            p.setPassengerId(++nextPassengerId);
            passengerMap.put(nextPassengerId, p);
        }
    }

    @Override
    public void updateAllPassengerStatusAndSeatNo(List<PassengerSeatIdDto> passengerSeatIdDtos, PassengerStatus status) {
        for (PassengerSeatIdDto dto : passengerSeatIdDtos) {
            Passenger curPassenger = passengerMap.get(dto.getPassengerId());
            curPassenger.setStatus(status);
            curPassenger.setSeatId(dto.getSeatId());
        }
    }
}
