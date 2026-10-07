package ui;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.Booking;
import model.Train;
import service.BookingService;
import utils.InputHandler;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;

public class BookingUi {
    private final BookingService bookingService;

    public BookingUi(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void bookTickets() throws Exception {
        if (!SessionStorage.userIsLoggedIn()) return;
        String from = InputHandler.getStringValue("Departure Station code");
        String to = InputHandler.getStringValue("Arrival Station code");
        System.out.println();

        List<Train> trainList = bookingService.getAllTrains();

        if (trainList == null || trainList.isEmpty()) throw new ItemNotFoundException("No trains found for this schedule");

        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1)+". "+trainList.get(i));
        }

        System.out.println();
        int choice = InputHandler.getNumericValue("Train Index", trainList.size());
        Train selectedTrain = trainList.get(choice - 1);

        String seatNo = bookingService.getAvailableSeats(selectedTrain.getTrainId());
        if (seatNo.equals("Regret No More booking")) throw new SeatNotFoundException("Regret No More booking");

        while (true) {
            try {
                System.out.println("------------------------");
                System.out.println(selectedTrain);
                System.out.println("Seats: "+seatNo);

                List<PassengerRequestDto> passengersList = gatherPassengers();

                if (passengersList == null || passengersList.isEmpty()) return;

                System.out.println("Selected " + passengersList.size() + " Passengers");
                for (PassengerRequestDto p : passengersList) {
                    System.out.println(p);
                }
                BookingResponseDto responseDto = bookingService.bookTickets(selectedTrain.getTrainId(), from, to, passengersList);
                System.out.println(responseDto);
                break;
            }catch (InvalidInputException | SeatNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void showBookings() throws DataBaseException {
        if(!SessionStorage.userIsLoggedIn()) return;
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings == null || bookings.isEmpty()) throw new ItemNotFoundException("No bookings found for your account");
        for (int i=0; i<bookings.size(); i++) {
            Booking curBooking = bookings.get(i);
            System.out.println((i + 1)+". pnr: "+curBooking.getPnr()+"\t"+curBooking.getFrom()+" - "+curBooking.getTo()+"\t"+curBooking.getStatus());
        }
        System.out.println();
        int choice = InputHandler.getNumericValue("Booking Index to show passenger details", bookings.size());
        BookingResponseDto selectedBooking = bookingService.getBookingDetails(bookings.get(choice - 1).getPnr());
        System.out.println(selectedBooking);
    }

    private List<PassengerRequestDto> gatherPassengers() {
        List<PassengerRequestDto> passengerList = new ArrayList<>();

        System.out.println("Enter Passenger details (upto 6)");

        while (passengerList.size() < 6) {
            System.out.println("1. Add new passenger");
            System.out.println("2. Submit");
            System.out.println("3. Back to Main Menu");

            int choice = InputHandler.getNumericValue("choice", 3);
            if (choice == 1) {
                String name = InputHandler.getStringValue("name");
                int age = InputHandler.getNumericValue("age", 100);
                passengerList.add(new PassengerRequestDto(name, age));
            }
            if (choice == 2) {
                if (passengerList.isEmpty()) {
                    System.out.println("Add at least one passenger.");
                    continue;
                }
                return passengerList;
            }
            if (choice == 3) return null;
        }
        System.out.println("Only 6 passengers can book in 1 Booking");
        return passengerList;
    }
}
