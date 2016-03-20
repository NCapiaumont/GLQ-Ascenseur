package tp4;

import java.util.ArrayList;

import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;
import outils.Sens;
import test.IController;

public class Controller implements IController {
	private ListeTrieeCirculaireDeDemandes liste;
	private ICabine cabine;
	private IIUG iug;
	private int position;
	private Sens sens;
	private EtatController etatController;
	ArrayList<Demande> indefini = new ArrayList<Demande>();
	
	public ArrayList<Demande> getIndefini() {
		return indefini;
	}

	/**
	 * getEtatController() 
	 * 
	 * retourn l'etat du controller
	 * @return EtatController
	 */
	public EtatController getEtatController() {
		return etatController;
	}

	/**
	 * enum pour representer l'etat du controller
	 * @author CHOUKRI
	 *
	 */
	public enum EtatController {
		// ATTENTE = INDEFINI
		DESCENTE, MONTEE, ARRET_ETAGE, ARRET_IMMINENT, ARRET_IMMEDIAT, ATTENTE;

		public boolean isEnMouvement() {
			return this == EtatController.DESCENTE || this == EtatController.MONTEE
					|| this == EtatController.ARRET_IMMINENT;
		}
	}

	/**
	 * Controller(ListeTrieeCirculaireDeDemandes)
	 * constructeur de la classe contoller
	 * @param liste
	 */
	public Controller(ListeTrieeCirculaireDeDemandes liste) {
		this.liste = liste;
		this.position = 0;
		etatController = EtatController.ATTENTE;
	}

	public ListeTrieeCirculaireDeDemandes getListe() {
		return liste;
	}

	public void setListe(ListeTrieeCirculaireDeDemandes liste) {
		this.liste = liste;
	}

	/**
	 * setIUG(IIUG)
	 * 
	 * Permet de donner un IUG au Controlleur
	 */
	public void setIUG(IIUG iug) {
		this.iug = iug;
	}

	/**
	 * setCabine(ICabine)
	 * 
	 * Permet de donner une Cabine au Controller
	 */
	public void setCabine(ICabine cabine) {
		this.cabine = cabine;
	}

	private Demande demandeInitiale(Demande d) {
		if(indefini.contains(new Demande(d.etage(), Sens.INDEFINI))) {
			return new Demande(d.etage(), Sens.INDEFINI);
		}
		else return d;
	}

	/**
	 * demander(Demande)
	 * 
	 * executer une demande en fonction de l'etat du controller
	 */
	public void demander(Demande d) { // cas bleus7
		System.out.println("Appel " + d.toString());
		if (d.estIndefini()) {
			if(!indefini.contains(d)) indefini.add(d);
			if (d.etage() > this.position) {
				d = new Demande(d.etage(),Sens.MONTEE);
			}
			else if (d.etage() < this.position){
				d = new Demande(d.etage(),Sens.DESCENTE);
			}
		}
		switch (this.etatController) {
		case ATTENTE:
			liste.inserer(d);
			// cas particulier
			if (d.etage() == this.position) {
				// ToDo (ex : ouverture des portes)
			}
			else if (Math.abs(d.etage() - this.position) == 1) {
				iug.allumerBouton(demandeInitiale(d));
				if (d.etage() - this.position > 0) {
					this.sens = Sens.MONTEE;
				} else {
					this.sens = Sens.DESCENTE;
				}
				majEtat(EtatController.ARRET_IMMINENT);
			}
			// cas 1 bleu
			else if (d.etage() > this.position) {
				iug.allumerBouton(demandeInitiale(d));
				majEtat(EtatController.MONTEE);
			}

			// cas 2 bleu
			else if (d.etage() < this.position) {
				iug.allumerBouton(demandeInitiale(d));
				majEtat(EtatController.DESCENTE);
			}

			// cas 3 bleu
			
			break;
		case MONTEE:
		case DESCENTE:
			// MÃªme traitement pour l'etat Montee ou Descente en cas de demande
			// (pas de break entre les 2 cases)
			// cas 6 bleu
			liste.inserer(d);
			iug.allumerBouton(demandeInitiale(d));
			break;
		case ARRET_IMMINENT:
			// cas 10 bleu
			// on stocke la demande s'il y a plus d'un etage d'ï¿½cart entre la
			// position actuelle de la cabine, et l'etage de la demande,
			// ou si le sens de la cabine l'eloigne de l'etage de la demande.
			int absDeltaEtage = Math.abs(d.etage() - this.position);
			if (absDeltaEtage > 1 
					|| (this.sens != d.sens())) {
				liste.inserer(d);
				iug.allumerBouton(demandeInitiale(d));
			}
			break;
		case ARRET_ETAGE:
			// cas 14 bleu
			if (d.etage() != this.position || d.sens().toString() != this.etatController.toString()) {
				liste.inserer(d);
				iug.eteindreBouton(demandeInitiale(d));
				indefini.remove(demandeInitiale(d));
			}
			break;
		default:
			break;
		}
	}

