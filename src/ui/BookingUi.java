package ui;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import model.Booking;
import model.Train;
import service.BookingService;
import utils.InputHandler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingUi {
    private final BookingService bookingService;

    public BookingUi(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void bookTickets() throws Exception {
        String from = InputHandler.getStringValue("Departure Station code");
        String to = InputHandler.getStringValue("Arrival Station code");
        List<Train> trainList = bookingService.getAllTrains();
        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1)+". "+trainList.get(i));
        }
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

    public void showBookings() throws Exception {
        List<Booking> bookings = bookingService.getAllBookings();
        for (int i=0; i<bookings.size(); i++) {
            Booking curBooking = bookings.get(i);
            System.out.println((i + 1)+". pnr: "+curBooking.getBookingId()+"\t"+curBooking.getFrom()+" - "+curBooking.getTo()+"\t"+LocalDate.now());
        }
        int choice = InputHandler.getNumericValue("Booking Index", bookings.size());
        BookingResponseDto selectedBooking = bookingService.getBookingDetails(bookings.get(choice - 1).getBookingId());
        System.out.println(selectedBooking);
    }

    private List<PassengerRequestDto> gatherPassengers() throws Exception {
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
