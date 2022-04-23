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
package starcorp.client;

import java.io.IOException;
import starcorp.common.util.XMLConfiguration;

/**
 * starcorp.client.ClientConfiguration
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class ClientConfiguration {

	private static final XMLConfiguration config = new XMLConfiguration("client-config.xml");
	
	public synchronized static void save() {
		try {
			config.write("client-config.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String getSmtpHost() {
		return config.getValue("smtp-host");
	}
	
	public synchronized static void setSmtpHost(String value) {
		config.setValue("smtp-host", value);
	}
	
	public static int getSmtpPort() {
		return config.getValueAsInt("smtp-port");
	}
	
	public synchronized static void setSmtpPort(int value) {
		config.setValue("smtp-port", value);
	}
	
	public static String getSmtpUser() {
		return config.getValue("smtp-user");
	}
	
	public synchronized static void setSmtpUser(String value) {
		config.setValue("smtp-user", value);
	}
	
	public static String getSmtpPassword() {
		return config.getValue("smtp-password");
	}
	
	public synchronized static void setSmtpPassword(String value) {
		config.setValue("smtp-password", value);
	}
	

	public static String getServerEmailTurns() {
		return config.getValue("server-emails-turns","starcorp.turns@gmail.com");
	}

	public synchronized static void setServerEmailTurns(String value) {
		config.setValue("server-emails-turns", value);
	}
	
}
