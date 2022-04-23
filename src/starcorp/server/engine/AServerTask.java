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

import org.apache.commons.logging.Log;

import starcorp.server.Util;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.engine.AServerTask
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 22 Sep 2007
 */
public abstract class AServerTask implements Runnable {
	protected ServerEngine engine;
	protected IEntityStore entityStore;
	protected int taskNumber;
	private boolean done = false;
	protected long startTime;
	protected long endTime;
	protected long timeTaken;
	
	public AServerTask() {
	}
	
	public void setEngine(ServerEngine engine) {
		this.engine = engine;
	}
	
	public void setEntityStore(IEntityStore entityStore) {
		this.entityStore = entityStore;
	}
	
	public void setTaskNumber(int number) {
		this.taskNumber = number;
	}
	
	protected Object getProperty(String name) {
		return engine.getProperty(name);
	}
	
	protected void setProperty(String name, Object value) {
		engine.setProperty(name, value);
	}
	
	public void run() {
		try {
			startTime = System.currentTimeMillis();
			if(getLog().isDebugEnabled())
				getLog().debug("[Task: " + taskNumber +"] started.");
			doJob();
			
			endTime = System.currentTimeMillis();
			timeTaken = endTime - startTime;
			if(getLog().isDebugEnabled())
				getLog().debug("[Task: " + taskNumber +"] done in " + Util.getDisplayDuration(timeTaken) + ".");
		}
		catch(Exception e) {
			getLog().error("[Task: " + taskNumber +"] "+ e.getMessage(),e);
			onException(e);
		}
		engine.done(this);
		done = true;
	}
	
	protected void onException(Exception e) {
		// do nothing
	}
	
	public boolean isDone() {
		return this.done;
	}
	
	public boolean isHighPriority() {
		return false;
	}
	
	protected abstract void doJob() throws Exception;
	protected abstract Log getLog();

	protected String getName() {
		String name = getClass().getSimpleName();
		if(name == null || name.length() == 0) {
			name = getClass().getName();
			int end = name.lastIndexOf("$");
			if(end == -1) end = name.length() - 1;
			name = name.substring(name.lastIndexOf(".") + 1, end);
		}
		return name;
	}
	
	@Override
	public String toString() {
		long running = timeTaken;
		if(timeTaken < 1 && startTime > 0) {
			running = System.currentTimeMillis() - startTime;
		}
		String s = Util.getDisplayDuration(running);
		return "[" + taskNumber + " : " + s + "] " + getName();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + taskNumber;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final AServerTask other = (AServerTask) obj;
		if (taskNumber != other.taskNumber)
			return false;
		return true;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getTimeTaken() {
		return timeTaken;
	}
}
