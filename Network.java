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
        for (int i = 1; i <= totalDevices; i++) {
            String deviceType = i % 2 == 0 ? "tablet" : "mobile"; // Alternate between tablet and mobile
            Device device = new Device("C" + i, deviceType, router);
            devices.add(device);
            Thread thread = new Thread(device);
            thread.start();
        }

    }
}

class Device extends Thread {
    private String name;
    private String type;
    Router myRouter;

    public Device() {
        this.name = "";
        this.type = "";
        this.myRouter = null;
    }

    public Device(String name, String type, Router router) {
        this.name = name;
        this.type = type;
        this.myRouter = router;
//        if (myRouter.getMaxConnections() < 4)
//            System.out.println(toString() + "arrived");
//        else{
//            System.out.println(toString() + "arrived and waiting");
//
//        }
    }

//    public String getName() {
//        return name;
//    }

//    public void setName(String name) {
//        this.name = name;
//    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "(" + name +  ") " +"(" + type + ") ";
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
        myRouter.occupy(this.name);
        System.out.println(this.name + " login.");
        myRouter.getConnections().add(this.name);
    }

    public void performOnlineActivity() throws InterruptedException {
        System.out.println(this.name + " performs online activity.");
        // Simulate online activity
        Thread.sleep(new Random().nextInt(2000) + 1000);
    }

    public void disconnect() {
        myRouter.getConnections().remove(this.name);
        myRouter.release(this.name);
        System.out.println(this.name + " logged out.");
    }
}

class Router {
    private List<String> connections;
    Semaphore mySemaphore;
//    private int maxConnections;

    public Router(int nOfConnections) { // constructor function
        connections = new ArrayList<>();
        mySemaphore = new Semaphore(nOfConnections);
    }

    public void occupy(String deviceName) {
        mySemaphore.sem_wait();
        // critical section
        connections.add(deviceName);
        System.out.println("Connection " +
                connections.size() + ": C" + deviceName + " Occupied" );
    }

    public void release(String deviceName) {
        connections.remove(deviceName);
        mySemaphore.sem_signal();
    }

    public List<String> getConnections() {
        return connections;
    }

//    public int getMaxConnections() {
//        return maxConnections;
//    }
}


class Semaphore {
    private int count;

    public Semaphore()
    {
        this(0);
    }

    public Semaphore(int maxConnections) {
        count = maxConnections;
    }

    // NOTE:
    // I think wait() is reserved in Java so, I used sem_wait(), then sem_signal() for consistency

    public synchronized void sem_wait() {
        while (count < 0) ;
        count--;
    }

    public synchronized void sem_signal() {
        count++;
    }

}
