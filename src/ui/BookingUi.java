package ui;

import dto.BookingResponseDto;
import dto.MultiHopTrainDto;
import dto.PassengerRequestDto;
import dto.TrainResponseDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.ItemNotFoundException;
import exception.SeatNotFoundException;
import model.Booking;
import model.SeatType;
import model.Station;
import model.Train;
import service.BookingService;
import service.TrainService;
import utils.InputHandler;
import utils.SessionStorage;

import java.util.ArrayList;
import java.util.List;

public class BookingUi {
    private final BookingService bookingService;
    private final TrainService trainService;

    public BookingUi(BookingService bookingService, TrainService trainService) {
        this.bookingService = bookingService;
        this.trainService = trainService;
    }

    public void bookTickets() throws Exception {
        if (!SessionStorage.userIsLoggedIn()) return;
        System.out.println();
        Station fromStation = getStation("Departure Station code");
        if (fromStation == null) return;
        Station toStation = getStation("Arrival Station code");
        if (toStation == null) return;

        List<TrainResponseDto> trainList = bookingService.getAllTrains(fromStation, toStation);

        if (trainList == null || trainList.isEmpty()) {
            System.out.println("No Direct trains found for this Route");
            System.out.println("1. Advanced Search");
            System.out.println("2. return to Main Menu");
            System.out.println();

            int option = InputHandler.getNumericValue("Option", 2);
            if (option == 1) {
                MultiHopTrainDto dto = trainService.searchTrains(fromStation.getStationCode(), toStation.getStationCode());
                if (dto == null || dto.toString() == null) {
                    System.out.println("Cannot reach from source to destination");
                    return;
                }
                System.out.println("\n"+dto+"\n");
            }
            return;
        }

        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1)+". "+trainList.get(i));
            System.out.println("-----------------------");
        }
        System.out.println((trainList.size() + 1)+". Back to Main Menu");
        System.out.println();
        int choice = InputHandler.getNumericValue("Train Index", trainList.size() + 1);

        if (choice - 1 == trainList.size()) {
            System.out.println("Returning to Main Menu");
            return;
        }
        TrainResponseDto selectedTrain = trainList.get(choice - 1);
        System.out.println();
        if (selectedTrain.getSeatAvailabilityDto() == null || selectedTrain.getSeatAvailabilityDto().isEmpty() || selectedTrain.getSeatAvailabilityDto().get(0) == null) {
            System.out.println("No Seats Found in this train.");
            return;
        }
        for (int i=0; i<selectedTrain.getSeatAvailabilityDto().size(); i++) {
            System.out.println((i + 1)+". "+selectedTrain.getSeatAvailabilityDto().get(i));
        }
        System.out.println((selectedTrain.getSeatAvailabilityDto().size() + 1)+". Back To Main Menu");
        int seatChoice = InputHandler.getNumericValue("Seat Choice", selectedTrain.getSeatAvailabilityDto().size() + 1);

        if (seatChoice == selectedTrain.getSeatAvailabilityDto().size() + 1) return;
        Train curTrain = selectedTrain.getTrain();
        SeatType seatType = selectedTrain.getSeatAvailabilityDto().get(seatChoice - 1).getSeatType();
        if (selectedTrain.getSeatAvailabilityDto().get(seatChoice - 1).getAvailabilityStatus().equals("REGRET")) throw new SeatNotFoundException("No seats found for this train");

        while (true) {
            try {
                System.out.println("------------------------");
                System.out.println(selectedTrain.onlyTrain());
                System.out.println("\n"+selectedTrain.getSeatAvailabilityDto().get(seatChoice - 1));
                System.out.println();
                List<PassengerRequestDto> passengersList = gatherPassengers();


                if (passengersList == null || passengersList.isEmpty()) return;

                System.out.println("\nSelected " + passengersList.size() + " Passengers");
                for (PassengerRequestDto p : passengersList) {
                    System.out.println(p);
                }
                System.out.println();
                while (true) {
                    double price = bookingService.calculatePrice(seatType, fromStation, toStation, passengersList.size());
                    System.out.println("Total Fare: "+price);
                    String cnf = InputHandler.getStringValue("Confirm booking [y / n]");
                    if (cnf.equals("y")) {
                        BookingResponseDto responseDto = bookingService.bookTickets(curTrain.getTrainId(), seatType, fromStation, toStation, passengersList);
                        System.out.println();
                        System.out.println(responseDto);
                        break;
                    } else if (cnf.equals("n")) {
                        break;
                    } else  {
                        System.out.println("Wrong Input Try Again!");
                    }
                }
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
        int choice = InputHandler.getNumericValue("Booking Index to show passenger details or 0 for back", bookings.size());
        if (choice == 0) return;
        BookingResponseDto selectedBooking = bookingService.getBookingDetails(bookings.get(choice - 1).getPnr());
        System.out.println(selectedBooking);
    }

    private List<PassengerRequestDto> gatherPassengers() {
        List<PassengerRequestDto> passengerList = new ArrayList<>();

        System.out.println("Enter Passenger details (upto 6)");

        while (passengerList.size() < 6) {
            System.out.println("1. Add new passenger");
            System.out.println("2. Submit");
            System.out.println("3. Back to Main Menu\n");

            int choice = InputHandler.getNumericValue("choice", 3);
            System.out.println();
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

    public void getBookingDetailsByPNR() {
        String pnr = InputHandler.getStringValue("pnr No");
        try {
            BookingResponseDto booking = bookingService.getBookingDetails(pnr.trim());
            if (booking != null) {
                System.out.println(booking);
            }
        } catch (Exception e) {
            System.out.println("Error Occurred "+e.getMessage());
        }
    }

    private Station getStation(String label) {
        while (true) {
            try {
                String stationCode = InputHandler.getStringValue(label+" or 0 for Back");
                if (stationCode.equals("0")) return null;
                Station s = bookingService.getStation(stationCode);
                if (s == null) throw new InvalidInputException("Invalid "+label);
                return s;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
