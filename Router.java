
package Semaphores;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;



public class Router {
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

	public void connect(String deviceName) throws InterruptedException {
		mySemaphore.sem_acquire();
		System.out.println(deviceName + " has connected to the router.");
		connections.add(deviceName);
	}

	public void performOnlineActivity(String deviceName) throws InterruptedException {
		System.out.println(deviceName + " is performing online activity.");
		// Simulate online activity
		Thread.sleep(new Random().nextInt(2000) + 1000);
	}

	public void disconnect(String deviceName) {
		connections.remove(deviceName);
		mySemaphore.sem_release();
		System.out.println(deviceName + " has disconnected from the router.");
	}
}


