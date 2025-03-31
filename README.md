# FrameWork
Sprint 14 : 

1. **Ajout de `sprint14.jar` :** Placez le fichier dans le dossier `lib` de votre projet.
2. **Configuration de `web.xml` :** Définissez l'URL par défaut et configurez le message de bienvenue ("Welcome Here").
3. **Déclaration d'un contrôleur :** Importez le package `mg.annotation.*` et ajoutez l'annotation `@Controller` à la classe.
4. **Détection du package des contrôleurs :** Déclarez le nom du package des contrôleurs dans `<init-param>` du fichier `web.xml`.
5. **Association URL et méthode :** Utilisez l'annotation `@Get(/url)` pour lier une méthode à une URL spécifique.
6. **Exécution des méthodes :** Testez les méthodes en accédant à l'URL correspondante dans le navigateur.
7. **Exceptions à gérer :** Vérifiez les erreurs comme un package vide, des URL dupliquées ou un mauvais type de retour.
8. **Envoi de données du formulaire au contrôleur :** Utilisez `@RequestParam` pour les types primitifs et `@ModelAttribute` pour les types complexes.
9. **Utilisation des sessions :** Créez une session avec `MySession`, ajoutez, récupérez ou supprimez des valeurs via les méthodes appropriées.
10. **API REST :** Utilisez `@RestApi` pour exposer des méthodes en tant qu'API.
11. **Gestion des fichiers :** Utilisez la classe `MultiPart` pour gérer les fichiers téléchargés avec des méthodes comme `getFileName()`, `getFileSize()`, et `getFileContent()`.
12. **Validation des données :** Employez des annotations comme `@NotNull`, `@Min`, `@Max`, et `@Mail` pour contrôler les valeurs des champs.

Les objectifs principaux de la classe Fonction sont les suivants :

1. **Invocation dynamique de méthodes** : Appeler des méthodes sur des objets en fonction des paramètres d'une requête HTTP.
2. **Gestion des API REST** : Vérifier si une méthode est annotée comme API REST et traiter la réponse en JSON.
3. **Gestion des sessions** : Gérer et maintenir la session utilisateur dans l'application.
4. **Récupération et conversion des paramètres** : Extraire et convertir les paramètres de la requête HTTP en objets appropriés, selon des annotations comme `@RequestParameter` ou `@ModelAttribute`.
5. **Validation des données** : Valider les objets extraits des formulaires et gérer les erreurs.
6. **Conversion des types de données** : Convertir les valeurs de la requête HTTP en types de données adaptés (ex. chaîne en entier, en double).
7. **Suppression des attributs de session** : Supprimer tous les attributs stockés dans la session si nécessaire.
8. **Gestion des fichiers multipart** : Gérer l'upload de fichiers multipart et extraire des informations sur ces fichiers.

L'objectif général est de rendre l'application plus flexible, modulaire et efficace dans la gestion des requêtes HTTP complexes, des sessions et des validations.
