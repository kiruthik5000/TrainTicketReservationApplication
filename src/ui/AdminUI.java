package ui;

import dto.PassengerResponseDto;
import exception.DataBaseException;
import exception.UnAuthorizedAccessException;
import model.Train;
import service.AdminService;
import utils.InputHandler;
import utils.SessionStorage;

import java.util.List;

public class AdminUI {
    private final AdminService adminService;

    public AdminUI(AdminService adminService) {
        this.adminService = adminService;
    }

    public void showAllPassengersInTrain() throws DataBaseException {
        if (!SessionStorage.isAdmin()) throw new UnAuthorizedAccessException("Admins can Only view this feature");
        List<Train> trainList = adminService.getAllTrains();
        for (int i=0; i<trainList.size(); i++) {
            System.out.println((i + 1) +". "+ trainList.get(i));
        }
        int choice = InputHandler.getNumericValue("Train Index", trainList.size());
        List<PassengerResponseDto> passengerResponseDtos = adminService.showAllPassengersInTrain(trainList.get(choice - 1).getTrainId());
        System.out.println("Passengers: ");
        for (PassengerResponseDto p : passengerResponseDtos) {
            System.out.println(p);
        }
    }
}
