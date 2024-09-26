/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.SystemPerformanceData;
import java.util.logging.Logger;
import java.util.logging.Level;
/**
 * Shell representation of class for student implementation.
 * 
 */
public class SystemPerformanceManager
{
	// private var's
	private ScheduledExecutorService schedExecSvc = null;
	private SystemCpuUtilTask sysCpuUtilTask = null;
	private SystemMemUtilTask sysMemUtilTask = null;

	private Runnable taskRunner = null;
	private boolean isStarted = false;

	private static final Logger _Logger = Logger.getLogger(SystemPerformanceManager.class.getName());
	private int pollRate = ConfigConst.DEFAULT_POLL_CYCLES;

	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public SystemPerformanceManager() {
	    this.pollRate = ConfigUtil.getInstance().getInteger(
	        ConfigConst.GATEWAY_DEVICE, ConfigConst.POLL_CYCLES_KEY, ConfigConst.DEFAULT_POLL_CYCLES);

	    if (this.pollRate <= 0) {
	        this.pollRate = ConfigConst.DEFAULT_POLL_CYCLES;
	    }

	    this.schedExecSvc = Executors.newScheduledThreadPool(1);
	    this.sysCpuUtilTask = new SystemCpuUtilTask();
	    this.sysMemUtilTask = new SystemMemUtilTask();

	    this.taskRunner = () -> {
	        this.handleTelemetry();
	    };
	}


	
	
	// public methods
	
	public void handleTelemetry() {
	    float cpuUtil = this.sysCpuUtilTask.getTelemetryValue();
	    float memUtil = this.sysMemUtilTask.getTelemetryValue();

	    // Log telemetry results at the info level
	    _Logger.info("Handle telemetry results: cpuUtil=" + cpuUtil + ", memUtil=" + memUtil);
	}



	
	public void setDataMessageListener(IDataMessageListener listener)
	{
	}
	
	public boolean startManager() {
	    if (!this.isStarted) {
	        _Logger.info("SystemPerformanceManager is starting...");

	        // Scheduling the taskRunner
	        this.schedExecSvc.scheduleAtFixedRate(this.taskRunner, 1L, this.pollRate, TimeUnit.SECONDS);

	        this.isStarted = true;
	    } else {
	        _Logger.info("SystemPerformanceManager is already started.");
	    }

	    return this.isStarted;
	}


	
	public boolean stopManager() {
	    this.schedExecSvc.shutdown();
	    this.isStarted = false;

	    _Logger.info("SystemPerformanceManager is stopped.");

	    return true;
	}


	
}
