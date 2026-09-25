package ui;

import dto.BookingResponseDto;
import dto.PassengerResponseDto;
import exception.InvalidInputException;
import model.Booking;
import model.BookingStatus;
import service.BookingService;
import service.CancellationService;
import utils.InputHandler;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CancellationUi {
    private final CancellationService cancellationService;
    private final BookingService bookingService;
    public CancellationUi(CancellationService cancellationService, BookingService bookingService) {
        this.cancellationService = cancellationService;
        this.bookingService = bookingService;
    }

    public void cancelTickets() throws NoSuchFieldException {
        List<Booking> bookingList = bookingService.getAllBookings();
        for (int i=0; i<bookingList.size(); i++) {
            Booking curBooking = bookingList.get(i);
            if (curBooking.getStatus().equals(BookingStatus.ACTIVE)) {
                System.out.println((i + 1) + ". pnr: " + curBooking.getBookingId() + "\t" + curBooking.getFrom() + " - " + curBooking.getTo() + "\t" + curBooking.getStatus());
            }
        }
        int choice = InputHandler.getNumericValue("Choice of Booking", bookingList.size());
        Booking selectedBooking = bookingList.get(choice - 1);
        BookingResponseDto dto = bookingService.getBookingDetails(selectedBooking.getBookingId());
        System.out.println(dto);
        System.out.println();
        System.out.println("1. Cancel Entire Booking");
        System.out.println("2. Cancel Partial Booking");
        System.out.println("3. return");
        System.out.println();
        choice = InputHandler.getNumericValue("Choice", 3);
        if (choice == 1) {
            if (cancellationService.cancelFullBooking(selectedBooking.getBookingId())) {
            System.out.println("Booking cancelled successfully.");
        } else {
            System.out.println("Unexpected Error in cancellation.");
        }
        }
        if (choice == 2) {
            Set<Integer> selectedPassengers = gatherPassengersNeedToRemove(selectedBooking.getBookingId(), selectedBooking.getTrainId());

            if (selectedPassengers.isEmpty()) {
                System.out.println("No passengers selected.");
                return;
            }
            if (cancellationService.partialCancellation(selectedBooking.getBookingId(), selectedPassengers)) {
                System.out.println("Selected passengers are cancelled successfully.");
            } else {
                System.out.println("Unexpected Error in cancellation");
            }
        }

        if (choice == 3)return;
    }

    private Set<Integer> gatherPassengersNeedToRemove(String pnr, int trainId) {
        List<PassengerResponseDto> passengers = bookingService.gatherPassengerDetails(pnr, trainId);
        System.out.println("\nPassengers:");
        for (int i=0; i<passengers.size(); i++) {
            System.out.println((i + 1)+". "+passengers.get(i));
        }
        Set<Integer> passengerIndex = getAllPassengerIndex(passengers.size());
        System.out.println("Selected passengers: ");
        for (int i : passengerIndex) {
            if (i >= 0 && i < passengers.size()) {
                System.out.println(passengers.get(i));
            }
        }
        return passengerIndex;
    }

    private Set<Integer> getAllPassengerIndex(int limit) {
        String input = InputHandler.getStringValue("Index of Passengers separated by comma ','");
        Set<Integer> selected = new HashSet<>();
        String[] indexes = input.split(",");

        for (String v : indexes) {
            try {
                int index = Integer.parseInt(v.trim());
                if (index < 1 || index > limit) throw new InvalidInputException("Invalid Passenger Index");
                System.out.println("stored index");
                System.out.println(index);
                selected.add(index - 1);
            } catch (NumberFormatException e) { System.out.println( "Invalid input: " + v ); }
        }
        return selected;
    }
}
