package io.sparqs.hrworks.common.services.mail;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final SendGrid sendGrid;

    MailService(SendGrid sendGrid) {
        this.sendGrid = sendGrid;
    }

    public void sendExceptionalMail(String from, String subject, ExceptionalMailContent exceptionalMailContent, String... toMailAddress) throws IOException {
        final Personalization personalization = new Personalization();
        final Mail mail = new Mail();
        mail.setFrom(new Email(from));
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        mail.addContent(new Content("text/plain", new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(exceptionalMailContent)));
        Arrays.stream(toMailAddress).map(Email::new).forEach(personalization::addTo);
        final Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        logger.info("Sending email with " + new ObjectMapper().writeValueAsString(request));
        Response response = sendGrid.api(request);
        logger.info("Email sent with status code " + response.getStatusCode());
    }

}
