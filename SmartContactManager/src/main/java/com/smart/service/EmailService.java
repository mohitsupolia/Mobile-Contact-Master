package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@org.springframework.stereotype.Service
public class EmailService 
{
public boolean SendEmail(String to,String subject,String message) {
		boolean f=false;
		//host
		String host="smtp.gmail.com";
		String from="supoliamohit8@gmail.com";
		// set Properties
		Properties properties=System.getProperties();
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.auth","true");
		properties.put("mail.smtp.ssl.enable","true");
		//Session 
		Session session=Session.getInstance(properties,new Authenticator() {
  protected PasswordAuthentication getPasswordAuthentication() 
            {
		  return new PasswordAuthentication("supoliamohit8@gmail.com","jxffgtmzufiaicmx");
			}
		   });
		session.setDebug(true);
		//compose Message
		MimeMessage m =new MimeMessage(session) ;
			try {
				//set from 
				m.setFrom(from);
				//Adding recepient 
				m.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
				//Add subject 
				m.setSubject(subject);
			//set Message 
			//	m.setText(message);
				m.setContent(message,"text/html");
				// send email 
				Transport.send(m);
				System.out.println("Done!!!!!");
				f=true;
			}catch(Exception e){
				e.printStackTrace();
				
			}
		return f;
		}
	}

