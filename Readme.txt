Ce que j'ai ajout�:
*Le service PresenceService.java
*Dans ExamenAdapter.java un onlongclickeventlistener
*Ce listener va passer l'id de l'examen selectionn� � activity main (l'id est un attribut de l'activity r�cuper� par une m�thode get,j'ai pas trouv� une autre mani�re pour le passer) et d�clenche le scan
*A la fin du scan , la m�thode onActivityResult de activity main va se lancer et appelle ainsi le service.