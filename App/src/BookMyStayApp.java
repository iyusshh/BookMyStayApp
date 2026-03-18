import java.io.*;
import java.util.*;

// Reservation class (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationId;
    private String guestName;
    private String roomType;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Guest: " + guestName +
                ", Room: " + roomType;
    }
}

// Wrapper class to persist entire system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    List<Reservation> reservations;
    Map<String, Integer> inventory;

    public SystemState(List<Reservation> reservations, Map<String, Integer> inventory) {
        this.reservations = reservations;
        this.inventory = inventory;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.dat";

    // Save state to file
    public static void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("System state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // Load state from file
    public static SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            System.out.println("System state loaded successfully.");
            return (SystemState) ois.readObject();

        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading state. Starting with clean state.");
        }
        return null;
    }
}

// Main class
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Load previous state
        SystemState state = PersistenceService.load();

        List<Reservation> reservations;
        Map<String, Integer> inventory;

        if (state != null) {
            reservations = state.reservations;
            inventory = state.inventory;
        } else {
            // Initialize fresh state
            reservations = new ArrayList<>();
            inventory = new HashMap<>();
            inventory.put("Single", 3);
            inventory.put("Double", 2);
        }

        int choice;

        do {
            System.out.println("\n--- Menu ---");
            System.out.println("1. Add Booking");
            System.out.println("2. View Bookings");
            System.out.println("3. View Inventory");
            System.out.println("4. Save & Exit");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter Reservation ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Enter Guest Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter Room Type: ");
                    String roomType = scanner.nextLine();

                    // Simple allocation check
                    int available = inventory.getOrDefault(roomType, 0);

                    if (available > 0) {
                        Reservation r = new Reservation(id, name, roomType);
                        reservations.add(r);

                        inventory.put(roomType, available - 1);

                        System.out.println("Booking successful.");
                    } else {
                        System.out.println("No rooms available for this type.");
                    }
                    break;

                case 2:
                    System.out.println("\n--- Booking History ---");
                    for (Reservation r : reservations) {
                        System.out.println(r);
                    }
                    break;

                case 3:
                    System.out.println("\n--- Inventory ---");
                    for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    break;

                case 4:
                    // Save state before exit
                    SystemState newState = new SystemState(reservations, inventory);
                    PersistenceService.save(newState);
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 4);

        scanner.close();
    }
}