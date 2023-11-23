package Semaphores;

class Semaphore {
    private int count;

    public Semaphore(int maxConnections) {
        //
    }

    // NOTE:
    // I think wait() is reserved in Java so I used sem_wait(), then sem_signal() for consistency
    
    public synchronized void sem_wait() {
        //
    }

    public synchronized void sem_signal() {
    	//
    }

    public synchronized void sem_acquire() {
        //
    }

    public synchronized void sem_release() {
        //
    }
}
