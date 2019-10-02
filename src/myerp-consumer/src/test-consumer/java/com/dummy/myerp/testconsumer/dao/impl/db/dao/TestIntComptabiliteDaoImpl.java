package com.dummy.myerp.testconsumer.dao.impl.db.dao;

import com.dummy.myerp.consumer.dao.impl.db.dao.ComptabiliteDaoImpl;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.NotFoundException;

import com.dummy.myerp.testconsumer.ConsumerTestCase;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TestIntComptabiliteDaoImpl extends ConsumerTestCase {

    private static ComptabiliteDaoImpl dao;
    private static EcritureComptable vEcritureComptable;
    private static Date vCurrentDate;
    private static Integer vCurrentYear;

    @BeforeAll
    static void initAll() {
        dao = new ComptabiliteDaoImpl();
        vCurrentDate = new Date();
        vCurrentYear = LocalDateTime.ofInstant(vCurrentDate.toInstant(), ZoneId.systemDefault()).toLocalDate().getYear();
    }

    @BeforeEach
    void init() {
        vEcritureComptable = new EcritureComptable();
    }

    @AfterAll
    static void tearDownAll() {
        vEcritureComptable = null;
    }


    // ==================== CompteComptable - GET ====================

    @Test
    void getListCompteComptable() {
        List<CompteComptable> vList = dao.getListCompteComptable();
        assertEquals(7, vList.size());
    }


    // ==================== JournalComptable - GET ====================

    @Test
    void getListJournalComptable() {
        List<JournalComptable> vList = dao.getListJournalComptable();
        assertEquals(4, vList.size());
    }


    // ==================== EcritureComptable - GET ====================

    @Test
    void getListEcritureComptable() {
        List<EcritureComptable> vList = dao.getListEcritureComptable();
        assertFalse(vList.isEmpty());
    }

    @Test
    void getEcritureComptable() throws NotFoundException {
        EcritureComptable vEcritureComptable = dao.getEcritureComptable(-5);
        assertEquals("BQ-2016/00005", vEcritureComptable.getReference());

        assertThrows(NotFoundException.class, () -> dao.getEcritureComptable(0));
    }

    @Test
    void getEcritureComptableByRef() throws NotFoundException {
        EcritureComptable vEcritureComptable = dao.getEcritureComptableByRef("BQ-2016/00005");
        assertEquals("BQ", vEcritureComptable.getJournal().getCode());
        String vEcritureYear = new SimpleDateFormat("yyyy").format(vEcritureComptable.getDate());
        assertEquals("2016", vEcritureYear);
        assertEquals(-5, vEcritureComptable.getId().intValue());

        assertThrows(NotFoundException.class, ()
                -> dao.getEcritureComptableByRef("BQ-2016/11111"));
    }

    @Test
    void loadListLigneEcriture() {
        vEcritureComptable.setId(-4);
        dao.loadListLigneEcriture(vEcritureComptable);
        assertEquals(3, vEcritureComptable.getListLigneEcriture().size());
    }

    @Test
    void getListCompteComptableTest() {
        assertEquals(7, dao.getListCompteComptable().size());
    }

    @Test
    void getListJournalComptableTest() {
        assertEquals(4, dao.getListJournalComptable().size());
    }

    @Test
    @Order(1)
    void insertEcritureComptable() {
        vEcritureComptable.setJournal(new JournalComptable("OD", "Opérations Diverses"));
        vEcritureComptable.setReference("OD-" + vCurrentYear + "/00356");
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Vente épinard");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Station service ALF", new BigDecimal(90),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(2),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Facture épinard M. Popeye FACT456", null,
                new BigDecimal(100)));

        dao.insertEcritureComptable(vEcritureComptable);
    }


    @Test
    @Order(2)
    void updateEcritureComptable() throws NotFoundException {
        vEcritureComptable = dao.getEcritureComptableByRef("OD-" + vCurrentYear + "/00356");

        vEcritureComptable.setJournal(new JournalComptable("OD", "Opérations Diverses"));
        vEcritureComptable.setDate(vCurrentDate);
        vEcritureComptable.setLibelle("Vente épinard update");

        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(401),
                "Station service ALF", new BigDecimal(120),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(4456),
                "TVA 20%", new BigDecimal(3),
                null));
        vEcritureComptable.getListLigneEcriture().add(new LigneEcritureComptable(new CompteComptable(411),
                "Facture épinard M. Popeye FACT456", null,
                new BigDecimal(110)));

        assertNotNull(dao.updateEcritureComptable(vEcritureComptable));
    }


    @Test
    @Order(3)
    void deleteEcritureComptable() throws NotFoundException {
        vEcritureComptable = dao.getEcritureComptableByRef("OD-" + vCurrentYear + "/00356");
        dao.deleteEcritureComptable(vEcritureComptable.getId());
    }


    @Test
    void getSequenceByCodeAndAnneeCourante() throws NotFoundException {
        SequenceEcritureComptable vRechercheSequence = new SequenceEcritureComptable();
        vRechercheSequence.setJournalCode("VE");
        vRechercheSequence.setAnnee(2016);
        SequenceEcritureComptable vExistingSequence = dao.getSequenceByCodeJournalAndByAnneeCourante(vRechercheSequence);

        assertEquals("VE", vExistingSequence.getJournalCode());
        assertEquals(2016, vExistingSequence.getAnnee().intValue());
        assertEquals(41, vExistingSequence.getDerniereValeur().intValue());

        assertThrows(NotFoundException.class, () -> {
            vRechercheSequence.setJournalCode("VE");
            vRechercheSequence.setAnnee(1964);
            dao.getSequenceByCodeJournalAndByAnneeCourante(vRechercheSequence);
        });
    }

    @Test
    void insertOrUpdateSequenceEcritureComptable() throws NotFoundException {

        // **** insert ******
        SequenceEcritureComptable vSequenceEcritureComptable = new SequenceEcritureComptable();
        vSequenceEcritureComptable.setJournalCode("AC");
        vSequenceEcritureComptable.setAnnee(2019);
        vSequenceEcritureComptable.setDerniereValeur(42);

        dao.insertOrUpdateSequenceEcritureComptable(vSequenceEcritureComptable);

        // ctrl insert
        SequenceEcritureComptable vExistingSequence = dao.getSequenceByCodeJournalAndByAnneeCourante(vSequenceEcritureComptable);
        assertEquals("AC", vExistingSequence.getJournalCode());
        assertEquals(2019, vExistingSequence.getAnnee().intValue());
        assertEquals(42, vExistingSequence.getDerniereValeur().intValue());

        // **** update ******
        SequenceEcritureComptable vSequenceEcritureComptableUpdate = new SequenceEcritureComptable();
        vSequenceEcritureComptableUpdate.setJournalCode("AC");
        vSequenceEcritureComptableUpdate.setAnnee(2019);
        vSequenceEcritureComptableUpdate.setDerniereValeur(43);

        dao.insertOrUpdateSequenceEcritureComptable(vSequenceEcritureComptableUpdate);

        // ctrl insert
        vExistingSequence = dao.getSequenceByCodeJournalAndByAnneeCourante(vSequenceEcritureComptableUpdate);
        assertEquals("AC", vExistingSequence.getJournalCode());
        assertEquals(2019, vExistingSequence.getAnnee().intValue());
        assertEquals(43, vExistingSequence.getDerniereValeur().intValue());
    }
}