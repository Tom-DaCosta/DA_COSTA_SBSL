# Projet SBLS : Spatially Balanced Latin Square

Ce projet porte sur la résolution du problème des Carrés Latins Spatialement Équilibrés (SBLS) en utilisant la programmation par contraintes avec la bibliothèque Choco Solver.

## Description du problème

L'objectif est de tester n types d'engrais sur n x n parcelles de terrain. Pour éviter les biais de proximité (comme le voisinage fréquent de deux engrais spécifiques), on utilise un SBLS. Dans ce modèle, la somme des distances entre chaque paire d'engrais doit être identique pour l'ensemble du carré latin.

## Prérequis

* Java SDK (version 11 ou supérieure).
* Le fichier JAR de Choco Solver (ex: `choco-solver-5.0.0-beta.1-light.jar`) placé à la racine du projet.

## Structure du projet

```
DA_COSTA_SBLS/
├── choco-solver-5.0.0-beta.1-light.jar
├── README.md
├── Rapport_SBLS.pdf
└── Chocoproject.java
```
## Compilation

Ouvrez un terminal dans le dossier racine (DA_COSTA_SBLS) et lancez la commande suivante :

Sur Windows :
```
javac -cp ".;choco-solver-5.0.0-beta.1-light.jar" ./Chocoproject.java
```

Sur Linux / macOS :
```
javac -cp ".:choco-solver-5.0.0-beta.1-light.jar" ./Chocoproject.java
```

## Exécution

Une fois compilé, vous pouvez exécuter le programme en précisant les valeurs de n souhaitées (par exemple n=3 et n=4) :

Sur Windows :
```
java -cp ".;choco-solver-5.0.0-beta.1-light.jar" Chocoproject 3 4
```

Sur Linux / macOS :
```
java -cp ".:choco-solver-5.0.0-beta.1-light.jar" Chocoproject 3 4
```
## Contenu du rapport

Le rapport joint (Rapport.pdf) détaille:
* Les contraintes utilisées (allDifferent, contraintes de distance).
* Les algorithmes de filtrage (AC pour les contraintes allDifferent).
* Les stratégies de recherche (choix des variables et des valeurs).
* Les résultats expérimentaux comparant la méthode simple et la méthode SBLS.

## Contact

Projet réalisé par DA COSTA Tom
