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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.HQL
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class HQL extends ACommand {
	private static Log log = LogFactory.getLog(HQL.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "hql (Query)\n\nPerforms a Hibernate Query using the provided query string.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "hql";
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
				return "hql";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				String hql = args.concat(0);
				if(hql == null || hql.length() < 1) {
					out.println();
					out.println("Invalid argument.");
				}
				else {
					List<?> list = entityStore.query(hql);
					if(list == null || list.size() < 1) {
						out.println();
						out.println("0 results.");
					}
					else {
						out.println();
						for(Object o : list) {
							out.println(o);
						}
					}
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
