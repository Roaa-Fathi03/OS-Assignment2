import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Network {
    private static final StringBuilder outputBuffer = new StringBuilder();

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();
        Router router = new Router(maxConnections, outputBuffer);

        // Create a list to store devices
        List<Device> devices = new ArrayList<>();

        // Create and start threads for each device
        for (int i = 0; i < totalDevices; i++) {
            System.out.print("Enter device name and type (e.g., C1 mobile): ");
            String devname = scanner.next();
            String deviceType = scanner.next();
            System.out.println("Name : " + devname + " type: " + deviceType);
            Device device = new Device(devname, deviceType, router, outputBuffer);
            devices.add(device);
        }

        // Start threads for each device
        List<Thread> threads = new ArrayList<>();
        for (Device device : devices) {
            Thread thread = new Thread(device);
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Write the accumulated output to a file
        try (FileWriter fileWriter = new FileWriter("output.txt")) {
            fileWriter.write(outputBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Output written to the file: output.txt");
    }
}

class Device extends Thread {
    private String name;
    private String type;
    private Router myRouter;
    private StringBuilder outputBuffer;

    public Device(String name, String type, Router router, StringBuilder outputBuffer) {
        this.name = name;
        this.type = type;
        this.myRouter = router;
        this.outputBuffer = outputBuffer;
    }

    @Override
    public void run() {
        try {
            connect();
            performOnlineActivity();
            disconnect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws InterruptedException {
        myRouter.occupy(this);
        outputBuffer.append(this.name).append(" login.\n");
    }

    public void performOnlineActivity() throws InterruptedException {
        outputBuffer.append(this.name).append(" performs online activity.\n");
        // Simulate online activity
        Thread.sleep(new Random().nextInt(2000) + 1000);
    }

    public void disconnect() {
        outputBuffer.append(this.name).append(" logged out.\n");
        myRouter.release(this);
    }
    
    public String getDeviceType() {
        return type;
    }

    public String getDeviceName() {
        return name;
    }
}

class Router {
    private List<Device> connections;
    private Semaphore mySemaphore;
    private StringBuilder outputBuffer;

    public Router(int nOfConnections, StringBuilder outputBuffer) {
        connections = new ArrayList<>();
        mySemaphore = new Semaphore(nOfConnections);
        this.outputBuffer = outputBuffer;
    }

    public void occupy(Device device) throws InterruptedException {
        synchronized (this) {
            while (connections.size() >= mySemaphore.getCount()) {
                outputBuffer.append(device.getDeviceName()).append(" (")
                        .append(device.getDeviceType()).append(") arrived and waiting\n");
                wait(); // Wait until a connection is released
            }

            connections.add(device);
            outputBuffer.append(device.getDeviceName()).append(" (")
            .append(device.getDeviceType()).append(") arrived\n");
            outputBuffer.append("- Connection ").append(device.getDeviceName())
                    .append(" (").append(device.getDeviceType()).append(") Occupied\n");
        }
    }

    public void release(Device device) {
        synchronized (this) {
            connections.remove(device);
            mySemaphore.sem_signal();
            notify(); // Notify waiting threads that a connection is available
        }
    }
}

class Semaphore {
    private int count;

    public Semaphore() {
        this(0);
    }

    public Semaphore(int maxConnections) {
        count = maxConnections;
    }

    public synchronized void sem_wait() {
        while (count <= 0) ; // if count less than or equal to 0, full
        count--;
    }

    public synchronized void sem_signal() {
        count++;
    }

    public int getCount() {
        return count;
    }
}
