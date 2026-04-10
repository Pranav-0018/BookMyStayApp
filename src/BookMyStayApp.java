
import java.util.*;

/* ------------------ CUSTOM EXCEPTIONS ------------------ */
class InvalidRoomTypeException extends Exception {
    public InvalidRoomTypeException(String message) { super(message); }
}

class InsufficientInventoryException extends Exception {
    public InsufficientInventoryException(String message) { super(message); }
}

class ReservationNotFoundException extends Exception {
    public ReservationNotFoundException(String message) { super(message); }
}

/* ------------------ VALIDATOR ------------------ */
class BookingValidator {
    public static void validateRequest(String type, Map<String, Integer> inventory)
            throws InvalidRoomTypeException, InsufficientInventoryException {

        if (!inventory.containsKey(type))
            throw new InvalidRoomTypeException("Error: Room type '" + type + "' does not exist.");

        if (inventory.get(type) <= 0)
            throw new InsufficientInventoryException("Error: No vacancy for '" + type + "'.");
    }
}

/* ------------------ RESERVATION MODEL ------------------ */
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String id, String guestName, String roomType) {
        this.reservationId = id;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

/* ------------------ BOOKING HISTORY ------------------ */
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();
    private Map<String, Reservation> activeReservations = new HashMap<>();

    public void addReservation(Reservation r) {
        history.add(r);
        activeReservations.put(r.getReservationId(), r);
    }

    public Reservation removeReservation(String reservationId)
            throws ReservationNotFoundException {

        if (!activeReservations.containsKey(reservationId))
            throw new ReservationNotFoundException(
                    "Error: Reservation '" + reservationId + "' not found.");

        return activeReservations.remove(reservationId);
    }

    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }

    public Collection<Reservation> getActiveReservations() {
        return activeReservations.values();
    }
}

/* ------------------ REPORT SERVICE ------------------ */
class BookingReportService {

    public static void printActiveBookings(BookingHistory history) {
        System.out.println("\n--- Active Bookings ---");

        for (Reservation r : history.getActiveReservations()) {
            System.out.println(r.getReservationId() + " | "
                    + r.getGuestName() + " | " + r.getRoomType());
        }
    }
}

/* ------------------ CANCELLATION SERVICE ------------------ */
class CancellationService {
    private Stack<String> rollbackStack = new Stack<>();

    public void cancelReservation(String reservationId,
                                  Map<String, Integer> inventory,
                                  BookingHistory history)
            throws ReservationNotFoundException {

        Reservation r = history.removeReservation(reservationId);

        rollbackStack.push(reservationId);

        // restore inventory
        String roomType = r.getRoomType();
        inventory.put(roomType, inventory.get(roomType) + 1);

        System.out.println("CANCELLED: Reservation " + reservationId
                + " for " + r.getGuestName());
    }
}

/* ------------------ CORE SYSTEM ------------------ */
class HotelSystem {
    private Map<String, Integer> inventory = new HashMap<>();
    private BookingHistory history = new BookingHistory();
    private CancellationService cancellationService = new CancellationService();
    private int idCounter = 1;

    public void addInventory(String type, int count) {
        inventory.put(type, count);
    }

    public BookingHistory getHistory() { return history; }

    public void processBooking(String guest, String type) {
        System.out.println("[PROCESSING] " + guest + " requesting " + type + "...");

        try {
            BookingValidator.validateRequest(type, inventory);

            inventory.put(type, inventory.get(type) - 1);

            String reservationId = "RES-" + idCounter++;
            Reservation r = new Reservation(reservationId, guest, type);
            history.addReservation(r);

            System.out.println("SUCCESS: Booking confirmed. ID = " + reservationId);

        } catch (InvalidRoomTypeException | InsufficientInventoryException e) {
            System.err.println("REJECTED: " + e.getMessage());
        }

        System.out.println("-------------------------------------------");
    }

    public void cancelBooking(String reservationId) {
        try {
            cancellationService.cancelReservation(reservationId, inventory, history);
        } catch (ReservationNotFoundException e) {
            System.err.println("REJECTED: " + e.getMessage());
        }
        System.out.println("-------------------------------------------");
    }
}

/* ------------------ MAIN APPLICATION ------------------ */
public class BookMyStayApp {
    public static void main(String[] args) {

        HotelSystem hotel = new HotelSystem();
        hotel.addInventory("Suite", 1);
        hotel.addInventory("Single", 2);

        hotel.processBooking("Alice", "Suite");
        hotel.processBooking("Bob", "Single");

        BookingReportService.printActiveBookings(hotel.getHistory());

        // Cancellation
        hotel.cancelBooking("RES-1");

        BookingReportService.printActiveBookings(hotel.getHistory());
    }
}