import java.util.*;

// Room class
class Room {
    int roomId;
    String type;
    boolean isAvailable;

    Room(int roomId, String type) {
        this.roomId = roomId;
        this.type = type;
        this.isAvailable = true;
    }
}

// Booking class
class Booking {
    int bookingId;
    int roomId;
    String customerName;

    Booking(int bookingId, int roomId, String customerName) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.customerName = customerName;
    }
}

// Booking Request (for Queue)
class BookingRequest {
    String customerName;
    String preferredType;

    BookingRequest(String customerName, String preferredType) {
        this.customerName = customerName;
        this.preferredType = preferredType;
    }
}

// Main System
class HotelSystem {

    private Map<Integer, Room> rooms = new HashMap<>();
    private Queue<BookingRequest> requestQueue = new LinkedList<>();
    private Set<Integer> bookedRooms = new HashSet<>();
    private List<Booking> bookings = new ArrayList<>();

    private int bookingCounter = 1;

    // Add Rooms
    public void addRoom(int id, String type) {
        rooms.put(id, new Room(id, type));
    }

    // Add Booking Request (FIFO)
    public void addRequest(String name, String type) {
        requestQueue.offer(new BookingRequest(name, type));
    }

    // Process Requests
    public void processRequests() {
        while (!requestQueue.isEmpty()) {
            BookingRequest req = requestQueue.poll();

            boolean booked = false;

            for (Room room : rooms.values()) {
                if (room.isAvailable && room.type.equalsIgnoreCase(req.preferredType)
                        && !bookedRooms.contains(room.roomId)) {

                    // Allocate room
                    room.isAvailable = false;
                    bookedRooms.add(room.roomId);

                    Booking booking = new Booking(
                            bookingCounter++, room.roomId, req.customerName
                    );

                    bookings.add(booking);

                    System.out.println("Booking Confirmed: " +
                            req.customerName + " -> Room " + room.roomId);

                    booked = true;
                    break;
                }
            }

            if (!booked) {
                System.out.println("No room available for " + req.customerName);
            }
        }
    }

    // View All Bookings
    public void viewBookings() {
        for (Booking b : bookings) {
            System.out.println("BookingID: " + b.bookingId +
                    ", Room: " + b.roomId +
                    ", Customer: " + b.customerName);
        }
    }
}

// Main Driver
public class solution{
    public static void main(String[] args) {

        HotelSystem system = new HotelSystem();

        // Add rooms
        system.addRoom(101, "Single");
        system.addRoom(102, "Double");
        system.addRoom(103, "Single");

        // Add booking requests (FIFO)
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of requests: ");
        int n = sc.nextInt();
        sc.nextLine();

        for (int i = 0; i < n; i++) {
            System.out.print("Enter name: ");
            String name = sc.nextLine();

            System.out.print("Enter room type: ");
            String type = sc.nextLine();

            system.addRequest(name, type);
        }

        // Process bookings
        system.processRequests();

        System.out.println("\nAll Bookings:");
        system.viewBookings();
    }
}
