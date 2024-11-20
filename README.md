# FrameWork
Sprint 13 :
  - Objectif : validation de donnees
  - Etapes : 
     - creation des type de validation au choix -> des annotations   
        - @ReqNotNullired -> le champ ne doit pas etre nul ok 
        - @Mail -> Le champ doit etre un mail ok  
        - @Max  -> la valeur ne doit pas depasser la valeur donnee ok 
        - @Min  ->la valeur ne doit pas etre en dessous de la valeur donnee ok 

Donc ,j'ai creer une classe validation :

     - creer une methode qui fonctionne comme ceci : 
        - elle prend en argument un objet
        - elle recupere l'instance de cet objet
        - elle recupere les attribut de cette classe
        - Pour chaque attribut, on recupere les annotations 
        - si les annotations creer tout a l'heure sont presents sur un fields,
          - recuperer la valeur assigne a cet attribut 
          - voir si la condition est satisfaite sinon on l'ajoute a une liste d'erreur 
        - on retourne la liste des erreurs    

La classe ValidationException améliore la gestion des erreurs dans une application 
en permettant de capturer des informations spécifiques sur les validations échouées, 
rendant le processus plus robuste et maintenable.






