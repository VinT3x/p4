package com.dummy.myerp.consumer.dao.impl.db.rowmapper.comptabilite;

import com.dummy.myerp.model.bean.comptabilite.CompteComptable;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * {@link RowMapper} de {@link CompteComptable}
 */
public class CompteComptableRM implements RowMapper<CompteComptable> {

    @Override
    public CompteComptable mapRow(ResultSet pRS, int pRowNum) throws SQLException {
        CompteComptable vBean = new CompteComptable();
        vBean.setNumero(pRS.getInt("numero"));
        vBean.setLibelle(pRS.getString("libelle"));

        return vBean;
    }
}
