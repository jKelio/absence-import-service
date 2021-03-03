package io.sparqs.hrworks.common.services.mail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailServiceTest {

    @Autowired
    MailService service;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSendMail() throws IOException {
        service.sendExceptionalMail("leon.jaekel@sparqs.io", "Fehler beim Import der Abwesenheiten", ExceptionalMailContent.builder()
                .customMessage("Fehler beim importieren der Abwesenheiten von HRworks nach moco aufgetreten")
                .exception(new Exception("provoked exception"))
                .build(),
                "leon1jaekel@aol.com", "info@leon-jaekel.com");
        assertTrue(true);
    }
}