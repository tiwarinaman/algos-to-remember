package ticketBookingMock;

import ticketBookingMock.SeatBookingSystem.RedisMock;
import ticketBookingMock.SeatBookingSystem.SeatReservationService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {

        // Initialize system
        RedisMock redisMock = new RedisMock();
        SeatReservationService seatReservationService = new SeatReservationService(redisMock);


        // Simulate 10,000 users trying to book 2 seats
        int numberOfUsers = 10_000;
        AtomicInteger successfulBookings = new AtomicInteger(0);


        try (ExecutorService executorService = Executors.newFixedThreadPool(100)) {

            Runnable bookingTasks = () -> {
                String[] seats = {"seat1", "seat2"};
                for (String seatId : seats) {
                    // Try reserving the seat
                    if (seatReservationService.reserveSeat(seatId, 5)) { // Reserve seat for 5 seconds
                        // Simulate some delay in payment processing
                        try {
                            Thread.sleep((long) (Math.random() * 200));
                        } catch (InterruptedException ex) {
                            Thread.currentThread().interrupt();
                        }
                        // Try booking the seat
                        if (seatReservationService.bookSeat(seatId)) {
                            successfulBookings.incrementAndGet();
                            break;
                        }
                    }
                }
            };

            // Submit booking tasks for all users
            for (int i = 0; i < numberOfUsers; i++) {
                executorService.submit(bookingTasks);
            }

            // Shutdown the executor service and wait for all tasks to complete
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

            // Results
            System.out.println("Total successful bookings: " + successfulBookings.get());
            System.out.println("Seat1 final status: " + redisMock.getSeat("seat1").status);
            System.out.println("Seat2 final status: " + redisMock.getSeat("seat2").status);


            // Cleanup
            redisMock.shutdown();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
