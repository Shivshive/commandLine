package commandutility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.LogOutputStream;
import org.apache.commons.exec.PumpStreamHandler;

public class NativeTask{
	
	private class CollectingLogOutputStream extends LogOutputStream {

		public List<String> lines = new ArrayList<String>();
		
		@Override 
		protected void processLine(String line, int level) {
			lines.add(line);
			System.out.println("[CollectingLogOutputStream] printing > "+line);
		}   
		public List<String> getLines() {
			return lines;
			
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
			System.out.println("[resultHandler] Process Completed !");
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
	
	public List<String> runCommand(CommandLine cmd, boolean executeAsync) {
		
		CollectingLogOutputStream logStream = null;
		CollectingLogOutputStream errStream = null;
		try {

			logStream = new CollectingLogOutputStream ();
			errStream = new CollectingLogOutputStream();

			PumpStreamHandler psh = new PumpStreamHandler(logStream,errStream);
			DefaultExecutor executor = new DefaultExecutor();
			ExecuteWatchdog watchDog = new ExecuteWatchdog(60*1000);
			
			executor.setStreamHandler(psh);
			executor.setWatchdog(watchDog);
			
			if (executeAsync) {
//				DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler	// Using the custom result handler.. 
				ResultHandler handler = new ResultHandler(watchDog);
				executor.execute(cmd, handler);
			}
			else {
				int result = executor.execute(cmd);
				System.out.println("Process exited with exit code : "+result);
			}
			
			System.out.println("ExecutionCompleted .. ");
			
			return logStream.getLines();
		}
		catch (ExecuteException e) {
			e.printStackTrace();
			return errStream.getLines();

		} catch (IOException e) {
			e.printStackTrace();
			return errStream.getLines();
		}
	}
}




