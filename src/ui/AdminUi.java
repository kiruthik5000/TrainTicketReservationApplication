package ui;

import dto.PassengerResponseDto;
import exception.DataBaseException;
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
            List<PassengerResponseDto> passengerResponseDtos = adminService.showAllPassengersInTrain(trainID, curWindow);
            if (passengerResponseDtos == null) {
                System.out.println("End of passenger list");
                return;
            }
            System.out.println("Passengers: ");
            for (PassengerResponseDto p : passengerResponseDtos) {
                System.out.println(p);
            }
            System.out.println("1. show next passengers");
            System.out.println("2. return");

            int choice = InputHandler.getNumericValue("choice", 2);
            if (choice == 1) {
                curWindow ++;
            } else {
                return;
            }
        }
    }
}
