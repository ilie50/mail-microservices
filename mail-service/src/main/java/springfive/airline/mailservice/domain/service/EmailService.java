package springfive.airline.mailservice.domain.service;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

import reactor.core.publisher.Flux;
import springfive.airline.mailservice.domain.Mail;

//import freemarker.template.TemplateException;

/**
 * Created by Olga on 8/22/2016.
 */
public interface EmailService {
	
	Flux<Boolean> send(Mail mail);
	
    void sendSimpleMessage(String to,
                           String subject,
                           String text);
    void sendSimpleMessageUsingTemplate(String to,
                                        String subject,
                                        String ...templateModel);
    void sendMessageWithAttachment(String to,
                                   String subject,
                                   String text,
                                   String pathToAttachment);
    /*
    void sendMessageUsingThymeleafTemplate(String to,
                                           String subject,
                                           Map<String, Object> templateModel) 
            throws IOException, MessagingException;

    void sendMessageUsingFreemarkerTemplate(String to,
                                            String subject,
                                            Map<String, Object> templateModel)
            throws IOException, TemplateException, MessagingException;
	*/
}