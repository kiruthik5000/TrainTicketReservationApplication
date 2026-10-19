package ui;

import dto.PassengerResponseDto;
import exception.DataBaseException;
import exception.UnAuthorizedAccessException;
import model.SeatType;
import model.Train;
import service.AdminService;
import utils.InputHandler;
import utils.SessionStorage;

import java.util.List;

public class AdminUi {
    private final AdminService adminService;

    public AdminUi(AdminService adminService) {
        this.adminService = adminService;
    }

    public void showAllPassengersInTrain() throws DataBaseException {
        if (!SessionStorage.isAdmin()) throw new UnAuthorizedAccessException("Admins can Only view this feature");
        List<Train> trainList = adminService.getAllTrains();
        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1) +". "+ trainList.get(i));
        }
        int choice = InputHandler.getNumericValue("Train Index", trainList.size());
        List<SeatType> types = List.of(SeatType.AC, SeatType.SLEEPER);
        while (true) {
            try {
                for (int i=0; i<types.size(); i++) {
                    System.out.println((i + 1)+". "+types.get(i));
                }
                System.out.println((types.size() + 1)+". Return to Main Menu");
                System.out.println();
                int seatType = InputHandler.getNumericValue("Seat Type", types.size());
                if (seatType == types.size() + 1) return;
                printPassengerValues(types.get(seatType - 1), trainList.get(choice - 1).getTrainId());
            } catch (Exception e) {
                System.out.println("Error Occurred"+e.getMessage());
            }
        }
    }
    public void printPassengerValues(SeatType seatType, int trainID) throws DataBaseException {
        int curWindow = 0;
        boolean hasNext;

        while (true) {
            List<PassengerResponseDto> passengerResponseDtos = adminService.showAllPassengersInTrain(seatType, trainID, curWindow);

            if (passengerResponseDtos != null && !passengerResponseDtos.isEmpty()) {
                System.out.println("\nPassengers: ");
                for (PassengerResponseDto p : passengerResponseDtos) {
                    System.out.println(p);
                }
                hasNext = true;
            } else {
                System.out.println("\nEnd of passenger list.");
                hasNext = false;
            }

            if (hasNext) {
                System.out.println("1. Show next passengers");
            }
            if (curWindow > 0) {
                System.out.println("2. Previous list of passengers");
            }
            System.out.println("3. Return");

            try {
                int choice = InputHandler.getNumericValue("choice", 3);

                if (choice == 1) {
                    if (hasNext) {
                        curWindow++;
                    } else {
                        System.out.println("Invalid Operation: Already at the end.");
                    }
                } else if (choice == 2) {
                    if (curWindow > 0) {
                        curWindow--;
                    } else {
                        System.out.println("Invalid Operation: Already at the beginning.");
                    }
                } else if (choice == 3) {
                    return;
                } else {
                    System.out.println("Invalid Input.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
