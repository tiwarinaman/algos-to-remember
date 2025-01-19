package ticketBookingMock;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SeatBookingSystem {


    /**
     * Mocking Redis with a ConcurrentHashMap for seat status
     */
    static class RedisMock {

        private final ConcurrentHashMap<String, Seat> seats = new ConcurrentHashMap<>();
        private final ScheduledExecutorService ttlScheduler = Executors.newScheduledThreadPool(1);

        public RedisMock() {
            seats.put("seat1", new Seat("seat1"));
            seats.put("seat2", new Seat("seat2"));
        }

        public Seat getSeat(String seatId) {
            return seats.get(seatId);
        }

        public boolean reserveSeat(String seatId, int ttlInSec) {
            Seat seat = seats.get(seatId);
            if (seat != null && seat.status == SeatStatus.AVAILABLE) {
                seat.status = SeatStatus.RESERVED;
                // Schedule the seat to be released after TTL expires
                ttlScheduler.schedule(() -> releaseSeat(seatId), ttlInSec, TimeUnit.SECONDS);
                return true;
            }
            return false;
        }

        public void bookSeat(String seatId) {
            Seat seat = seats.get(seatId);
            if (seat != null && seat.status == SeatStatus.RESERVED) {
                seat.status = SeatStatus.BOOKED;
            }
        }

        public void releaseSeat(String seatId) {
            Seat seat = seats.get(seatId);
            if (seat != null && seat.status == SeatStatus.RESERVED) {
                seat.status = SeatStatus.AVAILABLE;
            }
        }

        public void shutdown() {
            ttlScheduler.shutdown();
        }

    }


    /**
     * Seat class with optimistic locking (version field)
     */
    static class Seat {
        String seatId;
        SeatStatus status;
        AtomicInteger version;

        public Seat(String seatId) {
            this.seatId = seatId;
            this.status = SeatStatus.AVAILABLE;
            this.version = new AtomicInteger(0);
        }

    }

    enum SeatStatus {
        AVAILABLE,
        BOOKED,
        RESERVED
    }

    static class SeatReservationService {

        private final RedisMock redis;

        public SeatReservationService(RedisMock redis) {
            this.redis = redis;
        }

        public boolean reserveSeat(String seatId, int ttlSeconds) {
            return redis.reserveSeat(seatId, ttlSeconds);
        }

        public boolean bookSeat(String seatId) {

            Seat seat = redis.getSeat(seatId);

            if (seat == null) return false;

            int currentVersion = seat.version.get();
            if (seat.status == SeatStatus.RESERVED &&
                    seat.version.compareAndSet(currentVersion, currentVersion + 1)) {
                redis.bookSeat(seatId);
                return true;
            }
            return false;
        }

    }

}
