package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import com.dummy.myerp.consumer.dao.impl.cache.CompteComptableDaoCache;
import com.dummy.myerp.model.bean.comptabilite.LigneEcritureComptable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * {@link RowMapper} de {@link LigneEcritureComptable}
 */
public class LigneEcritureComptableRM implements RowMapper<LigneEcritureComptable> {

    /** CompteComptableDaoCache */
    private final CompteComptableDaoCache compteComptableDaoCache = new CompteComptableDaoCache();


    @Override
    public LigneEcritureComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException {
        LigneEcritureComptable vBean = new LigneEcritureComptable();
        vBean.setCompteComptable(compteComptableDaoCache.getByNumero(pRS.getObject("compte_comptable_numero",
                                                                                   Integer.class)));
        vBean.setCredit(pRS.getBigDecimal("credit"));
        vBean.setDebit(pRS.getBigDecimal("debit"));
        vBean.setLibelle(pRS.getString("libelle"));

        return vBean;
    }
}
