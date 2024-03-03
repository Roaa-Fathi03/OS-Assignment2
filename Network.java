package Semaphores;
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

        // Create and start threads for each device
        for (int i = 0; i < totalDevices; i++) {
            String devname = scanner.next();
            String deviceType = scanner.next();
            System.out.println("Name: " + devname + " Type: " + deviceType);
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
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect() throws InterruptedException {
        myRouter.occupy(this);
        System.out.println("Connection " + myRouter.getConnectionNumber(this) + ": " + this.name + " login.");
    }

    public void performOnlineActivity() throws InterruptedException {
        System.out.println("Connection " + myRouter.getConnectionNumber(this) + ": " + this.name + " performs online activity.");
        // Simulate online activity
        Thread.sleep(new Random().nextInt(2000) + 1000);
    }

    public void disconnect() {
        System.out.println("Connection " + myRouter.getConnectionNumber(this) + ": " + this.name + " logged out.");
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
    private List<Pair> connections;
    private Semaphore mySemaphore;
    private int maxConnections;

    public Router(int maxConnections) { // constructor function
        connections = new ArrayList<>();
        this.maxConnections = maxConnections;
        mySemaphore = new Semaphore(maxConnections);
    }

    public void occupy(Device device) throws InterruptedException {
        synchronized (this) {
        	if(connections.size()< maxConnections) {
                System.out.println(device.toString() + "arrived");
        	}
            while (connections.size() >= maxConnections) {
                System.out.println(device.getDeviceName() + " (" + device.getDeviceType() + ") arrived and waiting");
                mySemaphore.sem_wait();
                wait(); // Wait until a connection is released
            }

            int connectionNumber = getNextConnectionNumber();
            connections.add(new Pair(device, connectionNumber));
            System.out.println("Connection " + connectionNumber + ": " + device.getDeviceName() + " (" + device.getDeviceType() + ") Occupied");
        }
    }

    public void release(Device device) {
        synchronized (this) {
            int connectionNumber = getConnectionNumber(device);
            connections.removeIf(pair -> pair.device == device);
            mySemaphore.sem_signal();
            notify(); // Notify waiting threads that a connection is available
        }
    }

    private int getNextConnectionNumber() {
        int nextConnectionNumber = 1;
        for (Pair pair : connections) {
            if (pair.connectionNumber == nextConnectionNumber) {
                nextConnectionNumber++;
            }
            else {
                break;
            }
        }
        return nextConnectionNumber;
    }

    public int getConnectionNumber(Device device) {
        for (Pair pair : connections) {
            if (pair.device == device) {
                return pair.connectionNumber;
            }
        }
        return -1; // Device not found
    }

    public List<Pair> getConnections() {
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

class Pair {
    public Device device;
    public int connectionNumber;

    public Pair(Device device, int connectionNumber) {
        this.device = device;
        this.connectionNumber = connectionNumber;
    }
}
