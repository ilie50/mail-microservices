package springfive.airline.mailservice.domain.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
//import org.thymeleaf.context.Context;
//import org.thymeleaf.spring5.SpringTemplateEngine;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import springfive.airline.mailservice.domain.Mail;

//import freemarker.template.Template;
//import freemarker.template.TemplateException;

/**
 * Created by Olga on 7/15/2016.
 */
@Service("EmailService")
public class EmailServiceImpl implements EmailService {

    private static final String NOREPLY_ADDRESS = "noreply@ibdomain.tld";

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SimpleMailMessage template;
    
    //@Autowired
    //private SpringTemplateEngine thymeleafTemplateEngine;
    
    //@Autowired
    //private FreeMarkerConfigurer freemarkerConfigurer;
    
    //@Value("classpath:/mail-logo.png")
    //private Resource resourceFile;

    public void sendSimpleMessage(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(NOREPLY_ADDRESS);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void sendSimpleMessageUsingTemplate(String to,
                                               String subject,
                                               String ...templateModel) {
        String text = String.format(template.getText(), templateModel);  
        sendSimpleMessage(to, subject, text);
    }

    @Override
    public void sendMessageWithAttachment(String to,
                                          String subject,
                                          String text,
                                          String pathToAttachment) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            // pass 'true' to the constructor to create a multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(NOREPLY_ADDRESS);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Invoice", file);

            emailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

	@Override
	public Flux<Boolean> send(Mail mail) {
		return Mono.fromCallable(() -> {
			try {
	            MimeMessage message = emailSender.createMimeMessage();
	            // pass 'true' to the constructor to create a multipart message
	            MimeMessageHelper helper = new MimeMessageHelper(message, true);
	            helper.setTo(mail.getTo());
	            helper.setSubject(mail.getSubject());
	            helper.setText(mail.getMessage());
	            emailSender.send(message);
	            System.out.printf("Email send successfully, subject %s , to %s %n", mail.getSubject(), mail.getTo());
	            return true;
	        } catch (Exception e) {
	        	System.err.printf("Failed to send email with subject %s, due to %s",mail.getSubject(), e.getMessage());
	            return false;
	        }
		}).flux();
	}
    
    /*
    @Override
    public void sendMessageUsingThymeleafTemplate(
        String to, String subject, Map<String, Object> templateModel)
            throws MessagingException {
                
        Context thymeleafContext = new Context();
        thymeleafContext.setVariables(templateModel);
        
        String htmlBody = thymeleafTemplateEngine.process("template-thymeleaf.html", thymeleafContext);

        sendHtmlMessage(to, subject, htmlBody);
    }

    @Override
    public void sendMessageUsingFreemarkerTemplate(
        String to, String subject, Map<String, Object> templateModel)
            throws IOException, TemplateException, MessagingException {

        Template freemarkerTemplate = freemarkerConfigurer.getConfiguration().getTemplate("template-freemarker.ftl");
        String htmlBody = FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerTemplate, templateModel);

        sendHtmlMessage(to, subject, htmlBody);
    }
    
    private void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(NOREPLY_ADDRESS);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);
        helper.addInline("attachment.png", resourceFile);
        emailSender.send(message);
    }
    */
}