Creer une nouvelle branche "sprint1-2722"
Et envoyer le framework sur git.
Modif dans mon framework :
1-Creer AnnotationController
2- Annoter mes controleurs avec AnnotationController
2-creer une classe FOnction : 
 *Scanner (CurrentThread)
 *getAnnotationClass
 *pour connaitre s'il est controller (isController)
 *findClasses
3- Mettre mes controleurs dans le meme package
Modif dans FrontController :
(Prendre le nom du package où se trouvent mes controleurs,lister tous les controllers excister )
1- Tester si j'ai déjà scanner mes controleurs
+ Si oui, afficher la liste des noms de mes controleurs 
+Sinon scanner, puis afficher la liste des noms de mes controleurs 
Modif dans le projet de test:
Web.xml
     + declarer  le nom du package (misy ny controller rehetra) (using init-param)
     + declarer mon frontServlet