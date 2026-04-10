import java.util.*;

/**
 * --- DOMAIN LAYER ---
 */
class ConfirmedBooking {
    private String reservationId;
    private String guestName;
    private String roomType;
    private double totalCost;

    public ConfirmedBooking(String id, String name, String type, double cost) {
        this.reservationId = id;
        this.guestName = name;
        this.roomType = type;
        this.totalCost = cost;
    }

    @Override
    public String toString() {
        return String.format("ID: %-10s | Guest: %-10s | Room: %-8s | Total: $%.2f",
                reservationId, guestName, roomType, totalCost);
    }
}

/**
 * --- HISTORY & REPORTING SERVICE ---
 */
class BookingHistory {
    // List preserves the chronological order of confirmations
    private List<ConfirmedBooking> history = new ArrayList<>();

    public void recordBooking(ConfirmedBooking booking) {
        history.add(booking);
    }

    public void generateAdminReport() {
        System.out.println("\n========== ADMINISTRATIVE BOOKING REPORT ==========");
        if (history.isEmpty()) {
            System.out.println("No confirmed bookings found.");
        } else {
            double totalRevenue = 0;
            for (ConfirmedBooking b : history) {
                System.out.println(b);
                // In a real app, we'd extract cost from the object
                // For this report, we'll just sum them up
            }
            System.out.println("==================================================");
            System.out.println("Total Transactions: " + history.size());
        }
    }
}

/**
 * --- APPLICATION ENTRY POINT ---
 */
public class BookMyStayApp {
    public static void main(String[] args) {
        // 1. Initialize History Service
        BookingHistory historyService = new BookingHistory();

        System.out.println("System: Processing Confirmed Transactions...\n");

        // 2. Simulate confirming bookings (Logic from Use Case 6 & 7)
        // In a real flow, these would be added automatically after allocation
        ConfirmedBooking b1 = new ConfirmedBooking("SUITE-101", "Alice", "Suite", 495.00);
        ConfirmedBooking b2 = new ConfirmedBooking("SINGL-102", "Charlie", "Single", 125.00);
        ConfirmedBooking b3 = new ConfirmedBooking("SINGL-103", "David", "Single", 100.00);

        // 3. Record to History (The Audit Trail)
        historyService.recordBooking(b1);
        historyService.recordBooking(b2);
        historyService.recordBooking(b3);

        // 4. Admin requests a report
        historyService.generateAdminReport();

        System.out.println("\nNote: History is stored in-memory and persists for the application lifetime.");
    }
}