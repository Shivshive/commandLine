package test_winium;

import org.testng.annotations.Test;
import static org.testng.Assert.assertEquals;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.ProcessDestroyer;
import org.apache.commons.exec.PumpStreamHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.winium.DesktopOptions;
import org.openqa.selenium.winium.WiniumDriver;
import org.openqa.selenium.winium.WiniumDriverService;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

public class CalculatorTest {

	DesktopOptions options;
	WiniumDriver app;
	WiniumDriverService service;
	String appPath = "C:\\Windows\\System32\\calc.exe";
	String winiumDesktopDriverPath = "C:\\Users\\Chandra-Kautilya\\Documents\\winium desktop driver\\Winium.Desktop.Driver.exe";
	WebDriverWait wait;
	
	private Map<String, String> runCommand(CommandLine command) {
		
		final String STATUS = "status";
		final String MSG = "message";
		final String PASS = "0";
		final String Error = "444";
		
		Map<String, String> output = new HashMap<String, String>();
		
		try {
			
			DefaultExecutor executor = new DefaultExecutor();
			ExecuteWatchdog watchDog = new ExecuteWatchdog(60*1000);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PumpStreamHandler psh = new PumpStreamHandler(baos);
			executor.setStreamHandler(psh);
			
			executor.setWatchdog(watchDog);
			int result = executor.execute(command);
			
			System.out.println("Command Executed ... ");
			System.out.println("-------------------------------------------------");
			System.out.println(baos.toString());
			System.out.println("-------------------------------------------------");
			
			String tempResult = baos.toString();

			baos.flush();
			baos.close();

			output.put(STATUS,Integer.toString(result));
			output.put(MSG, tempResult);
			
			return output;

		} catch (ExecuteException e) {
			e.printStackTrace();
			output.put(STATUS, Integer.toString(e.getExitValue()));
			output.put(MSG, e.getLocalizedMessage());
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			output.put(STATUS, Error);
			output.put(MSG, e.getLocalizedMessage());
			return output;
		}
		
	}
	

	private boolean killRunningServerThread() {

		DefaultExecutor executor = new DefaultExecutor();
		ExecuteWatchdog watchdog = new ExecuteWatchdog(60*1000);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PumpStreamHandler psh = new PumpStreamHandler(baos);

		executor.setStreamHandler(psh);
		executor.setWatchdog(watchdog);
		executor.setExitValue(0);

		CommandLine cmd1 = new CommandLine("TASKLIST");
		cmd1.addArgument("/FI");
		cmd1.addArgument("\"imagename eq Winium.Desktop.Driver.exe\"");

		try {

			executor.execute(cmd1);

			if(!(baos.toString().contains("No tasks are running"))) {

				System.out.println("Kill Process Started .... ");

				CommandLine cmd = new CommandLine("TaskKill");
				cmd.addArgument("/f");
				cmd.addArgument("/im");
				cmd.addArgument("Winium.Desktop.Driver.exe");
				baos.reset();

				try {
					//					executor.setExitValue(0);
					executor.execute(cmd);
					System.out.println("-------------------------------------------------------");
					System.out.println(baos.toString());
					System.out.println("-------------------------------------------------------");
					return watchdog.isWatching();

				} catch (ExecuteException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}

			}
			else {
				return true;
			}

		} catch (ExecuteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkAndKillApplication(String ApplicationName) {
		
		boolean flag = false;
		
		CommandLine isRunning = new CommandLine("TaskList");
		isRunning.addArgument("/fi");
		isRunning.addArgument("\"imagename eq "+ApplicationName+"\"");
		
		Map<String, String> result = runCommand(isRunning);
		
		if(Integer.parseInt(result.get("status"))==0 && !(result.get("message").contains("No tasks are running"))) {
			
			System.out.println(ApplicationName+" instance is already running.. hence attempting to close it.");
			CommandLine killApplication = new CommandLine("TaskKill");
			killApplication.addArgument("/f");
			killApplication.addArgument("/IM");
			killApplication.addArgument(ApplicationName);
			
			Map<String, String> result1 = runCommand(killApplication);
			if(Integer.parseInt(result1.get("status"))==0) {
				System.out.println(ApplicationName+" instance has been closed successfully..");
				flag = true;
			}
		}
		else {
			System.out.println(ApplicationName+" is not running already");
			flag = true;
		}
		
		return flag;
	}
	
	
	@Parameters({"appPath","winiumdriverpath"})
	@BeforeTest
	public void setup(String appPath, String winiumdriverpath) {

		try {

			System.out.println("Setting Up Test Environment....");
			options = new DesktopOptions();
			options.setApplicationPath(appPath);
			
			String appName = appPath.substring(appPath.lastIndexOf("\\")+1);
			System.out.println("Application under test "+appName);
			String winiumDriverName = winiumdriverpath.substring(winiumdriverpath.lastIndexOf("\\")+1);
			System.out.println("Winium Driver "+winiumDriverName);
			
			checkAndKillApplication(appName);

			Thread.sleep(5000);
			
			checkAndKillApplication(winiumDriverName);
			
			Thread.sleep(5000);

			service = new WiniumDriverService.Builder().usingDriverExecutable(new File(winiumDesktopDriverPath)).withVerbose(true).withSilent(false).usingPort(9999).buildDesktopService();
			service.start();
			Thread.sleep(5000);
			System.out.println("Service Started...");
			
			app = new WiniumDriver(service,options);
			
			Thread.sleep(5000);

		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} 
		catch (InterruptedException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void AddTest() {
				System.out.println("Executing Test Script...");
		//		wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("7")));
				app.findElement(By.name("7")).click();
				app.findElement(By.name("Add")).click();
				app.findElement(By.name("5")).click();
				app.findElement(By.name("Equals")).click();
				String result = app.findElement(By.id("150")).getAttribute("Name");
				assertEquals(Integer.parseInt(result), 12);	// Results = (actual == [7+5]=12)
	}

	@AfterTest
	public void tearDown() throws Exception {

				System.out.println("Tearing Down Test Case....");
				app.quit();
				System.out.println("Service Stopped...");
				service.stop();
	}

}
