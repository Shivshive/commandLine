package commandLine;

import java.io.IOException;
import java.util.*;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;

import commandutility.NativeTask;

public class Main {

	public java.util.Map<String, String> getDetails(){

		java.util.Map<String, String> details = new HashMap<String, String>();
		Scanner sc = new Scanner(System.in);

		System.out.println("Enter the Path");
		details.put("path", sc.next());
		sc.close();

		return details;
	}

	public static void main(String[] args) {

		Main main = new Main();
		
//		java.util.Map<String,String> d = main.getDetails();
//		CommandExecutor exe_ = main.new CommandExecutor();
//		exe_.setFilePath("C:\\Users\\Chandra-Kautilya\\Documents\\selenium-server-standalone-3.141.59.jar");
//
//		Thread task = new Thread(exe_, "ServerThread");
//		task.start();
//		System.out.println("Thread Started other task can be taken now");


		CommandLine cmd = new CommandLine("TaskList");
		cmd.addArgument("/fi");
		cmd.addArgument("\"imagename eq calc.exe\"");
		
//		DefaultExecutor executor = new DefaultExecutor();
//		ExecuteWatchdog watch = new ExecuteWatchdog(60*1000);
//		ResultHandler handler = main.new ResultHandler(watch);
//		
//		executor.setWatchdog(watch);
//		
//		try {
//			
//			executor.execute(cmd, handler);
//			System.out.println("Execution Started");
//			
//		} catch (ExecuteException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		

//
		System.out.println("Starting execution...");
		new NativeTask().runCommand(cmd, true);
		System.out.println("Execution Completed... ");
		System.out.println(" --- - -- -- -- Execution Started");

	}

	private class CommandExecutor implements Runnable{

		CommandLine command;
		String serverPath = "";

		public void setFilePath(String path) {
			this.serverPath=path ;
		}

		private String getFilePath() {
			return serverPath;
		}

		public void run() {

			try {

				command = new CommandLine("java");
				command.addArgument("-jar");
				command.addArgument(getFilePath());

				DefaultExecutor executor = new DefaultExecutor();
				executor.setExitValue(1);
				ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
				executor.setWatchdog(watchdog);

				ResultHandler handler = new ResultHandler(watchdog);

				System.out.println("Starting execution ... ");
				executor.execute(command, handler);

			} 
			catch (ExecuteException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}

		}

	}


	class ResultHandler extends DefaultExecuteResultHandler{

		private ExecuteWatchdog watchdog;

		public ResultHandler(final ExecuteWatchdog watchdog) {

			super();
			this.watchdog = watchdog;
		}

		@Override
		public void onProcessComplete(int exitValue) {
			super.onProcessComplete(exitValue);
			System.out.println("Execution completed");
		}

		@Override
		public void onProcessFailed(ExecuteException e) {
			super.onProcessFailed(e);

			if (watchdog!=null && watchdog.killedProcess()) {
				System.err.println("[resultHandler] Process time out");;
			}
			else {
				System.err.println("[resultHandler] Process failed to do "+e.getMessage());
			}
		}
	}


}
