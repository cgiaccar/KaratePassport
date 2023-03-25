# KaratePassport  
Android project for SIM (2021 second semester course)  

Starting date (first commit): 14/11/22  
[Coggle for organization](https://coggle.it/diagram/Y956keV5GurAx0LT/t/karatepassport)  
  
## Login activity  
Email and password; checks for empty fields and short password; password recovery; link to Register activity.
## Register activity  
Username, passport number, email, password; checks for empty fields, short password and passport uniqueness; link to Login activity; registers in the DB the new passport number and the user (with their info and the white belt).  
## Main activity  
Serves as host for the Navigation menu, the app bar and the fragments; customizes the user experience after checking if they are a master ("Logged in as userName", eventual "Grant a new Belt!", eventual "Master userName").  


## Home Fragment  
User is directed here after login/registration or after clicking on the menu; it displays a welcome message and the user informations; logout button.  
## Belt log fragment  
Reachable from its label in the navigation menu; displays two tables with all the belt ranks achieved by the logged user and relative dates.  
## Grant a Belt fragment  
Reachable from its label in the navigation menu; displays a spinner for passportNumber selection, a confirmation button, a spinner for belt selection, another confirmation button; the new belt is saved on the DB at confirmation; a short toast is displayed after a successful granting operation.  


## Navigation menu  
Displays a header; list of links to all the fragments and logout link; app bar at the top with the name of the current fragment and a little icon to open and close the menu.  

  
## DB ([Firebase Authentication](https://console.firebase.google.com/u/0/project/karatepassport-bac6b/authentication/users) and [Firebase Firestore](https://console.firebase.google.com/u/0/project/karatepassport-bac6b/firestore/data/~2Fusers~2FE6fi8tpLvTcy9M3V4NxipB4FcE32))  
Email and password authentication;  

"passports" collection with a document for each passport; documentID is the passportNumber; document has "owner" field with value userID;  

"users" collection with a document for each user; documentID automatically generated at registration; each user has their info as fields and a "belts" collection; each belt in belts has the rank name as the documentID and a field "timestamp" with the obtaining date.  
