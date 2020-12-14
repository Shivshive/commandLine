package commandLine;

import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.Executor;


public class CommandHandle{

	public static void main(String[] args) throws ExecuteException, IOException, InterruptedException {

		Mthread task1 = new Mthread();
		Mthread task2 = new Mthread();
		Mthread task3 = new Mthread();
		
		final Thread t1 = new Thread(task1,"Task 1");
		final Thread t2 = new Thread(task2,"Task 2");
		final Thread t3 = new Thread(task3,"Task 3");
		
		task2.setThreadPrdecessor(t1);
		task3.setThreadPrdecessor(t2);
		
		t1.start();
		t2.start();
		t3.start();
	}


	public void case_1() throws ExecuteException, IOException {
		
		String line = "java -jar C:\\Users\\Chandra-Kautilya\\Documents\\selenium-server-standalone-3.141.59.jar";
		CommandLine cmdLine = CommandLine.parse(line);
		DefaultExecutor executor = new DefaultExecutor();
		executor.setExitValue(1);
		int exitValue = executor.execute(cmdLine);

		System.out.println(exitValue);
	}
	
	
	public void case_2() throws ExecuteException, IOException, InterruptedException {
		
		CommandLine cmdLine = new CommandLine("Java");
		cmdLine.addArgument("-jar");
		cmdLine.addArgument("C:\\Users\\Chandra-Kautilya\\Documents\\selenium-server-standalone-3.141.59.jar");
		
		DefaultExecuteResultHandler resultHandler = new DefaultExecuteResultHandler();

		ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
		Executor executor = new DefaultExecutor();
		executor.setExitValue(1);
		executor.setWatchdog(watchdog);
		executor.execute(cmdLine, resultHandler);

		
		System.out.println("Hello ,, this is the secondary code");
		
	}

}
