package com.dummy.myerp.testbusiness.business;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestIntComptabiliteManager {

    private ComptabiliteManager vComptabiliteManager = BusinessTestCase.getBusinessProxy().getComptabiliteManager();


    @Test
    @Order(1)
    void insertEcritureComptableTest() {
        EcritureComptable vEcritureComptableSample = new EcritureComptable();
        vEcritureComptableSample.setJournal(new JournalComptable("VE", "Vente"));
        vEcritureComptableSample.setDate(Date.from(Instant.now()));
        vEcritureComptableSample.setLibelle("Vente TI");
        vEcritureComptableSample.setReference("VE-2019/00011");

        vEcritureComptableSample.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(606),
                null, new BigDecimal(123),
                null));
        vEcritureComptableSample.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(706),
                null, null,
                new BigDecimal(123)));

        assertDoesNotThrow(() -> vComptabiliteManager.insertEcritureComptable(vEcritureComptableSample));

    }

    @Test
    @Order(2)
    void checkEcritureComptableTest() throws NotFoundException, FunctionalException {
        EcritureComptable ec = getEcritureComptableByRef("VE-2019/00011");
        assertDoesNotThrow(() -> vComptabiliteManager.checkEcritureComptable(ec));
    }

    @Test
    @Order(3)
    void updateEcritureComptable() throws NotFoundException {
        EcritureComptable ec = getEcritureComptableByRef("VE-2019/00011");
        ec.setLibelle("modif libelle vente TI");

        assertDoesNotThrow(() -> vComptabiliteManager.updateEcritureComptable(ec));
    }

    @Test
    @Order(4)
    void insertOrUpdateSequenceEcritureComptable() {
        SequenceEcritureComptable sec = new SequenceEcritureComptable();
        sec.setAnnee(2019);
        sec.setJournalCode("VE");
        sec.setDerniereValeur(12);

        assertDoesNotThrow(() -> vComptabiliteManager.insertOrUpdateSequenceEcritureComptable(sec));
    }

    @Test
    @Order(5)
    void getSequenceByCodeJournalAndByAnneeCourante() {
        SequenceEcritureComptable sec = new SequenceEcritureComptable();
        sec.setAnnee(2019);
        sec.setJournalCode("VE");
        sec.setDerniereValeur(12);

        assertDoesNotThrow(() -> vComptabiliteManager.getSequenceByCodeJournalAndByAnneeCourante(sec));
    }

    @Test
    void getListCompteComptable() {
        List<CompteComptable> compteComptables = vComptabiliteManager.getListCompteComptable();
        assertFalse(compteComptables.isEmpty());
    }

    @Test
    void getListJournalComptable() {
        List<JournalComptable> journalComptables = vComptabiliteManager.getListJournalComptable();
        assertFalse(journalComptables.isEmpty());
    }

    @Test
    void getListEcritureComptable() {
        List<EcritureComptable> ecritureComptables = vComptabiliteManager.getListEcritureComptable();
        assertFalse(ecritureComptables.isEmpty());
    }


    @Test
    @Order(6)
    void deleteEcritureComptableTest() throws NotFoundException {
        EcritureComptable ec = getEcritureComptableByRef("VE-2019/00011");
        assertDoesNotThrow(() -> vComptabiliteManager.deleteEcritureComptable(ec.getId()));
    }



    /**
     *
     * @param ref, la référence de l'écriture comptable à chercher
     * @return EcritureComptable
     * @throws NotFoundException
     */
    private EcritureComptable getEcritureComptableByRef(String ref) throws NotFoundException {
        ComptabiliteDao comptabiliteDao = new ComptabiliteDaoImpl();
        return comptabiliteDao.getEcritureComptableByRef(ref);
    }
}
