import java.util.*;

// Class representing an Add-On Service
class AddOnService {
    private String serviceName;
    private double cost;

    public AddOnService(String serviceName, double cost) {
        this.serviceName = serviceName;
        this.cost = cost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return serviceName + " (₹" + cost + ")";
    }
}

// Manager class to handle mapping between reservation and services
class AddOnServiceManager {
    private Map<String, List<AddOnService>> reservationServicesMap;

    public AddOnServiceManager() {
        reservationServicesMap = new HashMap<>();
    }

    // Add service
    public void addService(String reservationId, AddOnService service) {
        reservationServicesMap
                .computeIfAbsent(reservationId, k -> new ArrayList<>())
                .add(service);
    }

    // Get services
    public List<AddOnService> getServices(String reservationId) {
        return reservationServicesMap.getOrDefault(reservationId, new ArrayList<>());
    }

    // Calculate total cost
    public double calculateTotalCost(String reservationId) {
        double total = 0;
        for (AddOnService service : getServices(reservationId)) {
            total += service.getCost();
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        List<AddOnService> services = getServices(reservationId);

        if (services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        System.out.println("Selected Add-On Services:");
        for (AddOnService service : services) {
            System.out.println("- " + service);
        }
    }
}

// Main class (UPDATED NAME)
public class BookMyStayApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AddOnServiceManager manager = new AddOnServiceManager();

        System.out.print("Enter Reservation ID: ");
        String reservationId = scanner.nextLine();

        int choice;

        do {
            System.out.println("\n--- Add-On Service Menu ---");
            System.out.println("1. Add Breakfast (₹200)");
            System.out.println("2. Add Airport Pickup (₹500)");
            System.out.println("3. Add Extra Bed (₹300)");
            System.out.println("4. View Selected Services");
            System.out.println("5. Calculate Total Cost");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    manager.addService(reservationId, new AddOnService("Breakfast", 200));
                    System.out.println("Breakfast added.");
                    break;

                case 2:
                    manager.addService(reservationId, new AddOnService("Airport Pickup", 500));
                    System.out.println("Airport Pickup added.");
                    break;

                case 3:
                    manager.addService(reservationId, new AddOnService("Extra Bed", 300));
                    System.out.println("Extra Bed added.");
                    break;

                case 4:
                    manager.displayServices(reservationId);
                    break;

                case 5:
                    double total = manager.calculateTotalCost(reservationId);
                    System.out.println("Total Add-On Cost: ₹" + total);
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