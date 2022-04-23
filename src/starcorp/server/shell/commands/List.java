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
package starcorp.server.shell.commands;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.List
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class List extends ACommand {
	private static Log log = LogFactory.getLog(List.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "list (Entity Class)\n\nLists entities of specified class.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "list";
	}

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			@Override
			public boolean isHighPriority() {
				return true;
			}
			protected String getName() {
				return "list";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				String entityClass = args.get(0);
				int page = args.getAsInt(1);
				
				if(entityClass == null) {
					out.print("Invalid arguments");
				}
				else {
					String className = "starcorp.common.entities." + entityClass;
					Class clazz = Class.forName(className);
					java.util.List<?> list = entityStore.listEntities(clazz);
					int size = list.size();
					if(size < 1) {
						out.println();
						out.println("No entities found.");
					}
					else {
						if(page < 1)
							page = 1;
						int start = (page - 1) * 10;
						if(start >= size) {
							out.println();
							out.println("Invalid page");
						}
						else {
							int totalPages = size / 10;
							if(size % 10 > 0)
								totalPages++;
							int end = start + 10;
							if(end > size)
								end = size;
							out.println();
							out.println(entityClass + " showing " + page + " of " + totalPages + ":");
							for(int i = start; i < end; i++) {
								out.println((i + 1) + ": " + list.get(i));
							}
						}
					}
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
