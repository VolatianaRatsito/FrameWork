# FrameWork
Sprint 10 : 

ny coté dev aloha zany mila afaka mametraka annotation eo @methode : @GET sy @POST
raha tsy misy ny 1 @ireo dia GET par défaut (ohatrany ataotsika zao)

Pour le Coté framework : 

mila fantatra zany hoe. GET sa POST ilay methode dia miankina @iny ko le Url
teo aloha izany dia ny url dia associé @Classe + action (methode any @controlleur) 
fa @zao mila ampiana attirbut hafa (VERB) dia io zany @zao soit GET soit POST
raha misy cas oahtr zao zany dia tokony mi lever exception : methode na controlleur :
   @GET @url("/getemp") getEmp .... d nefa ilay ilay url "getemp" io antsoina @verb (method) POST !!


Pour le sprint, l'objectif est que, du côté développement, nous puissions ajouter des annotations
 sur les méthodes : @GET et @POST. Si aucune de ces annotations n'est présente, nous considérerons 
 par défaut que c'est GET (c'est ce que nous faisons actuellement).

Du côté du framework creer une classe FrontServlet ,il est nécessaire de déterminer si la méthode est GET ou POST, et cela dépendra
de l'URL.
Auparavant, l'URL était associée à la classe et à l'action (méthode dans le contrôleur).
Cependant, maintenant, il faut ajouter un autre attribut (VERB) qui sera soit GET, soit POST.

Dans le cas où, par exemple, nous avons une méthode ou un contrôleur annoté @GET avec l'URL
 @url("/getemp") et que cette méthode getEmp est appelée avec le verbe (méthode) POST, 
 cela devrait lever une exception.


- Creer une fonction equals dans util pour verifier si parmi les methodes d'une classe il y a: 
Return False (valide): - meme url -> different verb
Return True (non valide): - meme url -> meme verb   : ok

Modification:
- Classe VerbMethode: String action, String verb ok
  ex: (method1, GET)
- Mapping (update): String classe, List<VerbMethode> ok

SPRINT 11:
- Au lieu d'exception: creer une reponse http et remplacer le status code en 404
s'il y a deux verb  egaux @GET @GET  il affichais une erreur de meme pour les methodes




