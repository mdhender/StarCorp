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

import starcorp.common.entities.IEntity;
import starcorp.common.util.Util;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Export
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 4 Oct 2007
 */
public class Export extends ACommand {
	private static final Log log = LogFactory.getLog(Export.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "Export (Entity Class) (ID)\nExports the specified entity (or all of a class if no ID is specified) to an XML file called export.xml.  If no entity class or ID is specified everything is exported.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "export";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#task(starcorp.server.shell.ACommand.Arguments, java.io.PrintWriter)
	 */
	@Override
	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			protected String getName() {
				return "export";
			}

			protected Log getLog() {
				return log;
			}

			protected void doJob() throws Exception {
				String entityClass = args.get(0);
				int ID = args.getAsInt(1);
				String msg = null;
				if(entityClass != null) {
					String className = "starcorp.common.entities."
							+ entityClass;
					Class<?> clazz = Class.forName(className);
					if(ID > 0) {
						IEntity o = entityStore.load(clazz, ID);
						if (o == null) {
							out.println();
							out.println("No such entity.");
						} else {
							Util.write(o, "export.xml", "starcorp", true);
							msg = o.getDisplayName();
						}
					}
					else {
						List<IEntity> entities = entityStore.list(clazz);
						Util.write(entities,"export.xml","starcorp", true);
						msg = entities.size() + " of " + clazz.getSimpleName();
					}
				}
				else {
					List<IEntity> entities = entityStore.listAll();
					Util.write(entities,"export.xml","starcorp", true);
					msg = entities.size() + " entities.";
				}
				out.println();
				out.println("Exported " + msg);
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
