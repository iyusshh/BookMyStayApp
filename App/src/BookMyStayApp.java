import java.util.*;

// Booking Request class
class BookingRequest {
    private String guestName;
    private String roomType;

    public BookingRequest(String guestName, String roomType) {
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

// Shared Booking Processor
class ConcurrentBookingProcessor {

    private Queue<BookingRequest> bookingQueue;
    private Map<String, Integer> inventory;

    public ConcurrentBookingProcessor(Queue<BookingRequest> queue,
                                      Map<String, Integer> inventory) {
        this.bookingQueue = queue;
        this.inventory = inventory;
    }

    // Critical Section (Thread-safe)
    public synchronized void processBooking() {
        if (bookingQueue.isEmpty()) {
            return;
        }

        BookingRequest request = bookingQueue.poll();

        if (request == null) return;

        String roomType = request.getRoomType();

        int available = inventory.getOrDefault(roomType, 0);

        if (available > 0) {
            // Allocate room safely
            inventory.put(roomType, available - 1);

            System.out.println(Thread.currentThread().getName() +
                    " booked " + roomType +
                    " room for " + request.getGuestName());
        } else {
            System.out.println(Thread.currentThread().getName() +
                    " FAILED booking for " + request.getGuestName() +
                    " (No " + roomType + " rooms available)");
        }
    }
}

// Worker Thread
class BookingWorker extends Thread {

    private ConcurrentBookingProcessor processor;

    public BookingWorker(ConcurrentBookingProcessor processor, String name) {
        super(name);
        this.processor = processor;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (processor) {
                if (Thread.currentThread().isInterrupted()) break;
                processor.processBooking();

                // Stop if queue becomes empty
                if (Thread.currentThread().isInterrupted()) break;
            }

            try {
                Thread.sleep(100); // simulate delay
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}

// Main class
public class UseCase11ConcurrentBookingSimulation {

    public static void main(String[] args) throws InterruptedException {

        // Shared booking queue
        Queue<BookingRequest> queue = new LinkedList<>();

        // Sample booking requests (simulating multiple users)
        queue.add(new BookingRequest("Alice", "Single"));
        queue.add(new BookingRequest("Bob", "Single"));
        queue.add(new BookingRequest("Charlie", "Single"));
        queue.add(new BookingRequest("David", "Double"));
        queue.add(new BookingRequest("Eve", "Double"));

        // Shared inventory
        Map<String, Integer> inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 1);

        ConcurrentBookingProcessor processor =
                new ConcurrentBookingProcessor(queue, inventory);

        // Multiple threads (simulating concurrent users)
        BookingWorker t1 = new BookingWorker(processor, "Thread-1");
        BookingWorker t2 = new BookingWorker(processor, "Thread-2");
        BookingWorker t3 = new BookingWorker(processor, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        // Let threads run for some time
        Thread.sleep(2000);

        // Stop threads
        t1.interrupt();
        t2.interrupt();
        t3.interrupt();

        t1.join();
        t2.join();
        t3.join();

        // Final inventory state
        System.out.println("\n--- Final Inventory ---");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}