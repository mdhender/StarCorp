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
package starcorp.server.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.engine.AServerTask;
import starcorp.server.engine.ServerEngine;
import starcorp.server.entitystore.HibernateStore;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.shell.Shell
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Shell {
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		new Shell(args);
	}
	public static final String PROMPT = "> ";

	private final ServerEngine engine;
	private final CommandParser parser;
	private final Log log = LogFactory.getLog(Shell.class);
	private final IEntityStore entityStore;
	private final BufferedReader input;
	
	private LinkedList<String> history = new LinkedList<String>();
	
	public Shell(String[] args) {
		entityStore = new HibernateStore();
		engine = new ServerEngine(entityStore);
		parser = new CommandParser(this);
		input = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Welcome to Starcorp server!");
		System.out.flush();
		if(args != null && args.length > 0) {
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < args.length; i++) {
				sb.append(args[i]);
				sb.append(" ");
			}
			processCommandLine(sb.toString());
		}
		while(true) {
			processInput();
			Thread.yield();
		}
	}
	
	private void processInput() {
		String text = readCommandLine();
		try {
			processCommandLine(text);
		}
		catch(Exception e) {
			log.error(e.getMessage(),e);
		}
	}
	
	private void processCommandLine(String text) {
		AServerTask task = parser.parse(text);
		if(task == null) {
			return;
		}
		if(history.size() > 10) {
			history.remove();
		}
		history.add(text);
		try {
			engine.schedule(task);
			
		} catch (Throwable e) {
			System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			log.error(e.getMessage(),e);
		}
	}
	
	private String readCommandLine() {
		System.out.print(PROMPT);
		System.out.flush();
		String line = null;
		do {
			try {
				line = input.readLine();
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
		while(line == null);
		return line;
	}

	public List<String> getHistory() {
		return history;
	}
	
}
