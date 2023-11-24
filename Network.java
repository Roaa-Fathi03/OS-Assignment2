import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Network {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int maxConnections = scanner.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int totalDevices = scanner.nextInt();
        Router router = new Router(maxConnections);

        // Create a list to store devices
        List<Device> devices = new ArrayList<>();

        // Create and start threads for each device'
        for (int i = 0; i < totalDevices; i++) {
            System.out.print("Enter device name and type (e.g., C1 mobile): ");
        	String devname = scanner.next();
        	String deviceType = scanner.next();
        	System.out.println("Name : " + devname + " type: " + deviceType);
            Device device = new Device(devname, deviceType, router);
            devices.add(device);
        }

        // Start threads for each device
        for (Device device : devices) {
            Thread thread = new Thread(device);
            thread.start();
        }
    }
}

class Device extends Thread {
    private String name;
    private String type;
    private Router myRouter;

    public Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.myRouter = router;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "(" + name + ") " + "(" + type + ") ";
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
        System.out.println(this.name + " login.");
    }

    public void performOnlineActivity() throws InterruptedException {
        System.out.println(this.name + " performs online activity.");
        // Simulate online activity
        Thread.sleep(new Random().nextInt(2000) + 1000);
    }

    public void disconnect() {
        System.out.println(this.name + " logged out.");
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

    public Router(int nOfConnections) { // constructor function
        connections = new ArrayList<>();
        mySemaphore = new Semaphore(nOfConnections);
    }

    public void occupy(Device device) throws InterruptedException {
        synchronized (this) {
            while (connections.size() >= mySemaphore.getCount()) {
                System.out.println(device.getDeviceName() + " (" + device.getDeviceType() + ") arrived and waiting");
                wait(); // Wait until a connection is released
            }

            connections.add(device);
            System.out.println(device.toString() + " arrived");
            System.out.println("- Connection " + device.getDeviceName() + " (" + device.getDeviceType() + ") Occupied");
        }
    }

    public void release(Device device) {
        synchronized (this) {
            connections.remove(device);
            //System.out.println(device.getDeviceName() + " logged out.");
            mySemaphore.sem_signal();
            notify(); // Notify waiting threads that a connection is available
        }
    }

    public List<Device> getConnections() {
        return connections;
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
