package io.sparqs.absenceimport.common.services.persons;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonEntityTest {

    private PersonEntity entity;

    @BeforeEach
    void setUp() {
        entity = new PersonEntity(
                "id",
                "firstname",
                "lastname",
                true,
                true,
                "mail@mail.com",
                "mobileNumber",
                "workPhone",
                "homeAddress",
                "info",
                "birthday",
                "avatar",
                "unit"
        );
    }

    @AfterEach
    void tearDown() {
        entity = null;
    }

    @Test
    void getId() {
        assertNotNull(entity.getId());
    }

    @Test
    void getFirstName() {
        assertNotNull(entity.getFirstName());
    }

    @Test
    void getLastName() {
        assertNotNull(entity.getLastName());
    }

    @Test
    void isActive() {
        assertTrue(entity.isActive());
    }

    @Test
    void isExtern() {
        assertTrue(entity.isExtern());
    }

    @Test
    void getMobilePhone() {
        assertNotNull(entity.getMobilePhone());
    }

    @Test
    void getWorkPhone() {
        assertNotNull(entity.getWorkPhone());
    }

    @Test
    void getHomeAddress() {
        assertNotNull(entity.getHomeAddress());
    }

    @Test
    void getBirthday() {
        assertNotNull(entity.getBirthday());
    }

    @Test
    void getAvatarUrl() {
        assertNotNull(entity.getAvatarUrl());
    }

    @Test
    void getUnitName() {
        assertNotNull(entity.getUnitName());
    }

    @Test
    void getInfo() {
        assertNotNull(entity.getInfo());
    }

    @Test
    void getEmail() {
        assertNotNull(entity.getEmail());
    }
}