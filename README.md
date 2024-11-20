# FrameWork
Sprint 14 : 

  - Objectif : validation de donnees
  - Etapes : 
     - creation des type de validation au choix -> des annotations   
        - @ReqNotNullired -> le champ ne doit pas etre nul ok 
        - @Mail -> Le champ doit etre un mail ok  
        - @Max  -> la valeur ne doit pas depasser la valeur donnee ok 
        - @Min  ->la valeur ne doit pas etre en dessous de la valeur donnee ok 

     - creer une methode qui fonctionne comme ceci : 
        - elle prend en argument un objet
        - elle recupere l'instance de cet objet
        - elle recupere les attribut de cette classe
        - Pour chaque attribut, on recupere les annotations 
        - si les annotations creer tout a l'heure sont presents sur un fields,
          - recuperer la valeur assigne a cet attribut 
          - voir si la condition est satisfaite sinon on l'ajoute a une liste d'erreur 
        - on retourne la liste des erreurs 
        -Que lorsqu'il y a une erreur, elle est renvoyée à la formule du développeur avec la valeur qu'il a saisie  

