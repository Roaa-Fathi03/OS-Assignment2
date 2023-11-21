
package Semaphores;
import java.util.ArrayList;
import java.util.List;
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
}


