package com.dummy.myerp.testconsumer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Classe de test de l'initialisation du contexte Spring
 */
class TestIntInitSpring extends ConsumerTestCase {

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
        assertNotNull(SpringRegistry.getDaoProxy());
    }
}
