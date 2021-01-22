# EpiSim

Simulateur de dynamique épidémique permettant de concevoir, paramétrer et appliquer des modèles compartimentaux avec différentes politiques publiques.

| Menu principal | Simulation |
|:-------------------------:|:-------------------------:|
|![Menu principal](docs/images/home.png)|![Simulation](docs/images/simulation.png)|

## Exécuter à partir des sources
```
git clone <this repo>
cd episim
./gradlew run
```

## Générer l'exécutable
```
git clone <this repo>
cd episim
./gradlew packageApp
```
Génère les archives: \
`app/build/app-{version}-{platform}.zip` \
`app/build/app-{version}-{platform}.tar.gz`

## Références

https://images.math.cnrs.fr/Modelisation-d-une-epidemie-partie-1.html \
https://prajwalsouza.github.io/Experiments/Epidemic-Simulation.html
