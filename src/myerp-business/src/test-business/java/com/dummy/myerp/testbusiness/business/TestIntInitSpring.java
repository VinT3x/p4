package com.dummy.myerp.testbusiness.business;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;


/**
 * Classe de test de l'initialisation du contexte Spring
 */
public class TestIntInitSpring extends BusinessTestCase {

    /**
     * Constructeur.
     */
    TestIntInitSpring() {
        super();
    }


    /**
     * Teste l'initialisation du contexte Spring
     */
    @Test
    void testInit() {
        SpringRegistry.init();
        assertNotNull(SpringRegistry.getBusinessProxy());
        assertNotNull(SpringRegistry.getTransactionManager());
    }
}
