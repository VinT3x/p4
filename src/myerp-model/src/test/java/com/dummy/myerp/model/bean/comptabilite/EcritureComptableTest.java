package com.dummy.myerp.model.bean.comptabilite;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;


class EcritureComptableTest {


    private LigneEcritureComptable createLigne(Integer pCompteComptableNumero, String pDebit, String pCredit) {
        BigDecimal vDebit = pDebit == null ? null : new BigDecimal(pDebit);
        BigDecimal vCredit = pCredit == null ? null : new BigDecimal(pCredit);
        String vLibelle = ObjectUtils.defaultIfNull(vDebit, BigDecimal.ZERO)
                .subtract(ObjectUtils.defaultIfNull(vCredit, BigDecimal.ZERO)).toPlainString();
        return new LigneEcritureComptable(new CompteComptable(pCompteComptableNumero),
                vLibelle,
                vDebit, vCredit);
    }

    @Test
    @DisplayName("isEquilibree() test")
    void isEquilibree() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("Equilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "100.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "301"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "40", "7"));
        assertTrue(vEcriture.isEquilibree(), vEcriture.toString());

        vEcriture.getListLigneEcriture().clear();
        vEcriture.setLibelle("Non équilibrée");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "10", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "20", "1"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, null, "30"));
        vEcriture.getListLigneEcriture().add(this.createLigne(2, "1", "2"));
        assertFalse(vEcriture.isEquilibree(), vEcriture.toString());
    }

    @Test
    @DisplayName("getTotalDebit() test")
    void getTotalDebit() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("getTotalDebit");
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "101.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "12", "33"));

        BigDecimal bd = new BigDecimal (314);
        assertEquals(vEcriture.getTotalDebit().compareTo(bd), 0);

    }

    @Test
    @DisplayName("getTotalCredit() test")
    void getTotalCredit() {
        EcritureComptable vEcriture;
        vEcriture = new EcritureComptable();

        vEcriture.setLibelle("getTotalCredit");
        vEcriture.setReference("AA-1234/12345");
        vEcriture.setJournal(new JournalComptable());
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "200.50", null));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "101.50", "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, null, "33"));
        vEcriture.getListLigneEcriture().add(this.createLigne(1, "12", "33"));

        BigDecimal bd = new BigDecimal (99);
        assertEquals(vEcriture.getTotalCredit().compareTo(bd), 0);

    }

}
