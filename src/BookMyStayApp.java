// Version 6.1

import java.util.*;

// Reservation Class
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

// Inventory Service
class RoomInventory {
    private HashMap<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 1);
        inventory.put("Suite", 1);
    }

    public int getAvailability(String type) {
        return inventory.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }
}

// Booking Service (Core Logic)
class RoomAllocationService {

    private Set<String> allocatedRooms = new HashSet<>();
    private Map<String, Set<String>> roomMap = new HashMap<>();
    private Map<String, Integer> counters = new HashMap<>();

    public RoomAllocationService() {
        counters.put("Single", 0);
        counters.put("Double", 0);
        counters.put("Suite", 0);
    }

    public void processBookings(Queue<Reservation> queue, RoomInventory inventory) {

        System.out.println("Room Allocation Processing");

        while (!queue.isEmpty()) {

            Reservation r = queue.poll();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {

                // Generate unique room ID
                int count = counters.get(type) + 1;
                counters.put(type, count);

                String roomId = type + "-" + count;

                // Ensure uniqueness
                if (!allocatedRooms.contains(roomId)) {

                    allocatedRooms.add(roomId);

                    roomMap.putIfAbsent(type, new HashSet<>());
                    roomMap.get(type).add(roomId);

                    // Update inventory immediately
                    inventory.decrement(type);

                    System.out.println(
                            "Booking confirmed for Guest: " +
                                    r.getGuestName() +
                                    ", Room ID: " + roomId
                    );
                }

            } else {
                System.out.println(
                        "Booking failed for Guest: " +
                                r.getGuestName() +
                                " (No rooms available)"
                );
            }
        }
    }
}

// Main Class
public class BookMyStayApp{

    public static void main(String[] args) {

        // Step 1: Create Queue (FIFO)
        Queue<Reservation> queue = new LinkedList<>();
        queue.offer(new Reservation("Abhi", "Single"));
        queue.offer(new Reservation("Subha", "Single"));
        queue.offer(new Reservation("Vanmathi", "Suite"));

        // Step 2: Inventory
        RoomInventory inventory = new RoomInventory();

        // Step 3: Allocation Service
        RoomAllocationService service = new RoomAllocationService();

        // Step 4: Process Bookings
        service.processBookings(queue, inventory);
    }
}