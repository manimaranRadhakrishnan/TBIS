package com.cissol.core.mail;

import java.io.File;
import java.util.Properties;

import com.sun.mail.smtp.SMTPTransport;

import jakarta.mail.Authenticator;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class PushMail implements Runnable {
	private String type = "";
	private String toAddresses = null;
	private String ccAddresses = null;
	private String bccAddresses = null;
	private String content = "";
	private String contentType = "";
	private String subject = "";
	private String attachmentFilePath = "";
	private String host = "";
	private String fromAddress = "";

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getIsAuth() {
		return isAuth;
	}

	public void setIsAuth(String isAuth) {
		this.isAuth = isAuth;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String port = "";
	private String isAuth = "";
	private String password = "";

	public String getBccAddresses() {
		return bccAddresses;
	}

	public void setBccAddresses(String bccAddresses) {
		this.bccAddresses = bccAddresses;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getToAddresses() {
		return toAddresses;
	}

	public void setToAddresses(String toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getCcAddresses() {
		return ccAddresses;
	}

	public void setCcAddresses(String ccAddresses) {
		this.ccAddresses = ccAddresses;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAttachmentFilePath() {
		return attachmentFilePath;
	}

	public void setAttachmentFilePath(String attachmentFilePath) {
		this.attachmentFilePath = attachmentFilePath;
	}

	public void sendMail() {
		Properties properties = System.getProperties();
		try {
			properties.setProperty("mail.smtp.host", host);
			properties.setProperty("mail.smtp.auth", isAuth);
			properties.setProperty("mail.smtp.user", fromAddress);
			properties.setProperty("mail.smtp.password", password);
			properties.setProperty("mail.smtp.port", port);
			properties.setProperty("mail.smtp.starttls.enable", "true");
			properties.setProperty("mail.debug", "true");
			SMTPAuthenticator mailAuth = new SMTPAuthenticator();
			mailAuth.setUserName(fromAddress);
			mailAuth.setPassword(password);
			Session session = Session.getInstance(properties, mailAuth);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			if (toAddresses != null) {
				String[] addresses = toAddresses.split(",");
				int s = addresses.length;
				InternetAddress[] ia = new InternetAddress[s];
				for (int i = 0; i < s; i++) {
					String addr = addresses[i];
					ia[i] = new InternetAddress(addr);
				}
				message.setRecipients(RecipientType.TO, ia);
			}
			if (ccAddresses != null) {
				String[] addresses = ccAddresses.split(",");
				int s = addresses.length;
				InternetAddress[] ic = new InternetAddress[s];
				for (int i = 0; i < s; i++) {
					String addr = addresses[i];
					ic[i] = new InternetAddress(addr);
				}
				message.setRecipients(RecipientType.CC, ic);
			}
			if (bccAddresses != null) {
				String[] addresses = bccAddresses.split(",");
				int s = addresses.length;
				InternetAddress[] ib = new InternetAddress[s];
				for (int i = 0; i < s; i++) {
					String addr = addresses[i];
					ib[i] = new InternetAddress(addr);
				}
				message.setRecipients(RecipientType.BCC, ib);
			}
			message.setSubject(subject);
			MimeMultipart m = new MimeMultipart();
			MimeBodyPart part = new MimeBodyPart();
			part.setContent(content, contentType);
			m.addBodyPart(part);
			if (attachmentFilePath != null && !"".equals(attachmentFilePath)) {
				MimeBodyPart attachment = new MimeBodyPart();
				attachment.attachFile(new File(attachmentFilePath));
				m.addBodyPart(attachment);
			}
			message.setContent(m);
			SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
			t.connect();
			t.send(message);
			t.close();
		} catch (Exception mex) {
			mex.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			sendMail();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

class SMTPAuthenticator extends Authenticator {
	private String userName = "";
	private String password = "";

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public SMTPAuthenticator() {
		super();
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		if ((userName != null) && (userName.length() > 0) && (password != null) && (password.length() > 0)) {

			return new PasswordAuthentication(userName, password);
		}

		return null;
	}
}