	/**
	 * signalerChangementDEtage()
	 * 
	 * signaler le changement d'etage et mit a jour la position
	 */
	public void signalerChangementDEtage() { // cas noirs

		// mise Ã  jour de la position
		if (etatController.isEnMouvement()) {
			if (Sens.DESCENTE == sens) {
				position--;
			} else if (Sens.MONTEE == sens) {
				position++;
			}
		}
		System.out.println("Signal de franchissement de palier (Cabine en " + position + ")");

		switch (etatController) {
		case MONTEE:
		case DESCENTE:
			// cas 4 noir pour la montee
			// cas 5 noir pour la descente
			if (stopNext()) {
				majEtat(EtatController.ARRET_IMMINENT);
			} else {

				// cas 4 noir + cas 7 noir pour la montee
				// cas 5 noir + cas 8 noir pour la descente
				majPosition();
			}
			break;
		case ARRET_IMMINENT:
			// cas 9 noir
			// recherche de la demande en cours
			Demande demandeEnCours = liste.suivantDe(new Demande(this.position, this.sens));
			liste.supprimer(demandeEnCours);
			iug.eteindreBouton(demandeInitiale(demandeEnCours));
			indefini.remove(demandeInitiale(demandeEnCours));
			arreter();
			break;

		default:
			break;
		}
	}

	/**
	 * Arreter()
	 *  
	 * Met a jour l'état du controller en fonction de la demande suivante
	 * S'il n'y a pas de demande : Attente
	 * Si la demande suivante est supérieur a la position : montée
	 * Si la demande suivante est inférieur a la position : descente
	 * Sinon : arret etage
	 */
	void arreter() { // cas verts
		majEtat(EtatController.ARRET_ETAGE);
		Demande demandeSuivante = liste.suivantDe(new Demande(this.position, this.sens));
		// cas 13
		if (demandeSuivante == null) {
			majEtat(EtatController.ATTENTE);
		}
		// cas 11
		else if (demandeSuivante.etage() > this.position) {
			if (stopNext()) {
				majEtat(EtatController.ARRET_IMMINENT);
			} else {
				majEtat(EtatController.MONTEE);
			}
		}
		// cas 12
		else if (demandeSuivante.etage() < this.position) {
			if (stopNext()) {
				majEtat(EtatController.ARRET_IMMINENT);
			} else {
				majEtat(EtatController.DESCENTE);
			}
		}
	}

	/**
	 * arretDUrgence()
	 * 
	 * Vide la liste
	 * Eteint tous les boutons
	 * Passe l'état du controller en attente
	 */
	public void arretDUrgence() { // cas rouges
		System.out.println("Arrêt d'urgence");
		iug.eteindreTousBoutons();
		liste.vider();
		majEtat(EtatController.ATTENTE);
	}

	/**
	 * getPosition()
	 * 
	 * Retourne la position de la cabine
	 * @return position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * stopNext()
	 * 
	 * Est-ce que la cabine va devoir s'arreter la prochaine etage.
	 * IE la cabine monte, et il y a une demande en cabine.position + 1 , montée
	 * ou indefini
	 * ou la cabine descent, et il y a une demande en cabine.position - 1 ,
	 * descente ou indefini.
	 * @return boolean true si on doit s'arreter au prochain étagen false sinon
	 */
	boolean stopNext() {
		int positionRecherchee = this.position;
		Demande d = new Demande(this.position,this.sens);
		d = liste.suivantDe(d);
		if(this.position>d.etage()) this.sens = Sens.DESCENTE;
		else if(this.position<d.etage()) this.sens = Sens.MONTEE;

		if (Sens.MONTEE == this.sens) {
			positionRecherchee++;
		} else if (Sens.DESCENTE == this.sens) {
			positionRecherchee--;
		}
		Demande demandeRecherchee1 = new Demande(positionRecherchee, this.sens);
		if (liste.taille() == 1) {
			Demande demandeRecherchee3 = new Demande(positionRecherchee, Sens.getOppose(sens));
			return liste.contient(demandeRecherchee1) 
					|| liste.contient(demandeRecherchee3);
		} else {
			return liste.contient(demandeRecherchee1); 
		}
		
	}

	/**
	 * majEtat(EtatController)
	 * 
	 * Met a jour l'état du controller par rapport au paramètre
	 * @param etat
	 */
	private void majEtat(EtatController etat) {
		this.etatController = etat;
		if (etatController == EtatController.MONTEE) {
			sens = Sens.MONTEE;
		}

		if (etatController == EtatController.DESCENTE) {
			sens = Sens.DESCENTE;

		}
		if (etatController == EtatController.ARRET_IMMINENT) {
			cabine.arretProchainNiveau();
		}

		if (etatController == EtatController.MONTEE 
				|| etatController == EtatController.DESCENTE
				|| etatController == EtatController.ARRET_IMMINENT) {
			majPosition();
		} else {
			cabine.arreter();
		}

	}

	/**
	 * majPosition()
	 * 
	 * Change la position de la cabine
	 * Signal a la cabine de monter, descendre ou s'arreter selon notre etat
	 */
	private void majPosition() {
		// signaler à  l'IUG la position
		iug.changerPosition(this.position);
		switch (this.sens) {
		case MONTEE:
			cabine.monter();
			break;

		case DESCENTE:
			cabine.descendre();
			break;

		case INDEFINI:
			cabine.arreter();
			break;
		}

	}

	/**
	 * setPosition(int)
	 * 
	 * Permet de placer la cabine a la position passé en paramètre
	 * @param position
	 */
	public void setPosition(int position) {
		System.out.println("(Cabine en " + position + ")");
		this.position = position;
	}
}
