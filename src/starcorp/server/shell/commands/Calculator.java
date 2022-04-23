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

import starcorp.common.types.Coordinates2D;
import starcorp.common.types.Coordinates3D;
import starcorp.common.types.ICoordinates;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Calculator
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 29 Sep 2007
 */
public class Calculator extends ACommand {
	private static final Log log = LogFactory.getLog(Calculator.class);

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "calc [operator] [operands]\n\nA calculator which takes the operator as the left-hand argument and operands after. Valid operators are +, -, *, / and path.  Path measures the shortest path between two sets of coordinates (either 2-d or 3-d). There should be no spaces in coordinate elements.\n\nExample: calc path (1,2) (2,3).";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "calc";
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
				return "calc";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				int total = 0;
				String operator = args.get(0);
				if("+".equals(operator)) {
					for(int i = 1; i < args.count(); i++) {
						total += args.getAsInt(i);
					}
				}
				else if("-".equals(operator)) {
					total = args.getAsInt(1);
					for(int i = 2; i < args.count(); i++) {
						total -= args.getAsInt(i);
					}
				}
				else if("*".equals(operator)) {
					total = args.getAsInt(1);
					for(int i = 2; i < args.count(); i++) {
						total *= args.getAsInt(i);
					}
				}
				else if("/".equals(operator)) {
					total = args.getAsInt(1);
					for(int i = 2; i < args.count(); i++) {
						total += args.getAsInt(i);
					}
				}
				else if("path".equals(operator)) {
					out.println();
					out.println("Distance between " + args.get(1) + " to " + args.get(2) + ":");
					ICoordinates c1 = parseCoordinates(args.get(1));
					ICoordinates c2 = parseCoordinates(args.get(2));
					if(c1 != null && c2 != null)
						total = c1.getDistance(c2);
				}
				out.println();
				out.println(total);
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}
			
	
	private ICoordinates parseCoordinates(String s) {
		if(s==null)
			return null;
		if(s.startsWith("("))
			s = s.substring(1);
		if(s.endsWith(")"))
			s = s.substring(0, s.length()-1);
		String[] strarr = s.split(",");
		if(strarr.length == 2) {
			return new Coordinates2D(Integer.parseInt(strarr[0]),Integer.parseInt(strarr[1]));
		}
		else if(strarr.length == 3) {
			return new Coordinates3D(Integer.parseInt(strarr[0]),Integer.parseInt(strarr[1]),Integer.parseInt(strarr[2]));
		}
		return null;
	}

}
