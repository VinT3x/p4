package com.dummy.myerp.business.impl.manager;

import com.dummy.myerp.business.contrat.manager.ComptabiliteManager;
import com.dummy.myerp.business.impl.AbstractBusinessManager;
import com.dummy.myerp.consumer.dao.contrat.ComptabiliteDao;
import com.dummy.myerp.model.bean.comptabilite.*;
import com.dummy.myerp.technical.exception.FunctionalException;
import com.dummy.myerp.technical.exception.NotFoundException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.TransactionStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;


/**
 * Comptabilite manager implementation.
 */
public class ComptabiliteManagerImpl extends AbstractBusinessManager implements ComptabiliteManager {

    // ==================== Attributs ====================
    private ComptabiliteDao comptabiliteDao;

    // ==================== Constructeurs ====================
    /**
     * Instantiates a new Comptabilite manager.
     */
    public ComptabiliteManagerImpl(ComptabiliteDao pComptabiliteDao) {
        this.comptabiliteDao = pComptabiliteDao;
    }


    // ==================== Getters/Setters ====================
     @Override
    public List<CompteComptable> getListCompteComptable() {
        return comptabiliteDao.getListCompteComptable();
    }


    @Override
    public List<JournalComptable> getListJournalComptable() {
        return comptabiliteDao.getListJournalComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EcritureComptable> getListEcritureComptable() {
        return comptabiliteDao.getListEcritureComptable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EcritureComptable getEcritureComptableByRef(String pRef) throws NotFoundException {
        return comptabiliteDao.getEcritureComptableByRef(pRef);
    }

    @Override
    public SequenceEcritureComptable getSequenceByCodeJournalAndByAnneeCourante(SequenceEcritureComptable pSeqEcritureComptable) throws NotFoundException {
        SequenceEcritureComptable sequenceEcritureComptable;
        try{
            sequenceEcritureComptable = comptabiliteDao.getSequenceByCodeJournalAndByAnneeCourante(pSeqEcritureComptable);
        }catch (NotFoundException nfe ){
            sequenceEcritureComptable = null;
        }
        return sequenceEcritureComptable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addReference(EcritureComptable pEcritureComptable) throws FunctionalException, NotFoundException {
        /* Le principe :
                1.  Remonter depuis la persitance la dernière valeur de la séquence du journal pour l'année de l'écriture
                    (table sequence_ecriture_comptable)
        */

        // annee ecriture comptable
        int vEcAnnee = Integer.parseInt(new SimpleDateFormat("yyyy").format(pEcritureComptable.getDate()));

        // recherche de la séquence de l'écriture comptable
        SequenceEcritureComptable vRechercheSequenceEC = new SequenceEcritureComptable();
        vRechercheSequenceEC.setJournalCode(pEcritureComptable.getJournal().getCode());
        vRechercheSequenceEC.setAnnee(vEcAnnee);

        SequenceEcritureComptable vExistingSequence = getSequenceByCodeJournalAndByAnneeCourante(vRechercheSequenceEC);

        /*        2.  * S'il n'y a aucun enregistrement pour le journal pour l'année concernée :
                        1. Utiliser le numéro 1.
                    * Sinon :
                        1. Utiliser la dernière valeur + 1
        */
        int vNumSeqEC;
        if (vExistingSequence == null){
            vNumSeqEC = 1;
        }else{
            vNumSeqEC = vExistingSequence.getDerniereValeur() + 1;
        }

        /*
                3.  Mettre à jour la référence de l'écriture avec la référence calculée (RG_Compta_5)
        */
        String vReference = pEcritureComptable.getJournal().getCode() +
                "-" + vEcAnnee +
                "/" + String.format("%05d", vNumSeqEC); // format sur 5 digit

        pEcritureComptable.setReference(vReference);
        pEcritureComptable = this.updateEcritureComptable(pEcritureComptable);

        /*
                4.  Enregistrer (insert/update) la valeur de la séquence en persistance
                    (table sequence_ecriture_comptable)
        */
        SequenceEcritureComptable vNewSequence = new SequenceEcritureComptable();
        vNewSequence.setJournalCode(pEcritureComptable.getJournal().getCode());
        vNewSequence.setAnnee(vEcAnnee);
        vNewSequence.setDerniereValeur(vNumSeqEC);
        this.insertOrUpdateSequenceEcritureComptable(vNewSequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertOrUpdateSequenceEcritureComptable(SequenceEcritureComptable pSequence) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            comptabiliteDao.insertOrUpdateSequenceEcritureComptable(pSequence);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void checkEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException, NotFoundException {
        this.checkEcritureComptableUnit(pEcritureComptable);
        this.checkEcritureComptableContext(pEcritureComptable);
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion unitaires,
     * c'est à dire indépendemment du contexte (unicité de la référence, exercie comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableUnit(EcritureComptable pEcritureComptable) throws FunctionalException, NotFoundException {
        // ===== Vérification des contraintes unitaires sur les attributs de l'écriture
        Set<ConstraintViolation<EcritureComptable>> vViolations = getConstraintValidator().validate(pEcritureComptable);
        if (!vViolations.isEmpty()) {
            throw new ConstraintViolationException(
                    "L'écriture comptable ne respecte pas les contraintes de validation",
                    vViolations);
        }

        // ===== RG_Compta_2 : Pour qu'une écriture comptable soit valide, elle doit être équilibrée
        if (!pEcritureComptable.isEquilibree()) {
            throw new FunctionalException("L'écriture comptable n'est pas équilibrée.");
        }

        // ===== RG_Compta_3 : une écriture comptable doit avoir au moins 2 lignes d'écriture (1 au débit, 1 au crédit)
        int vNbrCredit = 0;
        int vNbrDebit = 0;
        for (LigneEcritureComptable vLigneEcritureComptable : pEcritureComptable.getListLigneEcriture()) {
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getCredit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrCredit++;
            }
            if (BigDecimal.ZERO.compareTo(ObjectUtils.defaultIfNull(vLigneEcritureComptable.getDebit(),
                                                                    BigDecimal.ZERO)) != 0) {
                vNbrDebit++;
            }
        }
        // On test le nombre de lignes car si l'écriture à une seule ligne
        //      avec un montant au débit et un montant au crédit ce n'est pas valable
        if (vNbrCredit < 1 || vNbrDebit < 1) {
            throw new FunctionalException(
                "L'écriture comptable doit avoir au moins deux lignes : une ligne au débit et une ligne au crédit.");
        }

        // ===== RG_Compta_5 : Format et contenu de la référence
        // vérifier que l'année dans la référence correspond bien à la date de l'écriture, idem pour le code journal...
        String vDate = new SimpleDateFormat("yyyy").format(pEcritureComptable.getDate());
        if (!pEcritureComptable.getReference().substring(3, 7).equals(vDate))
            throw new FunctionalException(
                    "L'année dans la référence doit correspondre à la date de l'écriture comptable.");

        if (!pEcritureComptable.getReference().substring(0, 2).equals(pEcritureComptable.getJournal().getCode()))
            throw new FunctionalException(
                    "Le code journal dans la référence doit correspondre au code du journal en question.");
    }


    /**
     * Vérifie que l'Ecriture comptable respecte les règles de gestion liées au contexte
     * (unicité de la référence, année comptable non cloturé...)
     *
     * @param pEcritureComptable -
     * @throws FunctionalException Si l'Ecriture comptable ne respecte pas les règles de gestion
     */
    protected void checkEcritureComptableContext(EcritureComptable pEcritureComptable) throws FunctionalException {
        // ===== RG_Compta_6 : La référence d'une écriture comptable doit être unique
        if (StringUtils.isNoneEmpty(pEcritureComptable.getReference())) {
            try {
                // Recherche d'une écriture ayant la même référence
                EcritureComptable vECRef = getEcritureComptableByRef(pEcritureComptable.getReference());

                // Si l'écriture à vérifier est une nouvelle écriture (id == null),
                // ou si elle ne correspond pas à l'écriture trouvée (id != idECRef),
                // c'est qu'il y a déjà une autre écriture avec la même référence
                if (pEcritureComptable.getId() == null
                    || !pEcritureComptable.getId().equals(vECRef.getId())) {
                    throw new FunctionalException("Une autre écriture comptable existe déjà avec la même référence.");
                }
            } catch (NotFoundException vEx) {
                // Dans ce cas, c'est bon, ça veut dire qu'on n'a aucune autre écriture avec la même référence.
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException, NotFoundException {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            comptabiliteDao.insertEcritureComptable(pEcritureComptable);
            addReference(pEcritureComptable);
            checkEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }

    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    public EcritureComptable updateEcritureComptable(EcritureComptable pEcritureComptable) throws FunctionalException, NotFoundException {
        this.checkEcritureComptable(pEcritureComptable);

        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            comptabiliteDao.updateEcritureComptable(pEcritureComptable);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
        return pEcritureComptable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteEcritureComptable(Integer pId) {
        TransactionStatus vTS = getTransactionManager().beginTransactionMyERP();
        try {
            comptabiliteDao.deleteEcritureComptable(pId);
            getTransactionManager().commitMyERP(vTS);
            vTS = null;
        } finally {
            getTransactionManager().rollbackMyERP(vTS);
        }
    }
}
