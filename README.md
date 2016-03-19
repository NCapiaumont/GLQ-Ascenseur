# TP Génie logiciel et qualité - Ascenseur

Les exercices du TP s'insèrent dans le cadre plus général du développement d'un logiciel de simulation d'un système automatisé d'ascenseur.

## Ce système répond à la description suivante :
Contexte – Simulation d'un système automatisé d'ascenseur

### Les quatre commandes du moteur de la cabine
C'est la partie commande du système qui commande le moteur de traction de la cabine d'ascenseur. Ce moteur peut être commandé par quatre commandes exactement, qui peuvent être désignées informellement par : 
```
"monter"
"descendre"
"arrêter au prochain étage"
"arrêter d'urgence"
```

* Les commandes de déplacement, monter et descendre, sont élémentaires : elles ne provoquent que le déplacement de la cabine dans un sens, sans indication de destination. Ainsi, la commande monter provoque le déplacement de la cabine vers le haut. Inversement, la commande descendre provoque le déplacement de la cabine vers le bas. Dès que la cabine est arrêtée, il faut envoyer une commande de déplacement pour la faire repartir.
* La commande "arrêter au prochain étage" entraîne l'arrêt de la cabine au prochain palier. La cabine gère elle-même son positionnement devant le palier. 
* La commande "arrêter d'urgence" entraîne l'arrêt immédiat de la cabine quelle que soit sa position (y compris entre deux paliers).

### Les capteurs des paliers
Des capteurs placés sur chaque palier envoient des informations utilisables par la partie commande du système. Chaque fois que la cabine atteint un des paliers, un capteur émet un signal pour la partie commande.

### Les demandes de déplacement réalisées par les utilisateurs
Panneau de boutons Panneau externe sur chaque palier interne à une cabine d'ascenseur (générateur de demandes externes) (générateur de demandes internes) 

