/**
 *  Copyright 2007 Seyed Razavi
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and limitations under the License. 
 */
package starcorp.server.engine;

import java.text.DateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.Util;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.engine.ServerEngine
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 22 Sep 2007
 */
public class ServerEngine {
	private static final int THREAD_POOL = 10;
	private static final Log log = LogFactory.getLog(ServerEngine.class);
	private int taskNumber = 1;
	private ExecutorService highPriority = Executors.newCachedThreadPool();
	private ExecutorService execService = Executors.newFixedThreadPool(THREAD_POOL);
	private final IEntityStore entityStore;
	private Vector<AServerTask> tasks = new Vector<AServerTask>();
	private Properties props = System.getProperties();
	private long serverStarted = System.currentTimeMillis();
	private Date serverStartedDate = new Date(serverStarted);
	private String serverStartedString = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(serverStartedDate);
	private int tasksCompled = 0;
	private long totalTaskTime = 0;
	private long longestTaskTime = 0;
	private String longestTaskName = "";
	
	public ServerEngine(IEntityStore entityStore) {
		this.entityStore = entityStore;
		log.info("Server Engine started " + serverStartedString);
	}
	
	public void scheduleAndWait(AServerTask task) {
		schedule(task);
		while(!task.isDone()) {
			Thread.yield();
		}
	}
	
	public void schedule(AServerTask task) {
		task.setEngine(this);
		task.setTaskNumber(taskNumber);
		task.setEntityStore(entityStore);
		tasks.add(task);
		if(task.isHighPriority()) {
			if(log.isDebugEnabled())
				log.debug("[High Priority] Scheduled " + task);
			highPriority.execute(task);
		}
		else {
			if(log.isDebugEnabled())
				log.debug("Scheduled " + task);
			execService.execute(task);
		}
		taskNumber++;
	}
	
	public void shutdown() {
		execService.shutdownNow();
		entityStore.shutdown();
		if(log.isDebugEnabled())
			log.debug(this);
		log.info("Shutting down now!");
		System.exit(0);
	}
	
	synchronized void done(AServerTask task) {
		tasks.remove(task);
		if(task.getTimeTaken() > longestTaskTime) {
			longestTaskTime = task.getTimeTaken();
			longestTaskName = task.getName();
		}
		totalTaskTime += task.getTimeTaken();
		tasksCompled++;
	}
	
	public Enumeration<AServerTask> runningTasks() {
		return tasks.elements();
	}
	
	public long getTotalTime() {
		return totalTaskTime;
	}
	
	public long getLongestTaskTime() {
		return longestTaskTime;
	}
	
	public String getLongestTaskName() {
		return longestTaskName;
	}
	
	protected int getTasksCompleted() {
		return tasksCompled;
	}
	
	protected long getAverageTime() {
		if(tasksCompled > 1)
			return totalTaskTime / tasksCompled;
		return 0;
	}
	
	void setProperty(String name, Object value) {
		props.put(name, value);
	}
	
	Object getProperty(String name) {
		return props.get(name);
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Started: ");
		sb.append(serverStartedString);
		sb.append("\nTasks: ");
		sb.append(taskNumber = tasksCompled);
		sb.append(" scheduled ");
		sb.append(tasksCompled);
		sb.append(" completed\nTime: ");
		sb.append(Util.getDisplayDuration(getAverageTime()));
		sb.append(" average ");
		sb.append(Util.getDisplayDuration(getLongestTaskTime()));
		sb.append(" (");
		sb.append(getLongestTaskName());
		sb.append(") longest");
		return sb.toString();
	}
	
}
