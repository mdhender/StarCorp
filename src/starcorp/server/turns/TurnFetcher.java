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
package starcorp.server.turns;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import starcorp.common.entities.Corporation;
import starcorp.common.turns.Turn;
import starcorp.common.types.GalacticDate;
import starcorp.common.util.FetchEmail;
import starcorp.common.util.SendEmail;
import starcorp.common.util.FetchEmail.Attachment;
import starcorp.common.util.FetchEmail.IEmail;
import starcorp.common.util.FetchEmail.PlainTextEmail;
import starcorp.server.ServerConfiguration;
import starcorp.server.engine.AServerTask;


/**
 * starcorp.server.turns.TurnFetcher
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class TurnFetcher extends AServerTask {

	private final Log log = LogFactory.getLog(TurnFetcher.class);
	
	private final FetchEmail fetcher;
	private final SendEmail sender;
	
	private final File turnsFolder;
	private int fetched;
	
	public TurnFetcher() {
		fetcher = new FetchEmail(ServerConfiguration.FETCHER_USER,ServerConfiguration.FETCHER_PASSWORD,ServerConfiguration.FETCHER_SERVER,ServerConfiguration.FETCHER_PROVIDER);
		sender = new SendEmail(ServerConfiguration.SMTP_HOST_NAME,ServerConfiguration.SMTP_PORT,ServerConfiguration.SMTP_AUTH_USER,ServerConfiguration.SMTP_AUTH_PASSWORD);
		
		turnsFolder = new File(ServerConfiguration.TURNS_FOLDER);
		if(!turnsFolder.exists())
			turnsFolder.mkdirs();
		
	}
	
	public Log getLog() {
		return log;
	}
	
	public void doJob() {
		fetched = 0;
		try {
			List<IEmail> emails = fetcher.fetch();
			log.info("Fetched " + emails.size() + " emails.");
			Iterator<IEmail> i = emails.iterator();
			while(i.hasNext()) {
				if(handle(i.next()))
					fetched++;
			}
		} catch (Exception e) {
			log.fatal(e.getMessage(),e);
		}
		
	}
	
	private void forward(IEmail email) {
		String[] to = {ServerConfiguration.SERVER_EMAIL_BUGS};
		try {
			sender.send(to, null, null, email.getSubject(), email.getBodyText(), email.getFrom(), null);
		} catch (MessagingException e) {
			log.error(e.getMessage(),e);
		}
	}
	
	private boolean handle(IEmail email) {
		boolean saved = false;
		if(email instanceof PlainTextEmail) {
			forward(email);
		}
		else if(email.countAttachments() < 1 || !(saved = saveTurn(email))){
			forward(email);
		}
		return saved;
	}
	
	private boolean saveTurn(IEmail email) {
		boolean saved = false;
		int max = email.countAttachments();
		for(int i = 0; i < max; i++) {
			Attachment a = email.getAttachment(i);
			ByteArrayInputStream is = new ByteArrayInputStream(a.content);
			try {
				Turn turn = new Turn(is);
				Corporation corp = turn.getCorporation();
				GalacticDate date = ServerConfiguration.getCurrentDate();
				String filename = ServerConfiguration.TURNS_FOLDER + "/" + corp.getPlayerEmail() + "-turn-" + date.getMonth() + "-" +  date.getYear() + ".xml";
				turn.write(new FileWriter(filename));
				saved = true;
			}
			catch(Throwable e) {
				// ignore
				if(log.isDebugEnabled())
					log.debug(e.getMessage(),e);
			}
		}
		return saved;
	}

	public int getFetched() {
		return fetched;
	}
	
}
