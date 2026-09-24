package service;

import repository.BookingRepository;
import repository.SeatRepository;
import repository.WaitingListRepository;

public class CancellationService {
    private final BookingRepository bookingRepository;
    private final SeatRepository seatRepository;
    private final WaitingListRepository waitingListRepository;

    public CancellationService(BookingRepository bookingRepository, SeatRepository seatRepository, WaitingListRepository waitingListRepository) {
        this.bookingRepository = bookingRepository;
        this.seatRepository = seatRepository;
        this.waitingListRepository = waitingListRepository;
    }


}
