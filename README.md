# FrameWork

				Sprint 9 : 19:11 24/09/2024
                   * exposer les actions du controller en REST API
	* creer une nouvelle classe annotation (ex :  Restapi ) 
                   coter framwork : Utilisation méthode  " List Employer " il est annoter pour assosier au  url "?" , nous pouvons ajouter  l'annotation comme Restapi
                     => Dans la partie FrontServlet ,on doit verifier l'existence de cette annotation (Restapi)

                      *Si l'annotation n'existe pas  ==> continuez comme avant ("il n'y a rien à faire,il est necessaire de mettre le Check) 
                        * Si oui   ==>
		           * Récuperer la valeur de retour de la méthode 
			 * Si autre que ModeView , transformer en JSon directement
 (Cela ne devrait pas poser de problème si la valeur de retour n'est pas en ModeView,  si c'est de type 	                                                                                                                                                  Json, car il est possible de simplement déclarer dans le Controller  comme "public List Employer"   (par exemple, "getEmployer"). )
                                                                             
			 * Si  ModeView transformer en Json la valeur de l'attribut " data " 
                De même, pour tester si c'est une instance de ModelView ou non, cela signifie que si la valeur de retour n'est pas                                                                         			              en  ModeView, il est possible de récupérer la valeur, de la transformer en Json en utilisant GSon, etc. Si la valeur   de  retour est de type ModeView, 
                 il suffit de récupérer la valeur de l'attribut "data" et de la transformer en Json.

			* Ne plus utiliser DispatchForward mais utiliser directement getWriter()  et print(json) 

			* Ne pas oublier de changer le response type  =>  text/Json 
				(avant c'est le text html  et on devrait tranformer en Json  pour être vu par les clients )