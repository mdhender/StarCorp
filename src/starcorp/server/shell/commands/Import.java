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
 * starcorp.server.shell.commands.Import
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 4 Oct 2007
 */
public class Import extends ACommand {
	private static final Log log = LogFactory.getLog(Import.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "Import (Filename)\n\nImports the entities specified in the file. If there is an ID conflict, the entity will NOT be imported.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "import";
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
				return "import";
			}

			protected Log getLog() {
				return log;
			}

			protected void doJob() throws Exception {
				String fileName = args.get(0);
				if(fileName == null) {
					out.println();
					out.println("Invalid arguments.");
				}
				else {
					List<IEntity> entities = Util.readXML(fileName);
					log.info("Found " + entities.size() + " to import.");
					int imported = 0;
					int failed = 0;
					for(IEntity entity : entities) {
						try {
							entityStore.importEntity(entity);
							log.info("Imported " + entity.getDisplayName());
							imported++;
						}
						catch(Throwable e) {
							log.error("Failed to import " + entity.getDisplayName(),e);
							failed++;
						}
					}
					out.println();
					out.println("Imported " + imported + " entities. Failed to import " + failed +" entities");
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
