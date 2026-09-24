package ui;

import dto.BookingResponseDto;
import dto.PassengerRequestDto;
import exception.InvalidInputException;
import model.Train;
import service.BookingService;
import utils.InputHandler;

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
        int choice;
        while (true) {
            try {
                choice = InputHandler.getNumericValue("Train Index");
                if (choice <= 0 || choice > trainList.size()) throw new InvalidInputException("Invalid choice");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        Train selectedTrain = trainList.get(choice - 1);
        System.out.println("------------------------");
        System.out.println(selectedTrain);
        System.out.println("Seats: "+bookingService.getAvailableSeats(selectedTrain.getTrainId()));
        System.out.println("1. Select passengers");
        while (true) {
            try {
                choice = InputHandler.getNumericValue("Choice");
                if (choice != 1) throw new InvalidInputException("Invalid Choice");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        List<PassengerRequestDto> passengersList = gatherPassengers();
        System.out.println("Selected "+passengersList.size()+" of Passengers");
        BookingResponseDto responseDto = bookingService.bookTickets(selectedTrain.getTrainId(), from, to, passengersList);
        System.out.println(responseDto);
    }

    private List<PassengerRequestDto> gatherPassengers() throws Exception {
        List<PassengerRequestDto> passengerList = new ArrayList<>();
        int i = 6;
        while (i -- > 0) {
            System.out.println("1. Add new passenger");
            System.out.println("2. return");

            int choice = InputHandler.getNumericValue("choice");
            if (choice == 1) {
                String name = InputHandler.getStringValue("name");
                int age = InputHandler.getNumericValue("age");
                passengerList.add(new PassengerRequestDto(name, age));
            } else break;
        }
        return passengerList;
    }
}
