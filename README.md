# Projet OC : testez vos développements Java

## Les tests
Les tests unitaires et d'intégration ont permis de déceler des erreurs, voici les corections apportées :

### Couche MODEL :
Dans l'entité EcritureComptable :
*   le pattern de la propriété reference n'était pas correct, il faut en début de chaînes 2 lettres majuscules et non pas de 1 à 5 lettres.
*   la méthode getTotalCredit() utilisait la méthode getDebit() au lieu de getCredit(),
*   la méthode isEquilibree() comparait le résultat d'une égalité de BigDecimal à l'aide de equals() au lieu de compareTo()


### Couche BUSINESS :
Dans la classe ComptabiliteManagerImpl:
*   suppression de l'héritage de la classe AbstractBusinessManager, pour "mocké" plus facilement dans les tests unitaires.
*   la méthode updateEcritureComptable(), ne contrôlait pas l'écriture comptable passée en paramètre. 
Ajout du contrôle en appelant la méthode checkEcritureComptable(pEcritureComptable).
		
### Couche CONSUMER :
Dans le fichier sqlContext.xml :
*   sur la requête Insert de la propriété SQLinsertListLigneEcritureComptable, 
	il manquait une virgule entre les colonnes debit et credit.


## Docker
### Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs _docker_ utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application


### Environnement de développement

Les composants nécessaires lors du développement sont disponibles via des conteneurs _docker_.
L'environnement de développement est assemblé grâce à _docker-compose_
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données _PostgreSQL_ contenant un jeu de données de démo (`postgresql://127.0.0.1:9032/db_myerp`)



#### Lancement

    cd docker/dev
    docker-compose up


#### Arrêt

    cd docker/dev
    docker-compose stop


#### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up
