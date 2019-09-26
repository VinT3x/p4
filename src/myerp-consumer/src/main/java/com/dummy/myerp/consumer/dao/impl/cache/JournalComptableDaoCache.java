package com.dummy.myerp.consumer.dao.impl.cache;

import com.dummy.myerp.consumer.ConsumerHelper;
import com.dummy.myerp.model.bean.comptabilite.JournalComptable;

import java.util.List;


/**
 * Cache DAO de {@link JournalComptable}
 */
public class JournalComptableDaoCache {

    // ==================== Attributs ====================
    /** The List compte comptable. */
    private List<JournalComptable> listJournalComptable;


    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Compte comptable dao cache.
     */
    public JournalComptableDaoCache() {
    }


    // ==================== Méthodes ====================
    /**
     * Gets by code.
     *
     * @param pCode le code du {@link JournalComptable}
     * @return {@link JournalComptable} ou {@code null}
     */
    public JournalComptable getByCode(String pCode) {
        if (listJournalComptable == null) {
            listJournalComptable = ConsumerHelper.getDaoProxy().getComptabiliteDao().getListJournalComptable();
        }

        return JournalComptable.getByCode(listJournalComptable, pCode);
    }
}
