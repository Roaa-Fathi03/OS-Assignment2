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

        Device d = new Device("c1", "mobile", router);

    }
}
class Device {
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
        System.out.println(toString() + "arrived");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
    public void connect(String deviceName) throws InterruptedException {
        myRouter.occupy(this.name);
        System.out.println(deviceName + " has connected to the router.");
        myRouter.getConnections().add(deviceName);
    }

    public void performOnlineActivity(String deviceName) throws InterruptedException {
        System.out.println(deviceName + " is performing online activity.");
        // Simulate online activity
        Thread.sleep(new Random().nextInt(2000) + 1000);
    }

    public void disconnect(String deviceName) {
        myRouter.getConnections().remove(deviceName);
        myRouter.release(this.name);
        System.out.println(deviceName + " has disconnected from the router.");
    }
}

class Router {
    private List<String> connections;
    Semaphore mySemaphore;

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
        if (count < 0) ;
        count--;
    }

    public synchronized void sem_signal() {
        count++;
    }

}
