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

import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.MarketItem;
import starcorp.common.types.AItemType;
import starcorp.common.types.Items;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Inject
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class Inject extends ACommand {
	private static final Log log = LogFactory.getLog(Inject.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "inject market (Corporation ID) (Colony ID) (Type)\n\nInjects a random selection of items of the specified type put on sale by the corporation specified.\n\ninject items (Corporation ID) (Colony ID) (Type)\n\nnjects a random selection of items of the specified type into the inventory of the corporation specified.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "inject";
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
			public String getName() {return "inject";}
			protected void doJob() throws Exception {
				String type = args.get(0);
				int corporationId = args.getAsInt(1);
				int colonyId = args.getAsInt(2);
				
				Corporation corp = (Corporation) entityStore.load(Corporation.class,corporationId);
				Colony colony = (Colony) entityStore.load(Colony.class, colonyId);
				
				if(corp == null || colony == null) {
					out.println("Invalid arguments");
				}
				else if("market".equalsIgnoreCase(type)) {
					String className = "starcorp.common.types." + args.get(3);
					Class clazz = Class.forName(className);
					
					java.util.List<AItemType> types = AItemType.listTypes(clazz);
					if(types == null || types.size() < 1) {
						out.println("Invalid item category.");
					}
					else {
						for(AItemType itemType : types) {
							MarketItem item = new MarketItem();
							int price = (int) (entityStore.getAveragePrice(colony.getID(), itemType) * 1.10);
							item.setColony(colony.getID());
							item.setCostPerItem(price);
							item.setIssuedDate(ServerConfiguration.getCurrentDate());
							item.setItem(new Items(itemType));
							item.add(Util.rnd.nextInt(10000));
							item.setSeller(corp.getID());
							entityStore.create(item);
							log.info("Created " + item);
						}
					}
					out.println();
					out.println("Created market items of " + clazz.getSimpleName());
				}
				else if("items".equalsIgnoreCase(type)) {
					String className = "starcorp.common.types." + args.get(3);
					Class clazz = Class.forName(className);
					
					java.util.List<AItemType> types = AItemType.listTypes(clazz);
					if(types == null || types.size() < 1) {
						out.println("Invalid item category.");
					}
					else {
						for(AItemType itemType : types) {
							ColonyItem item = new ColonyItem();
							item.setColony(colony.getID());
							item.setItem(new Items(itemType));
							item.add(Util.rnd.nextInt(10000));
							item.setOwner(corp.getID());
							entityStore.create(item);
							log.info("Created " + item);
						}
					}
					out.println();
					out.println("Created items of " + clazz.getSimpleName());
				}
				else {
					out.println("Invalid type");
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
			protected Log getLog() {
				return log;
			}
		};
	}

}
