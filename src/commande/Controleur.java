package commande;

import java.util.ArrayList;

import operative.ICabine;
import operative.IIUG;
import outils.Demande;
import outils.Sens;


public class Controleur implements IControleur {
	private IListeTrieeCirculaire<Demande> liste;
	private ICabine cabine;
	private IIUG iug;
	private int position;
	private Sens sens;
	private EtatController etatController;
	ArrayList<Demande> indefini = new ArrayList<Demande>();

	/**
	 * Constructeur de la classe contoller
	 * Initialise
	 * <ul>
	 * 	<li>liste avec notre liste en param�tre</li>
	 * 	<li>position � 0</li>
	 * 	<li>l'�tat de notre controlleur � ATTENTE</li>
	 * <ul>
	 * @param liste
	 */
	public Controleur(ListeTrieeCirculaireDeDemandes liste) {
		this.liste = liste;
		this.position = 0;
		etatController = EtatController.ATTENTE;
	}
	
	public Controleur(int nbEtages, IIUG iug2, ICabine cabine2,
			IListeTrieeCirculaire<Demande> liste) {
		this.liste = liste;
		this.position = 0;
		etatController = EtatController.ATTENTE;
	}

	/**
	 * Retourne la liste des demandes ind�fini
	 * @return ArrayList<Demande>
	 */
	public ArrayList<Demande> getIndefini() {
		return indefini;
	}

	/**
	 * Retourne l'etat du controller
	 * @return EtatController
	 */
	public EtatController getEtatController() {
		return etatController;
	}

	/**
	 * enum pour representer l'etat du controller
	 */
	public enum EtatController {
		DESCENTE, MONTEE, ARRET_ETAGE, ARRET_IMMINENT, ARRET_IMMEDIAT, ATTENTE;

		/**
		 * Retourne si l'�tat ou se trouve le controlleur est un etat de mouvement de la cabine
		 * @return Boolean
		 */
		public boolean isEnMouvement() {
			return this == EtatController.DESCENTE || this == EtatController.MONTEE
					|| this == EtatController.ARRET_IMMINENT;
		}
	}

	/**
	 * Retourne la liste des demandes
	 * @return ListeTrieeCirculaireDeDemandes
	 */
	public IListeTrieeCirculaire<Demande> getListe() {
		return liste;
	}

	/**
	 * Permet de donner un IUG au Controlleur
	 */
	public void setIUG(IIUG iug) {
		this.iug = iug;
	}

	/**
	 * Permet de donner une Cabine au Controller
	 */
	public void setCabine(ICabine cabine) {
		this.cabine = cabine;
	}

	/**
	 * Retourne la demande sous sa forme initial, si elle �tait ind�fini retourne la demande ind�fini
	 * @param Demande
	 * @return Demande
	 */
	private Demande demandeInitiale(Demande d) {
		if(indefini.contains(new Demande(d.etage(), Sens.INDEFINI))) {
			return new Demande(d.etage(), Sens.INDEFINI);
		}
		else return d;
	}

	/**
	 * Executer une demande en fonction de l'etat du controller
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
			// cas 3 bleu
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
			break;
		case MONTEE:
		case DESCENTE:
			// M�me traitement pour l'etat Montee ou Descente en cas de demande
			// (pas de break entre les 2 cases)
			// cas 6 bleu
			liste.inserer(d);
			iug.allumerBouton(demandeInitiale(d));
			break;
		case ARRET_IMMINENT:
			// cas 10 bleu
			// on stocke la demande s'il y a plus d'un etage d'�cart entre la
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
			if (d.etage() != this.position || !d.sens().toString().equals(this.etatController.toString())) {
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
	 * Signaler le changement d'etage et mit a jour la position
	 */
	public void signalerChangementDEtage() { // cas noirs
		// mise � jour de la position
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
	 * Met a jour l'�tat du controller en fonction de la demande suivante
	 * S'il n'y a pas de demande : Attente
	 * Si la demande suivante est sup�rieur a la position : mont�e
	 * Si la demande suivante est inf�rieur a la position : descente
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
	 * Vide la liste
	 * Eteint tous les boutons
	 * Passe l'�tat du controller en attente
	 */
	public void arretUrgence() { // cas rouges
		System.out.println("Arr�t d'urgence");
		
		for (Demande monBouton : this.liste.maListe) {
			Demande boutonDeMaDemandeIndefinie = new Demande(monBouton.etage(), Sens.INDEFINI);
			if (this.indefini.contains(boutonDeMaDemandeIndefinie)) {
				iug.eteindreBouton(boutonDeMaDemandeIndefinie);
				//System.out.println("Eteindre bouton "+boutonDeMaDemandeIndefinie.toString());
			} else {
				iug.eteindreBouton(monBouton);
				//System.out.println("Eteindre bouton "+monBouton.toString());
			}
		}
		
		liste.vider();
		majEtat(EtatController.ATTENTE);
	}

	/**
	 * Retourne la position de la cabine
	 * @return int position
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Est-ce que la cabine va devoir s'arreter la prochaine etage.
	 * IE la cabine monte, et il y a une demande en cabine.position + 1 , mont�e
	 * ou indefini
	 * ou la cabine descent, et il y a une demande en cabine.position - 1 ,
	 * descente ou indefini.
	 * @return boolean true si on doit s'arreter au prochain �tagen false sinon
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
	 * Met a jour l'�tat du controller par rapport au param�tre
	 * @param EtatController etat
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
			cabine.arreterProchainNiveau();
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
	 * Change la position de la cabine
	 * Signal a la cabine de monter, descendre ou s'arreter selon notre etat
	 */
	private void majPosition() {
		// signaler � l'IUG la position
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
	 * Permet de placer la cabine a la position pass� en param�tre
	 * @param position
	 */
	public void setPosition(int position) {
		System.out.println("(Cabine en " + position + ")");
		this.position = position;
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}