package ui;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import exception.ItemNotFoundException;
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

    public void bookTickets() {
        if (!SessionStorage.userIsLogin()) return;
        String from = InputHandler.getStringValue("Departure Station code");
        String to = InputHandler.getStringValue("Arrival Station code");
        System.out.println();
        List<Train> trainList = bookingService.getAllTrains();
        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1)+". "+trainList.get(i));
        }
        System.out.println();
        int choice = InputHandler.getNumericValue("Train Index", trainList.size());
        Train selectedTrain = trainList.get(choice - 1);
        System.out.println("------------------------");
        System.out.println(selectedTrain);
        System.out.println("Seats: "+bookingService.getAvailableSeats(selectedTrain.getTrainId()));
        List<PassengerRequestDto> passengersList = gatherPassengers();
        System.out.println("Selected "+passengersList.size()+" of Passengers");
        BookingResponseDto responseDto = bookingService.bookTickets(selectedTrain.getTrainId(), from, to, passengersList);
        System.out.println(responseDto);
    }

    public void showBookings() {
        if(!SessionStorage.userIsLogin()) return;
        List<Booking> bookings = bookingService.getAllBookings();
        if (bookings == null || bookings.isEmpty()) throw new ItemNotFoundException("No bookings found for your account");
        for (int i=0; i<bookings.size(); i++) {
            Booking curBooking = bookings.get(i);
            System.out.println((i + 1)+". pnr: "+curBooking.getBookingId()+"\t"+curBooking.getFrom()+" - "+curBooking.getTo()+"\t"+curBooking.getStatus());
        }
        System.out.println();
        int choice = InputHandler.getNumericValue("Booking Index", bookings.size());
        BookingResponseDto selectedBooking = bookingService.getBookingDetails(bookings.get(choice - 1).getBookingId());
        System.out.println(selectedBooking);
    }

    private List<PassengerRequestDto> gatherPassengers() {
        List<PassengerRequestDto> passengerList = new ArrayList<>();
        int i = 6;
        while (i -- > 0) {
            System.out.println("1. Add new passenger");
            System.out.println("2. return");

            int choice = InputHandler.getNumericValue("choice", 2);
            if (choice == 1) {
                String name = InputHandler.getStringValue("name");
                int age = InputHandler.getNumericValue("age", 100);
                passengerList.add(new PassengerRequestDto(name, age));
            } else break;
        }
        return passengerList;
    }
}
