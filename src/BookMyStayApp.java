import java.io.*;
import java.util.*;

class InventoryData implements Serializable {
    Map<String, Integer> inventory;

    public InventoryData(Map<String, Integer> inventory) {
        this.inventory = inventory;
    }
}

class PersistenceService {
    private static final String FILE = "inventory.dat";

    public static Map<String, Integer> loadInventory() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE))) {
            InventoryData data = (InventoryData) in.readObject();
            return data.inventory;
        } catch (Exception e) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return null;
        }
    }

    public static void saveInventory(Map<String, Integer> inventory) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE))) {
            out.writeObject(new InventoryData(inventory));
            System.out.println("Inventory saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("System Recovery");

        Map<String, Integer> inventory = PersistenceService.loadInventory();

        if (inventory == null) {
            inventory = new LinkedHashMap<>();
            inventory.put("Single", 5);
            inventory.put("Double", 3);
            inventory.put("Suite", 2);
        }

        System.out.println("Current Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + ": " + inventory.get(type));
        }

        PersistenceService.saveInventory(inventory);
    }
}