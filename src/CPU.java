/**
 * This class implements functionality associated with the CPU device of the
 * simulated system.
 */
public class CPU implements Constants {
	/** The queue of processes waiting for CPU time */
	private Queue queue;

	/** The amount of max CPU time for any process */
	private long maxCpuTime;

	/** Current process in CPU */
	private Process cp = null;

	/** How long the process has been current process */
	private long cpCpuTime = 0;

	/** Refference to the GUI */
	private Gui gui;
	
	/** Refference to the RAM */
	private Memory memory;

	/** Class name used for debug messages */
	private final static String CLASS_NAME = "CPU";

	/**
	 * Creates a new memory device with the given parameters.
	 * 
	 * @param cpuQueue - The CPU queue to be used.
	 * @param cpuSize - Max CPU Time for any process.
	 * @param stats - A reference to the statistics collector.
	 * @param gui - 
	 * @param memory -
	 */
	public CPU(Queue cpuQueue, long maxCpuTime, Gui gui, Memory memory) {
		this.queue = cpuQueue;
		this.maxCpuTime = maxCpuTime;
		this.gui = gui;
		this.memory = memory;
	}

	/**
	 * Adds a process to the CPU queue.
	 * 
	 * @param p - The process to be added.
	 */
	public void insertProcess(Process p) {
<<<<<<< HEAD:CPU.java
		this.queue.insert(p);
	}

	/**
	 * Get next process in queue
	 * 
	 * @return Returns next {@code Process} in {@code Queue} if any;
	 * 			{@code null} if the {@code Queue} is null. 
	 */
	private Process getNextProcess() {
		if (!this.queue.isEmpty()) {
			return (Process) this.queue.removeNext();
		} else {
			return null;
		}
	}
	
	/**
	 * This method is called when a process is exiting the system. The memory
	 * allocated to this process is freed.
	 * 
	 * @param p The process that is leaving the system.
	 */
	public void processCompleted(Process p) {
		p.updateStatistics();
	}

	/**
	 * Get the minimum of three values
	 * 
	 * @param time1 {@code long} 1st time
	 * @param time2 {@code long} 2nd time
	 * @param time3 {@code long} 3rd time
	 * @return Returns the minimum {@code long} value
	 */
	private long min(long time1, long time2, long time3) {
		return Math.min(time1, Math.min(time2, time3));
=======
		Debug.print(CLASS_NAME, "insertProcess", "PID '" + p.getProcessId()
				+ "'");
		cpuQueue.insert(p);
		this.cpuQueue.insert(p);
>>>>>>> 7a157f874921917814194b43392e266ca48c854a:src/CPU.java
	}

	/**
	 * Load next process in queue into CPU
	 * 
	 * @param timePassed Amount of time passed since last update
	 */
	private void loadNextProcess(long timePassed) {
		// Get next process
		this.cp = this.getNextProcess();
		this.cpCpuTime = 0;

		// There is a processes waiting in the queue
		if (this.cp != null) {
			this.gui.setCpuActive(this.cp);

			// Update the process with time passed
			if (timePassed > 0) {
				this.updateCurrentProcess(timePassed);
			}

		// No process waiting in queue
		} else {
			// Log CPU Idle Time
			Statistics.cpuIdleTime(timePassed);
			
			this.gui.setCpuActive(null);

			this.cp = null;
			this.cpCpuTime = 0;
		}
	}
	
	/**
	 * Update time in CPU for Current Process
	 * 
	 * @param timePassed Amount of time passed since last update
	 */
	public void updateCurrentProcess(long timePassed) {
		// There is a process currently in the CPU
		if (this.cp != null) {

			boolean nextProcess = false;
			boolean isFinished = true;

			long maxTime = this.min(timePassed, (this.maxCpuTime - this.cpCpuTime), this.cp.getRemainingCPUTime());
			long timeRemaining = timePassed - maxTime;

			// Increment max time for process
			this.cp.incrementCPUTime(maxTime);
			this.cpCpuTime += maxTime;

			// Max CPU time reached
			if (this.cpCpuTime == this.maxCpuTime) {
				nextProcess = true;
			}

			// Process still not finished
			if (this.cp.getRemainingCPUTime() > 0) {
				isFinished = false;

			// Process is finished
			} else if (this.cp.getRemainingCPUTime() == 0) {
				nextProcess = true;
				isFinished = true;
			}

			if (nextProcess) {
				if (!isFinished) {
					// Update force process changes count
					Statistics.processForceChange();

					// Re insert the process at the end of the queue
					this.insertProcess(this.cp);
				} else {
					// Update finished processes count
					Statistics.processCompleted();

					// Free up memmory
					this.memory.releaseMemory(this.cp);
				}
				
				// Load next process
				this.loadNextProcess(timeRemaining);
			}
		} else {
			// Load next process
			this.loadNextProcess(timePassed);
		}
	}

	/**
	 * This method is called when a discrete amount of time has passed.
	 * 
	 * @param timePassed The amount of time that has passed since the last call
	 *            to this method.
	 */
	public void timePassed(long timePassed) {
		// Update some statistics
		Statistics.cpuQueueLengthTime(queue.getQueueLength(), timePassed);
		Statistics.cpuQueueLength(queue.getQueueLength());

		// Get next process if there is no current
		if (this.cp == null) {
			Statistics.cpuIdleTime(timePassed);
			
			this.loadNextProcess(0);

		// Update current process
		} else {
			this.updateCurrentProcess(timePassed);
		}
	}
}
