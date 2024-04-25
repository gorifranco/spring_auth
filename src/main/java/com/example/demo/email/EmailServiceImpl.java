package com.example.demo.email;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.example.demo.config.ConfigManager;
import com.example.demo.models.EmailDetails;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

// Annotation
@Service
public class EmailServiceImpl implements EmailService {

	@Autowired private JavaMailSender javaMailSender;
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Value("${spring.mail.username}") private String sender;

	// Method 1
	// To send a simple email
	public boolean sendSimpleMail(EmailDetails details)
	{

		// Try block to check for exceptions
		try {

			// Creating a simple mail message
			SimpleMailMessage mailMessage
				= new SimpleMailMessage();

			// Setting up necessary details
			mailMessage.setFrom(ConfigManager.getString("mail_user"));
			mailMessage.setTo(ConfigManager.getString("mail_target"));
			mailMessage.setText(details.getMsgBody());
			mailMessage.setSubject(ConfigManager.getString("mail_subject"));

			// Sending the mail
			javaMailSender.send(mailMessage);
			logger.info("Email enviat a " + details.getRecipient());
			return true;
		}

		// Catch block to handle the exceptions
		catch (Exception e) {
			logger.warn("No s'ha pogut enviar el correu. Error: " + e.getMessage());
			return false;
		}
	}

	// Method 2
	// To send an email with attachment
	public String
	sendMailWithAttachment(EmailDetails details)
	{
		// Creating a mime message
		MimeMessage mimeMessage
			= javaMailSender.createMimeMessage();
		MimeMessageHelper mimeMessageHelper;

		try {

			// Setting multipart as true for attachments to
			// be send
			mimeMessageHelper
				= new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setFrom(ConfigManager.getString("mail_user"));
			mimeMessageHelper.setTo(ConfigManager.getString("mail_target"));
			mimeMessageHelper.setText(details.getMsgBody());
			mimeMessageHelper.setSubject(ConfigManager.getString("mail_subject"));

			// Adding the attachment
			FileSystemResource file
				= new FileSystemResource(
					new File(details.getAttachment()));

			mimeMessageHelper.addAttachment(
				file.getFilename(), file);

			// Sending the mail
			javaMailSender.send(mimeMessage);
			return "Mail sent Successfully";
		}

		// Catch block to handle MessagingException
		catch (MessagingException e) {

			// Display message when exception occurred
			return "Error while sending mail!!!";
		}
	}
}
