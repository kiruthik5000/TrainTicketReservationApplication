package repository.Implementation;

import model.Passenger;
import model.PassengerStatus;
import repository.PassengerRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryPassengerRepository implements PassengerRepository {
    private int nextPassengerId = 1;
    private Map<Integer, Passenger> passengerMap;

    public InMemoryPassengerRepository() {
        passengerMap = new HashMap<>();
        passengerMap.put(1, new Passenger(1, "admin", 25, PassengerStatus.CNF, "123456789", 1));
    }

    public List<Passenger> getPassengerByBooking(int bookingId) {
        return passengerMap
                .values()
                .stream()
                .filter(k -> k.getBookingId().equals(bookingId))
                .toList();
    }

    @Override
    public void addPassenger(Passenger passenger) {
        passenger.setPassengerId(++nextPassengerId);
        passengerMap.put(nextPassengerId, passenger);
    }
}