La partie commande du système reçoit des requêtes de la part des utilisateurs de l'ascenseur. Ces requêtes peuvent être de deux types :
* Les demandes internes à la cabine. Une telle demande est générée par un utilisateur qui se trouve à l'intérieur de la cabine. C'est une demande de déplacement pour un étage (caractérisée par le numéro de l'étage auquel l'utilisateur désire se rendre, par exemple :
** une demande interne de déplacement pour le deuxième étage),
** une demande d'arrêt d'urgence ou une demande d'annulation d'arrêt d'urgence.
Il existe N+1 demandes de déplacement pour un étage possibles si N est le nombre d'étages : 
** la demande de déplacement pour le rez-de-chaussée,
** celle pour le premier étage,
** jusqu'à celle pour le dernier étage (N).
Il ne peut y avoir qu'une demande interne de déplacement pour un même étage. Toutes les nouvelles demandes internes de déplacement pour ce même étage sont ignorées, tant que la demande existante de déplacement à cet étage n'a pas été satisfaite. La demande d'arrêt d'urgence est générée par un utilisateur qui souhaite arrêter la cabine immédiatement pour une raison urgente. L'état d'urgence commence au moment de la demande et finit quand l'utilisateur annule la demande par une demande contraire, la demande d'annulation d'arrêt d'urgence. Cette annulation ne peut intervenir qu'une fois que l'ascenseur s'est effectivement arrêté.
* Les demandes de déplacement externes à la cabine. Une telle demande est générée par un utilisateur qui se situe sur un palier à l'extérieur de la cabine, et qui souhaite utiliser l'ascenseur. On distingue deux types de demandes possibles à chaque palier : 
** la demande pour monter 
** et la demande pour descendre,
par exemple la demande externe de déplacement réalisée au 3ème palier pour monter et celle réalisée au 3ème palier également, pour descendre.
Une demande de déplacement externe est ainsi caractérisée par un numéro d'étage/palier, celui où se trouve l'utilisateur, et le sens dans lequel il veut se déplacer. Il ne peut y avoir qu'une demande pour monter par palier et qu'une demande pour descendre par palier. Toutes les nouvelles demandes à partir d'un palier sont ignorées, tant qu'il existe une demande de même objectif (monter ou descendre) réalisée à partir de ce palier qui n'a pas été satisfaite.
Les demandes sont générées par l'utilisateur via une IHM représentant les boutons à l'intérieur de la cabine (les boutons de demande de déplacement et le bouton d'arrêt d'urgence/annulation d'arrêt d'urgence), et les boutons montée et descente sur chaque palier (à l'exception des paliers extrêmes qui n'ont qu'un bouton : montée pour le rez-de-chaussée et descente pour le dernier palier).
Lorsque l'utilisateur appuie sur un bouton, la demande correspondante est générée, le bouton s'allume et reste allumé tant que la demande
n'est pas satisfaite. 

### La stratégie de déplacement de l'ascenseur
Les demandes internes (pour un étage) et externes (depuis un étage, avec un sens déterminé) sont traitées de façon identique, autrement dit, les unes ne sont pas plus prioritaires que les autres. Chacune est caractérisée par deux informations : 
** un étage (destination pour une demande interne, origine pour une demande externe) 
** et un sens (indéfini pour une demande interne, montée ou descente pour une demande externe). 
Dans les deux cas, pour que la demande soit satisfaite par le système, la cabine doit être déplacée en direction de l'étage demandé et arrêtée à cet étage. Considérons un utilisateur qui se trouve au premier étage et souhaite se rendre au 6ème étage. Celui-ci va générer une première demande externe du premier étage dans le sens de la montée, et plus tard, une deuxième demande quand il sera à l'intérieur de la cabine pour se rendre au 6ème. 
Il aura provoqué deux arrêts de la cabine : 
** un arrêt au premier 
** et un arrêt au 6ème. 
La stratégie de satisfaction des demandes mise en place par le système consiste à accepter toutes les demandes, à tout instant, sans distinguer les demandes provenant des paliers de celles provenant de la cabine, puis de les satisfaire de façon optimale en favorisant le sens de déplacement courant de la cabine pour minimiser le nombre de ses changements de sens.
Lorsque la cabine s'arrête à un palier, elle y reste immobile au moins un certain temps fixé (temporisation T).
Lorsqu'une seule demande de déplacement est émise, la cabine se met en mouvement en direction de l'étage demandé, s'arrête à l'étage demandé et y reste au moins la durée T, jusqu'à ce qu'une nouvelle demande soit émise.
A un moment donné, lorsque plusieurs demandes ont été réalisées, ces demandes ne vont pas être satisfaites dans l'ordre où elles ont été émises, mais dans un ordre tel que les déplacements de la cabine sont minimisés tout en satisfaisant équitablement les demandes des utilisateurs. Parmi ces demandes (auxquelles s'ajoutent d'autres demandes éventuelles générées au fur et à mesure du déplacement de la
cabine), la cabine satisfait en priorité celles qui correspondent à son sens de sa progression (montée ou descente) dans l'ordre de sa progression, les étages inférieurs avant les étages supérieurs quand elle monte, le contraire quand elle descend. 
Par exemple : supposons que des demandes de déplacement aient été générées selon la séquence suivante :
```
(10-indéfini, 4-montée, 3-descente, 3-indéfini, 2-descente)
```
lorsque la cabine est en train de monter sans avoir encore atteint le second étage, et si aucune autre demande n'est générée, la cabine satisfera les demandes dans l'ordre : 
```
3-indéfini, 4-monter, 10-indéfini, 3-descendre, 2- descendre.
```
Par conséquent, la cabine ne peut changer de sens de déplacement que dans l'un des trois cas suivants : 
** après avoir atteint le dernier palier, 
** ou après avoir atteint le palier 0, 
** ou à un palier intermédiaire, s'il n'y a pas d'autre demande à satisfaire dans le sens de sa progression courante. 
D'autre part, il apparaît que le traitement d'une demande interne se ramène à celui d'une demande externe en affectant le sens de la
demande interne au sens de déplacement courant de la cabine. 
Enfin, quand une demande d'arrêt d'urgence intervient, elle est prise immédiatement en compte et toutes les demandes de déplacement précédemment enregistrées et pas encore satisfaites, sont détruites. La cabine ne pourra repartir qu'après annulation de l'arrêt d'urgence qui permettra à la partie commande du système d'accepter de nouvelles demandes.
