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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;

/**
 * starcorp.common.util.FetchEmail
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class FetchEmail {

	public static class Attachment {
		public String contentType;
		public String filename;
		public byte[] content;
		public String contentId;
	}
	
	public static interface IEmail {
		public Attachment getAttachment(int i);
		public int countAttachments();
		public String getBodyText();
		public String getSubject();
		public String getFrom();
	}
	
	public static class PlainTextEmail implements IEmail {
		private String bodyText;
		private String subject;
		private String from;
		
		public PlainTextEmail(Message message) throws MessagingException, IOException {
			subject = message.getSubject();
			bodyText = (String) message.getContent();
			from = message.getFrom()[0].toString();
		}
		
		public int countAttachments() {
			return 0;
		}

		public Attachment getAttachment(int i) {
			return null;
		}

		public String getBodyText() {
			return bodyText;
		}

		public String getFrom() {
			return from;
		}

		public String getSubject() {
			return subject;
		}
		
	}

	public static class MultipartEmail implements IEmail {
		private String bodyText;
		private String subject;
		private String from;
		private ArrayList<Attachment> attachments;

		public MultipartEmail(Message message) throws MessagingException, IOException {
			subject=message.getSubject();
			from = message.getFrom()[0].toString();
			attachments=new ArrayList<Attachment>();
			extract(message);
		}
		
		private void extract(final Part part)  throws MessagingException, IOException {
	        if(part.getContent() instanceof Multipart) {
	            Multipart mp=(Multipart)part.getContent();
	            for(int i=0;i<mp.getCount();i++) {
	                extract(mp.getBodyPart(i));
	            }
	            return;
	        }
	        
	        if(part.getContentType().startsWith("text/html")) {
	            if(bodyText==null) {
	                bodyText=(String)part.getContent();
	            } else {
	                bodyText=bodyText+"<HR/>"+(String)part.getContent();
	            }
	        } else if(!part.getContentType().startsWith("text/plain")) {
	            Attachment attachment=new Attachment();
	            attachment.contentType=part.getContentType();
	            attachment.filename=part.getFileName();
	             
	            InputStream in=part.getInputStream();
	            ByteArrayOutputStream bos=new ByteArrayOutputStream();
	           
	            byte[] buffer=new byte[8192];
	            int count=0;
	            while((count=in.read(buffer))>=0) bos.write(buffer,0,count);
	            in.close();
	            attachment.content=bos.toByteArray();
	            attachments.add(attachment);
	            
	        }
	    }
		
		public int countAttachments() {
			return attachments.size();
		}

		public Attachment getAttachment(int i) {
			return attachments.get(i);
		}

		public String getBodyText() {
			return bodyText;
		}

		public String getFrom() {
			return from;
		}

		public String getSubject() {
			return subject;
		}
		
	}

    private String emailuser;
    private String emailpassword;
    private String emailserver;
    private String emailprovider;
    
    public FetchEmail(String emailuser,String emailpassword,String emailserver,String emailprovider) {
        this.emailuser=emailuser;
        this.emailpassword=emailpassword;
        this.emailserver=emailserver;
        this.emailprovider=emailprovider;
    }
    
    public List<IEmail> fetch() throws Exception {
    	ArrayList<IEmail> fetched = new ArrayList<IEmail>();
    	
    	Properties props = System.getProperties();
    	props.setProperty("mail.pop3s.rsetbeforequit","true");
        props.setProperty("mail.pop3.rsetbeforequit","true");
        Session session=Session.getInstance(props,null);
 //     session.setDebug(true);
    	
        Store store =session.getStore(emailprovider);
        store.connect(emailserver,emailuser,emailpassword);
        Folder folder=store.getDefaultFolder();
        if(folder==null) throw new Exception("No default folder");
        Folder inboxfolder=folder.getFolder("INBOX");
        if(inboxfolder==null) throw new Exception("No INBOX");
        inboxfolder.open(Folder.READ_WRITE);
        
        Message[] msgs=inboxfolder.getMessages();
        
        for(int i = 0; i < msgs.length; i++) {
        	IEmail email;
        	if(msgs[i].getContentType().startsWith("text/plain")) {
        		email = new PlainTextEmail(msgs[i]);
        	}
        	else {
        		email = new MultipartEmail(msgs[i]);
        	}
        	fetched.add(email);
        	msgs[i].setFlag(Flags.Flag.DELETED, true);
        }
        
//        folder.close(false);
        store.close();
        
    	return fetched;
    }
}
