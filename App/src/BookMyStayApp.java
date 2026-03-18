import java.util.*;

// Reservation class
class Reservation {
    private String reservationId;
    private String roomType;
    private String roomId;
    private boolean isActive;

    public Reservation(String reservationId, String roomType, String roomId) {
        this.reservationId = reservationId;
        this.roomType = roomType;
        this.roomId = roomId;
        this.isActive = true;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void cancel() {
        isActive = false;
    }

    @Override
    public String toString() {
        return "Reservation ID: " + reservationId +
                ", Room Type: " + roomType +
                ", Room ID: " + roomId +
                ", Status: " + (isActive ? "CONFIRMED" : "CANCELLED");
    }
}

// Cancellation Service
class CancellationService {

    private Map<String, Reservation> reservations;
    private Map<String, Integer> inventory;
    private Stack<String> rollbackStack;

    public CancellationService(Map<String, Reservation> reservations,
                               Map<String, Integer> inventory) {
        this.reservations = reservations;
        this.inventory = inventory;
        this.rollbackStack = new Stack<>();
    }

    // Cancel booking with rollback
    public void cancelBooking(String reservationId) {
        // Validate existence
        if (!reservations.containsKey(reservationId)) {
            System.out.println("Cancellation Failed: Reservation does not exist.");
            return;
        }

        Reservation res = reservations.get(reservationId);

        // Validate already cancelled
        if (!res.isActive()) {
            System.out.println("Cancellation Failed: Booking already cancelled.");
            return;
        }

        // Step 1: Push room ID to rollback stack (LIFO)
        rollbackStack.push(res.getRoomId());

        // Step 2: Restore inventory
        String roomType = res.getRoomType();
        inventory.put(roomType, inventory.getOrDefault(roomType, 0) + 1);

        // Step 3: Mark reservation as cancelled
        res.cancel();

        System.out.println("Cancellation successful for Reservation ID: " + reservationId);
    }

    // View rollback stack
    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent Releases): " + rollbackStack);
    }
}

// Main class
public class UseCase10BookingCancellation {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Sample inventory
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 2);

        // Reservation storage
        Map<String, Reservation> reservations = new HashMap<>();

        // Pre-create some bookings
        reservations.put("R1", new Reservation("R1", "Single", "S101"));
        inventory.put("Single", inventory.get("Single") - 1);

        reservations.put("R2", new Reservation("R2", "Double", "D201"));
        inventory.put("Double", inventory.get("Double") - 1);

        CancellationService service = new CancellationService(reservations, inventory);

        int choice;

        do {
            System.out.println("\n--- Cancellation Menu ---");
            System.out.println("1. View Reservations");
            System.out.println("2. Cancel Booking");
            System.out.println("3. View Inventory");
            System.out.println("4. View Rollback Stack");
            System.out.println("0. Exit");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    System.out.println("\n--- Reservations ---");
                    for (Reservation r : reservations.values()) {
                        System.out.println(r);
                    }
                    break;

                case 2:
                    System.out.print("Enter Reservation ID to cancel: ");
                    String id = scanner.nextLine();
                    service.cancelBooking(id);
                    break;

                case 3:
                    System.out.println("\n--- Inventory ---");
                    for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                    break;

                case 4:
                    service.showRollbackStack();
                    break;

                case 0:
                    System.out.println("Exiting...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        scanner.close();
    }
}