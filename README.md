# Projet SBLS : Spatially Balanced Latin Square

[cite_start]Ce projet porte sur la résolution du problème des Carrés Latins Spatialement Équilibrés (SBLS) en utilisant la programmation par contraintes avec la bibliothèque Choco Solver[cite: 7].

## Description du problème

[cite_start]L'objectif est de tester n types d'engrais sur n x n parcelles de terrain[cite: 1, 2, 5]. [cite_start]Pour éviter les biais de proximité (comme le voisinage fréquent de deux engrais spécifiques), on utilise un SBLS[cite: 5, 6]. [cite_start]Dans ce modèle, la somme des distances entre chaque paire d'engrais doit être identique pour l'ensemble du carré latin[cite: 6].

## Prérequis

* Java SDK (version 11 ou supérieure).
* Le fichier JAR de Choco Solver (ex: `choco-solver.jar`) placé à la racine du projet.

## Structure du projet

[cite_start]Pour respecter le package Java défini dans le code, votre dossier doit être organisé ainsi[cite: 10]:

NOM_PRENOM_SBLS/
├── choco-solver.jar
├── README.md
├── Rapport.pdf
└── Constraint/
    └── ChocoProject/
        └── Chocoproject.java

## Compilation

Ouvrez un terminal dans le dossier racine (NOM_PRENOM_SBLS) et lancez la commande suivante :

Sur Windows :
javac -cp ".;choco-solver.jar" Constraint/ChocoProject/Chocoproject.java

Sur Linux / macOS :
javac -cp ".:choco-solver.jar" Constraint/ChocoProject/Chocoproject.java

## Exécution

Une fois compilé, vous pouvez exécuter le programme en précisant les valeurs de n souhaitées (par exemple n=3 et n=4) :

Sur Windows :
java -cp ".;choco-solver.jar" Constraint.ChocoProject.Chocoproject 3 4

Sur Linux / macOS :
java -cp ".:choco-solver.jar" Constraint.ChocoProject.Chocoproject 3 4

## Contenu du rapport

[cite_start]Le rapport joint (Rapport.pdf) détaille[cite: 10, 11]:
* Les contraintes utilisées (allDifferent, contraintes de distance).
* Les algorithmes de filtrage (AC pour les contraintes allDifferent).
* Les stratégies de recherche (choix des variables et des valeurs).
* Les résultats expérimentaux comparant la méthode simple et la méthode SBLS.

## Contact

Projet réalisé par [VOTRE NOM]
[cite_start]Email de rendu : jcregin@gmail.com (avant le 25 janvier 2026)[cite: 11].
