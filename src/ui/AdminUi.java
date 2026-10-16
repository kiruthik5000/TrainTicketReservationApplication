package ui;

import dto.PassengerResponseDto;
import exception.DataBaseException;
import exception.InvalidInputException;
import exception.UnAuthorizedAccessException;
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
        printPassengerValues(trainList.get(choice - 1).getTrainId());
    }
    public void printPassengerValues(int trainID) throws DataBaseException {
        int curWindow = 0;
        while (true) {
            if (curWindow >= 0) {
                List<PassengerResponseDto> passengerResponseDtos = adminService.showAllPassengersInTrain(trainID, curWindow);
                if (passengerResponseDtos != null) {

                    System.out.println("Passengers: ");
                    for (PassengerResponseDto p : passengerResponseDtos) {
                        System.out.println(p);
                    }
                } else {
                    System.out.println("End of passenger list");
                    System.out.println("return to Main Menu");
                    return;
                }
            }
            System.out.println("1. show next passengers");
            System.out.println("2. previous list of passengers");
            System.out.println("3. return");
            try {
                int choice = InputHandler.getNumericValue("choice", 3);
                if (choice == 1) {
                    curWindow++;
                } else if (choice == 2) {
                    curWindow--;
                    if (curWindow < 0) throw new InvalidInputException("Invalid Operation");
                } else {
                    return;
                }
            }catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
        }
    }
}
