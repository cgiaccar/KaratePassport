# KaratePassport  
Android project for SIM (2021 second semester course)  
  
Data inizio (primo commit): 14/11/22  
[Coggle organizzativo](https://coggle.it/diagram/Y956keV5GurAx0LT/t/karatepassport)  
  
## Login activity  
campi per numeroTessera e password, pulsante login, pulsante registrati, avviso di errore se l'utente non esiste, la pw è sbagliata o se l'utente esiste già  
## Home activity  
ci arrivi dal login/registrati, titolo "Karate Passport", benvenuto nomeutente? (servirebbe mettere il nome associato al numeroTessera, magari modifica dati personali o solo per futuri sviluppi?), un bel logo o qualcosa del genere, menù a tendina in alto a sinistra  
## Home menu a tendina (fragment?)  
elenco con home, pagina dati? (oppure metto i dati nella home e cicce?), cronologia cinture, rilascia una cintura (solo se utente maestro)  
## (Dati personali activity?)  
titolo (allievo/maestro), nomeutente, grado attuale, direttore tecnico (magari solo lui può darti la nuova cintura? oppure non lo metto proprio?)  
## Cronologia cinture activity  
titolo con numeroTessera/nomeutente?, tabella di kyu e di dan con data di ottenimento, magari mostro la più recente in alto  
## Rilascia una cintura activity  
campo di ricerca inserisci numeroTessera, sotto appare il numero cercato, nome e cognome dell'allievo, cintura attuale, pulsante aumenta grado (o menù a tendina con scelta di quale grado dare?), al click del pulsante il DB viene aggiornato con il nuovo grado e la data attuale, il pulsante viene sostituito da un tick per evitare spam  
  
## DB  
tabella con nome, cognome, pw, numeroTessera (primary key), flag maestro (true/false)  
tabella con numeroTessera e una colonna per grado, con dentro le date di ottenimento  
