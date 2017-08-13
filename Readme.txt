Ce que j'ai ajouté:
*Le service PresenceService.java
*Dans ExamenAdapter.java un onlongclickeventlistener
*Ce listener va passer l'id de l'examen selectionné à activity main (l'id est un attribut de l'activity récuperé par une méthode get,j'ai pas trouvé une autre manière pour le passer) et déclenche le scan
*A la fin du scan , la méthode onActivityResult de activity main va se lancer et appelle ainsi le service.