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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import starcorp.common.entities.Corporation;
import starcorp.common.turns.TurnOrder;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.OrderType;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;
import starcorp.server.turns.TurnProcessor;

/**
 * starcorp.server.shell.commands.Turn
 * 
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class Turn extends ACommand {
	private static final Log log = LogFactory.getLog(Turn.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "turn corp (Corporation ID)\n\nStarts a new turn submission for the given corporation.\n\nturn order (Order) (Order Arguments)\n\nAdds a new order to the current turn.\n\nturn submit\n\nSubmits the current turn.\n\nturn save\n\nSaves current turn (and report if submitted) to file.\n\nturn load\n\nLoads current turn from saved file.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "turn";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#task(starcorp.server.shell.ACommand.Arguments,
	 *      java.io.PrintWriter)
	 */
	@Override
	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString()
						+ (args.count() > 0 ? " [" + args + "]" : "");
			}

			protected String getName() {
				return "turn";
			}

			protected Log getLog() {
				return log;
			}

			private void save() throws Exception {
				starcorp.common.turns.Turn turn = (starcorp.common.turns.Turn) getProperty("starcorp.turn");
				TurnReport report = (TurnReport) getProperty("starcorp.report");
				out.println();
				if (turn != null) {
					turn.write(new FileWriter("turn.xml"));
					out.println("Turn saved.");
				}
				if (report != null) {
					report.write(new FileWriter("report.xml"));
					out.println("Report saved.");
				}
			}

			protected void doJob() throws Exception {
				String action = args.get(0);
				if ("save".equalsIgnoreCase(action)) {
					save();
				} else if ("load".equalsIgnoreCase(action)) {
					SAXReader reader = new SAXReader();
					Document doc = reader.read(new File("turn.xml"));
					starcorp.common.turns.Turn turn = new starcorp.common.turns.Turn(
							doc.getRootElement().element("turn"));
					setProperty("starcorp.turn", turn);
					out.println();
					out.println("Turn loaded.");
				} else if ("corp".equalsIgnoreCase(action)) {
					int corpId = args.getAsInt(1);
					Corporation corp = (Corporation) entityStore.load(
							Corporation.class, corpId);
					if (corp == null) {
						out.println();
						out.println("Invalid corporation");
					} else {
						starcorp.common.turns.Turn turn = new starcorp.common.turns.Turn();
						turn.setCorporation(corp);
						setProperty("starcorp.turn", turn);
						out.println();
						out.println("New turn for " + corp + " in session.");
					}
				} else if ("order".equalsIgnoreCase(action)) {
					starcorp.common.turns.Turn turn = (starcorp.common.turns.Turn) getProperty("starcorp.turn");
					if (turn == null) {
						out.println();
						out
								.println("No turn yet.  Use turn corp (Corporation ID) first.");
					} else {
						OrderType type = OrderType.getType(args.get(1));
						if (type == null) {
							out.println();
							out.println("Invalid order type");
						} else {
							TurnOrder order = new TurnOrder();
							order.setCorp(turn.getCorporation());
							order.setType(type);
							for (int i = 2; i < args.count(); i++) {
								order.add(args.get(i));
							}
							turn.add(order);
							out.println();
							out.println("Order " + type
									+ " added to session turn");
						}
					}
				} else if ("submit".equalsIgnoreCase(action)) {
					starcorp.common.turns.Turn turn = (starcorp.common.turns.Turn) getProperty("starcorp.turn");
					if (turn == null) {
						out.println();
						out
								.println("No turn yet.  Use turn corp (Corporation ID) first.");
					} else {
						// refresh corporation on turn from entitystore
						Corporation corp = (Corporation) entityStore.load(Corporation.class, turn.getCorporation().getID());
						turn.setCorporation(corp);
						TurnProcessor processor = new TurnProcessor();
						processor.setEntityStore(entityStore);
						TurnReport report = processor.process(turn);
						setProperty("starcorp.report", report);
						out.println();
						out.println("Turn submitted. "
								+ report.getTurn().getErrors().size()
								+ " errors.");
						save();
						processor.zipAndEmail(corp, new File("report.xml"));
					}
				} else {
					out.println();
					out.println("Invalid arguments");
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
