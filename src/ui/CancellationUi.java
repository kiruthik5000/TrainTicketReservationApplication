package ui;

import dto.BookingResponseDto;
import dto.PassengerResponseDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import model.Booking;
import model.PassengerStatus;
import service.BookingService;
import service.CancellationService;
import utils.InputHandler;
import utils.SessionStorage;

import java.util.*;

public class CancellationUi {
    private final CancellationService cancellationService;
    private final BookingService bookingService;

    public CancellationUi(CancellationService cancellationService, BookingService bookingService) {
        this.cancellationService = cancellationService;
        this.bookingService = bookingService;
    }

    public void cancelTickets() throws DataBaseException {
        SessionStorage.userIsLoggedIn();
        List<Booking> bookingList = new ArrayList<>(bookingService.getActiveBookings());
        if (bookingList.isEmpty()) throw new ItemNotFoundException("No Active Bookings Found");
        bookingList.sort(Comparator.comparingInt(Booking::getBookingId).reversed());
        for (int i = 0; i < bookingList.size(); i++) {
            Booking curBooking = bookingList.get(i);
            System.out.println((i + 1) + ". pnr: " + curBooking.getPnr() + "\t" + curBooking.getFrom() + " - " + curBooking.getTo() +"\t"+curBooking.getSeatType()+ "\t" + curBooking.getStatus());
        }
        System.out.println();
        int choice = InputHandler.getNumericValue("Choice of Booking or 0 for Back", bookingList.size());
        if (choice == 0) return;
        Booking selectedBooking = bookingList.get(choice - 1);
        while (true) {
            BookingResponseDto dto = bookingService.getBookingDetails(selectedBooking.getPnr());
            System.out.println();
            System.out.println(dto);
            System.out.println();
            System.out.println("1. Cancel Entire Booking");
            System.out.println("2. Cancel Partial Booking");
            System.out.println("3. return");
            System.out.println();
            choice = InputHandler.getNumericValue("Choice", 3);

            if (choice == 1) {
                while (true) {
                    String cnf = InputHandler.getStringValue("Confirm full cancellation [y / n]");
                    if (cnf.equals("y")) {
                        if (cancellationService.cancelFullBooking(selectedBooking.getPnr())) {
                            System.out.println("Booking cancelled successfully.");
                        } else {
                            System.out.println("Unexpected Error in cancellation.");
                        }
                        return;
                    } else if (cnf.equals("n")) {
                        return;
                    } else {
                        System.out.println("Wrong Input, Try Again !");
                    }
                }
            } else if (choice == 2) {
                Set<Integer> selectedPassengers = gatherPassengersNeedToRemove(selectedBooking, selectedBooking.getTrainId());

                if (selectedPassengers.isEmpty()) {
                    System.out.println("No passengers selected.");
                    return;
                }
                while (true) {
                    String cnf = InputHandler.getStringValue("Confirm partial cancellation [y / n]");
                    if (cnf.equals("y")) {
                        if (cancellationService.partialCancellation(selectedBooking.getPnr(), selectedPassengers)) {
                            System.out.println("Selected passengers are cancelled successfully.");
                        } else {
                            System.out.println("Unexpected Error in cancellation");
                        }
                        return;
                    } else if (cnf.equals("n")) {
                        return;
                    } else {
                        System.out.println("Wrong Input Try Again!");
                    }
                }
            } else if (choice == 3) return;
            else {
                System.out.println("Wrong Input Try Again!");
            }
        }
    }

    private Set<Integer> gatherPassengersNeedToRemove(Booking curBooking, int trainId) throws DataBaseException {
        List<PassengerResponseDto> passengers = new ArrayList<>(bookingService.gatherPassengerDetails(curBooking, trainId))
                .stream()
                .filter(k->!k.getStatus().equals(PassengerStatus.CANCELLED))
                .toList();

        System.out.println("\nPassengers:");
        for (int i=0; i<passengers.size(); i++) {
            System.out.println((i + 1)+". "+passengers.get(i));
        }
        Set<Integer> passengerIndex = getAllPassengerIndex(passengers, passengers.size());
        System.out.println("Selected passengers: ");
        for (PassengerResponseDto p : passengers) {
            if (passengerIndex.contains(p.getpId())) {
                System.out.println(p);
            }
        }
        return passengerIndex;
    }

    private Set<Integer> getAllPassengerIndex(List<PassengerResponseDto> dtos, int limit) {
        while (true) {
            try {
                String input = InputHandler.getStringValue("Index of Passengers separated by comma ','");
                Set<Integer> selected = new HashSet<>();
                String[] indexes = input.split(",");

                for (String v : indexes) {
                    try {
                        int index = Integer.parseInt(v.trim());
                        if (index < 1 || index > limit) throw new InvalidInputException("Invalid Passenger Index");
                        PassengerResponseDto selectedPassenger = dtos.get(index - 1);
                        if (selectedPassenger != null) {
                            selected.add(selectedPassenger.getpId());
                        }
                    } catch (NumberFormatException e) {
                        throw new InvalidInputException(e.getMessage());
                    }
                }
                return selected;
            } catch (Exception e) {
                System.out.println("Error Occurred " + e.getMessage());
            }
        }
    }
}
