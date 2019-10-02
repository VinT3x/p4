package com.dummy.myerp.testconsumer;


import com.dummy.myerp.consumer.dao.contrat.DaoProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Registre des Beans Spring.
 */
final class SpringRegistry {

    /** Logger Log4j pour la classe */
    private static final Logger LOGGER = LogManager.getLogger(SpringRegistry.class);


    /** Instance unique de la classe (design pattern Singleton) */
    private static final SpringRegistry INSTANCE = new SpringRegistry();


    /** Nom des fichiers de contexte de l'application */
    private static final String CONTEXT_APPLI_LOCATION
            = "classpath*:/com/dummy/myerp/testconsumer/bootstrapContext.xml";

    /** Le context spring de l'application */
    private ApplicationContext contextAppli;


    /**
     * Constructeur.
     */
    private SpringRegistry() {
        super();
        SpringRegistry.LOGGER.debug("[DEBUT] SpringRegistry() - Initialisation du contexte Spring");
        this.contextAppli = new ClassPathXmlApplicationContext(SpringRegistry.CONTEXT_APPLI_LOCATION);
        SpringRegistry.LOGGER.debug("[FIN] SpringRegistry() - Initialisation du contexte Spring");
    }

    /**
     * Renvoie l'instance unique de la classe (design pattern Singleton).
     *
     * @return SpringRegistry
     */
    private static SpringRegistry getInstance() {
        return SpringRegistry.INSTANCE;
    }

    /**
     * Initialise et charge le contexte Spring
     *
     * @return ApplicationContext
     */
    static ApplicationContext init() {
        // le fait d'appeler cette méthode, déclanche l'appel des initialisation static et donc le chargement du context
        return getInstance().contextAppli;
    }

    /**
     * Récupération d'un bean via Spring.
     *
     * @return Object
     */
    private static Object getBeanDaoProxy() {
        String DAOPROXY = "daoProxy";
        SpringRegistry.LOGGER.debug("[DEBUT] SpringRegistry.getBean() - Bean ID : " + DAOPROXY);
        Object vBean = SpringRegistry.getInstance().contextAppli.getBean(DAOPROXY);
        SpringRegistry.LOGGER.debug("[FIN] SpringRegistry.getBean() - Bean ID : " + DAOPROXY);
        return vBean;
    }


    /**
     * Renvoie l'instance de {@link DaoProxy} de l'application
     *
     * @return {@link DaoProxy}
     */
    static DaoProxy getDaoProxy() {
        return (DaoProxy) SpringRegistry.getBeanDaoProxy();
    }
}
