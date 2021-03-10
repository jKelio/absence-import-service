package io.sparqs.absenceimport.common.services.mail;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MailServiceTest {

    public static final int EXPECTED_STATUS_CODE = 202;
    private SendGrid sendGrid;
    private io.sparqs.absenceimport.common.services.mail.MailService service;

    @BeforeEach
    void setUp() {
        sendGrid = mock(SendGrid.class);
        service = new io.sparqs.absenceimport.common.services.mail.MailService(sendGrid);
    }

    @AfterEach
    void tearDown() {
        sendGrid = null;
        service = null;
    }

    @Test
    void testPureTextualMail() throws IOException {
        when(sendGrid.api(any(Request.class)))
                .thenReturn(new Response(EXPECTED_STATUS_CODE, "", null));
        service.sendTextualMail(
                "leon.jaekel@sparqs.io",
                "Erfolgreicher Import der Abwesenheiten",
                "Alle Abwesenheiten wurden erfolgreich von HRworks nach moco importiert.",
                "leon1jaekel@aol.com", "info@leon-jaekel.com"
        );
        verify(sendGrid, times(1)).api(any(Request.class));
    }

    @Test
    void testSendExceptionalMail() throws IOException {
        when(sendGrid.api(any(Request.class)))
                .thenReturn(new Response(EXPECTED_STATUS_CODE, "", null));
        Throwable t = new Exception("provoked exception");
        io.sparqs.absenceimport.common.services.mail.ExceptionalMailContent content = io.sparqs.absenceimport.common.services.mail.ExceptionalMailContent.builder()
                .customMessage(t.getLocalizedMessage())
                .exception(t)
                .build();
        assertEquals(t, content.getException());
        service.sendExceptionalMail(
                "leon.jaekel@sparqs.io",
                "Fehler beim Import der Abwesenheiten",
                content,
                "leon1jaekel@aol.com", "info@leon-jaekel.com"
        );
        verify(sendGrid, times(1)).api(any(Request.class));
    }
}