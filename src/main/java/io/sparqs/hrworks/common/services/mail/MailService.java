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

    /**
     * send textual mail with sender mail address, subject of the mail,
     * optional multiple receiver mail addresses and textual content as {@link String}
     *
     * @param from sender mail address
     * @param subject subject the mail
     * @param textualContent textual content as {@link String}
     * @param to optional multiple receiver mail addresses
     * @throws IOException if serialization of {@link Mail} or {@link Request} fails or not supported http method is set
     */
    public void sendTextualMail(String from, String subject, String textualContent, String... to) throws IOException {
        final Mail mail = createMail(from, subject, to);
        mail.addContent(new Content("text/plain", textualContent));
        sendMail(mail);
    }

    /**
     * send exceptional mail with sender mail address, subject of the mail,
     * optional multiple receiver mail addresses and {@link ExceptionalMailContent}
     *
     * @param from sender mail address
     * @param subject subject the mail
     * @param exceptionalMailContent exceptional mail content with the stacktrace and message of a throwable and custom message
     * @param to optional multiple receiver mail addresses
     * @throws IOException if serialization of {@link Mail}, {@link Request} or {@link ExceptionalMailContent} fails or not supported http method is set
     */
    public void sendExceptionalMail(
            String from, String subject,
            ExceptionalMailContent exceptionalMailContent,
            String... to
    ) throws IOException {
        sendTextualMail(from, subject, new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValueAsString(exceptionalMailContent), to);
    }

    /**
     * send mail with previously created {@link Mail}
     *
     * @param mail previously created {@link Mail}
     * @throws IOException if serialization of {@link Mail} or {@link Request} fails or not supported http method is set
     */
    private void sendMail(Mail mail) throws IOException {
        final Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());
        logger.info("Sending email with " + new ObjectMapper().writeValueAsString(request));
        Response response = sendGrid.api(request);
        logger.info("Email sent with status code " + response.getStatusCode());
    }

    /**
     * create {@link Mail} with sender mail address, subject of the mail and optional multiple receiver mail addresses
     *
     * @param from sender mail address
     * @param subject subject the mail
     * @param to optional multiple receiver mail addresses
     * @return created {@link Mail} addresses
     */
    private Mail createMail(String from, String subject, String... to) {
        final Personalization personalization = new Personalization();
        final Mail mail = new Mail();
        mail.setFrom(new Email(from));
        mail.setSubject(subject);
        mail.addPersonalization(personalization);
        Arrays.stream(to).map(Email::new).forEach(personalization::addTo);
        return mail;
    }
}
