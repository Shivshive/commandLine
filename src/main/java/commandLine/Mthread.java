package commandLine;

public class Mthread implements Runnable {

	Thread predecessor;
		
	public void setThreadPrdecessor(Thread predecessor) {
		this.predecessor = predecessor;
	}
		
	public void run() {
		System.out.println("Starting "+Thread.currentThread().getName()+" Exection.... ");
		
		if(predecessor!=null) {
			
			try {
			
				predecessor.join();
			
			} 
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Ending "+Thread.currentThread().getName()+" Exection.... ");
	}
}
