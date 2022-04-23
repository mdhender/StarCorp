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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import starcorp.common.util.PackageExplorer;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.shell.CommandParser
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class CommandParser {

	private final Shell shell;
	private Map<String, ACommand> commands = new HashMap<String, ACommand>();
	private PrintWriter out = new PrintWriter(System.out);
	
	public CommandParser(Shell shell) {
		initializeCommands();
		this.shell = shell;
	}
	
	public Iterator<String> listCommands() {
		return commands.keySet().iterator();
	}
	
	public ACommand getCommand(String command) {
		return commands.get(command);
	}
	
	private void initializeCommands() {
		List<Class> classes;
		try {
			classes = PackageExplorer.getClassesForPackage("starcorp.server.shell.commands");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return;
		}
		for(Class clazz : classes) {
			try {
				ACommand command = (ACommand) clazz.newInstance();
				command.setParser(this);
				commands.put(command.getName(), command);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public AServerTask parse(String text) {
		if(text == null || (text = text.trim()).length() == 0) {
			return null;
		}
		
		String[] args = text.split(" ");
		String commandName = args[0];
		ACommand command = getCommand(commandName);
		
		if(command == null) {
			return null;
		}
		
		String[] commandArgs = new String[args.length - 1];
		System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);
		return command.task(new ACommand.Arguments(commandArgs), out);
	}

	public Shell getShell() {
		return shell;
	}
	
}