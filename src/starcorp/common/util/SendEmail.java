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
package starcorp.common.util;

import java.security.Security;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * starcorp.common.util.SendEmail
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 19 Sep 2007
 */
public class SendEmail {

	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	
	private String smtpHost;
	private int smtpPort;
	private String smtpUser;
	private String smtpPassword;
	
	public SendEmail(String smtpHost, int smtpPort, String smtpUser,
			String smtpPassword) {
		super();
		this.smtpHost = smtpHost;
		this.smtpPort = smtpPort;
		this.smtpUser = smtpUser;
		this.smtpPassword = smtpPassword;
	}

	public void send(String[] to, String[] cc, String[] bcc, String subject, String message, String from, String attachmentFile) throws MessagingException {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", smtpHost);
		props.put("mail.smtp.auth", "true");
		props.put("mail.stmp.port", String.valueOf(smtpPort));
//		props.put("mail.debug", "true");
		props.put("mail.smtp.port", String.valueOf(smtpPort));
		props.put("mail.smtp.socketFactory.port", smtpPort);
		props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.put("mail.smtp.socketFactory.fallback", "false");
//		System.out.println(props);
		Authenticator auth = new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(smtpUser,smtpPassword);
			}
			
		};
		Session session = Session.getInstance(props, auth);
		
		Message msg = new MimeMessage(session);
		
		InternetAddress addressFrom = new InternetAddress(from);
		msg.setFrom(addressFrom);
		
		InternetAddress[] addressTo = new InternetAddress[to.length];
		for(int i = 0; i < to.length; i++) {
			addressTo[i] = new InternetAddress(to[i]);
		}
		
		msg.setRecipients(Message.RecipientType.TO, toAddress(to));
		
		if(cc != null)
			msg.setRecipients(Message.RecipientType.CC, toAddress(cc));
		
		if(bcc != null)
			msg.setRecipients(Message.RecipientType.BCC, toAddress(bcc));

		msg.setSubject(subject);
		
		if(attachmentFile == null) {
			if(message != null)
				msg.setContent(message, "text/plain");
		}
		else {
			BodyPart msgBody = new MimeBodyPart();
			msgBody.setText(message);
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(msgBody);
			BodyPart msgAttachment = new MimeBodyPart();
			DataSource source = new FileDataSource(attachmentFile);
			msgAttachment.setDataHandler(new DataHandler(source));
			msgAttachment.setFileName(attachmentFile);
			multipart.addBodyPart(msgAttachment);
			msg.setContent(multipart);
		}
		
		Transport.send(msg);
	}
	
	private static InternetAddress[] toAddress(String[] addresses) throws AddressException {
		InternetAddress[] inetAddress = new InternetAddress[addresses.length];
		for(int i = 0; i < addresses.length; i++) {
			inetAddress[i] = new InternetAddress(addresses[i]);
		}
		return inetAddress;
	}
	
}
