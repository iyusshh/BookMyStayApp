import java.util.*;

// Custom Exception for invalid booking
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Validator class
class InvalidBookingValidator {

    private static final Set<String> VALID_ROOM_TYPES =
            new HashSet<>(Arrays.asList("Single", "Double", "Suite"));

    // Validate booking input
    public static void validate(String roomType, int roomsRequested, Map<String, Integer> inventory)
            throws InvalidBookingException {

        // Validate room type
        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }

        // Validate number of rooms
        if (roomsRequested <= 0) {
            throw new InvalidBookingException("Rooms requested must be greater than 0.");
        }

        // Validate availability
        int available = inventory.getOrDefault(roomType, 0);
        if (available <= 0) {
            throw new InvalidBookingException("No rooms available for type: " + roomType);
        }

        if (roomsRequested > available) {
            throw new InvalidBookingException(
                    "Requested rooms exceed available rooms. Available: " + available);
        }
    }
}

// Main class
public class UseCase9ErrorHandlingValidation {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        // Sample inventory
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 3);
        inventory.put("Suite", 1);

        int choice;

        do {
            System.out.println("\n--- Booking Menu ---");
            System.out.println("1. Book Room");
            System.out.println("2. View Inventory");
            System.out.println("0. Exit");

            System.out.print("Enter choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {

                case 1:
                    try {
                        System.out.print("Enter Room Type (Single/Double/Suite): ");
                        String roomType = scanner.nextLine();

                        System.out.print("Enter number of rooms: ");
                        int rooms = scanner.nextInt();

                        // Validate BEFORE updating system (Fail-Fast)
                        InvalidBookingValidator.validate(roomType, rooms, inventory);

                        // Update inventory only if valid
                        inventory.put(roomType, inventory.get(roomType) - rooms);

                        System.out.println("Booking successful!");

                    } catch (InvalidBookingException e) {
                        // Graceful failure handling
                        System.out.println("Booking Failed: " + e.getMessage());
                    } catch (Exception e) {
                        // Handle unexpected input errors
                        System.out.println("Invalid input! Please try again.");
                        scanner.nextLine(); // clear buffer
                    }
                    break;

                case 2:
                    System.out.println("\n--- Current Inventory ---");
                    for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
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