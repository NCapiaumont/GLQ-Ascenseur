package commande;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

	//Permet le calcul des temps d'attentes. (Voir dans insererDemande(), supprimerDemande(Demande d), demander(Demande(d))
	//Cette map stocke pour chaque demande de palier, la date d'ajout.
	protected Map<Demande, Date> attentes = new HashMap<Demande, Date>();

	//attente maximum ecoulee
	private long attenteMax = 0 ;
	//attente minimum
	private long attenteMin = Long.MAX_VALUE ;
	//cumul des attentes
	private long attenteTotale = 0 ;
	//attente moyenne
	private long attenteMoyenne = 0;
	//cumul des demandes
	private int nbDemande = 0;


	/**
	 * Constructeur de la classe contoller
	 * Initialise
	 * <ul>
	 * 	<li>liste avec notre liste en paramètre</li>
	 * 	<li>position à 0</li>
	 * 	<li>l'état de notre controlleur à ATTENTE</li>
	 * <ul>
	 * @param liste
	 */
	public Controleur(ListeTrieeCirculaireDeDemandes liste) {
		this.liste = liste;
		this.position = 0;
		etatController = EtatController.ATTENTE;
	}

	public Controleur(int nbEtages, IIUG iug, ICabine cabine,
			IListeTrieeCirculaire<Demande> liste) {
		this.liste = liste;
		this.position = 0;
		this.etatController = EtatController.ATTENTE;
		this.iug = iug;
		this.cabine = cabine;
		this.liste.setEtage(nbEtages);
	}

	/**
	 * Retourne la liste des demandes indéfini
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

	private void insereDemande(Demande demande, boolean isPallier){
		liste.inserer(demande);
		
		//si demande pallier, on stocke la date d'insertion
		if (isPallier){
			attentes.put(demande,new Date());
			nbDemande = nbDemande+1;
		}
	}
	
	/**
	 * Supprime une demande de la liste des demande.
	 * Si le demande était en attende, calcul le temps écoulé
	 * @param d
	 */
	private void supprimerDemande(Demande d){
		liste.supprimer(d);
		
		//Si la demande Ã©tait en attente, calcul du temps passÃ©, et mise Ã  jour des compteurs.
		if(attentes.containsKey(d)){
			Date dd = attentes.get(d);
			Date df = new Date();
			long tpsAttente = df.getTime() - dd.getTime();
			
			//Suppression de la demande de la liste d'attente
			attentes.remove(d);
			
			//Calcul de l'attente totale
			attenteTotale = attenteTotale+tpsAttente;
			
			//Calcul de l'attente max
			if(tpsAttente>attenteMax){
				attenteMax=tpsAttente;
			}
			
			//Calcul de l'attente min
			if(tpsAttente<attenteMin){
				attenteMin=tpsAttente;
			}
			
			//Calcul de l'attente moyenne
			attenteMoyenne=attenteTotale/nbDemande;
			
			//A afficher dans l'IUG
		}
	}


	/**
	 * enum pour representer l'etat du controller
	 */
	public enum EtatController {
		DESCENTE, MONTEE, ARRET_ETAGE, ARRET_IMMINENT, ARRET_IMMEDIAT, ATTENTE;

		/**
		 * Retourne si l'état ou se trouve le controlleur est un etat de mouvement de la cabine
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
	 * Retourne la demande sous sa forme initial, si elle était indéfini retourne la demande indéfini
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
		boolean isPallier = true; 
		System.out.println("Appel " + d.toString());
		if (d.estIndefini()) {
			isPallier = false; 
			if(!indefini.contains(d)) indefini.add(d);
			if (d.etage() >= this.position) {
				if (d.etage() == this.liste.getEtage()-1)
					d = new Demande(d.etage(), Sens.DESCENTE);
				else 
					d = new Demande(d.etage(),Sens.MONTEE);
			}
			else if (d.etage() <= this.position){
				if (d.etage() == 0)
					d = new Demande(d.etage(), Sens.MONTEE);
				else 
					d = new Demande(d.etage(),Sens.DESCENTE);
			}
		}
		switch (this.etatController) {
		case ATTENTE:
			insereDemande(d, isPallier);
			System.out.println(d);
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

				System.out.println(demandeInitiale(d)+" "+iug);
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
			// Même traitement pour l'etat Montee ou Descente en cas de demande
			// (pas de break entre les 2 cases)
			// cas 6 bleu
			insereDemande(d, isPallier);
			iug.allumerBouton(demandeInitiale(d));
			break;
		case ARRET_IMMINENT:
			// cas 10 bleu
			// on stocke la demande s'il y a plus d'un etage d'écart entre la
			// position actuelle de la cabine, et l'etage de la demande,
			// ou si le sens de la cabine l'eloigne de l'etage de la demande.
			int absDeltaEtage = Math.abs(d.etage() - this.position);
			if (absDeltaEtage > 1 
					|| (this.sens != d.sens())) {
				insereDemande(d, isPallier);
				iug.allumerBouton(demandeInitiale(d));
			}
			break;
		case ARRET_ETAGE:
			// cas 14 bleu
			if (d.etage() != this.position || !d.sens().toString().equals(this.etatController.toString())) {
				insereDemande(d, isPallier);
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
	public synchronized void signalerChangementDEtage() { // cas noirs
		// mise à  jour de la position
		Demande demandeEnCours;
		if (etatController.isEnMouvement()) {
			if (Sens.DESCENTE == sens) {
				position--;
				iug.changerPosition(this.position);
			} else if (Sens.MONTEE == sens) {
				position++;
				iug.changerPosition(this.position);
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
			if(this.position == this.liste.getEtage()-1){
				demandeEnCours = liste.suivantDe(new Demande(this.position -1, Sens.DESCENTE));	
			}
			else if(this.position == 0) {
				demandeEnCours = liste.suivantDe(new Demande(this.position , Sens.MONTEE));
			}
			else 		
				demandeEnCours = liste.suivantDe(new Demande(this.position , this.sens));			
			supprimerDemande(demandeEnCours);
			iug.eteindreBouton(demandeInitiale(demandeEnCours));
			indefini.remove(demandeInitiale(demandeEnCours));
			arreter();
			break;
		default:
			break;
		}

	}

	/** 
	 * Met a jour l'état du controller en fonction de la demande suivante
	 * S'il n'y a pas de demande : Attente
	 * Si la demande suivante est supérieur a la position : montée
	 * Si la demande suivante est inférieur a la position : descente
	 * Sinon : arret etage
	 */
	void arreter() { // cas verts
		Demande demandeSuivante;
		majEtat(EtatController.ARRET_ETAGE);
		if(this.position == this.liste.getEtage()-1){
			demandeSuivante = liste.suivantDe(new Demande(this.position -1, Sens.DESCENTE));	
		}
		else if(this.position == 0) {
			demandeSuivante = liste.suivantDe(new Demande(this.position , Sens.MONTEE));
		}
		else 			
			demandeSuivante = liste.suivantDe(new Demande(this.position , this.sens));
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
	 * Passe l'état du controller en attente
	 */
	public void arretUrgence() { // cas rouges
		System.out.println("Arrêt d'urgence");

		for (Demande monBouton : this.liste.getMaListe()) {
			Demande boutonDeMaDemandeIndefinie = new Demande(monBouton.etage(), Sens.INDEFINI);
			if (this.indefini.contains(boutonDeMaDemandeIndefinie)) {
				iug.eteindreBouton(boutonDeMaDemandeIndefinie);
			} else {
				iug.eteindreBouton(monBouton);
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
		return this.position;
	}

	/**
	 * Est-ce que la cabine va devoir s'arreter la prochaine etage.
	 * IE la cabine monte, et il y a une demande en cabine.position + 1 , montée
	 * ou indefini
	 * ou la cabine descent, et il y a une demande en cabine.position - 1 ,
	 * descente ou indefini.
	 * @return boolean true si on doit s'arreter au prochain étagen false sinon
	 */
	boolean stopNext() {
		int positionRecherchee = this.position;
		Demande d;
		if(this.position == this.liste.getEtage()-1){
			d = new Demande(this.position , Sens.DESCENTE);	
		}
		else if(this.position == 0) {
			d = new Demande(this.position , Sens.MONTEE);
		}
		else {
			d = new Demande(this.position,this.sens);
		}
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
	 * Met a jour l'état du controller par rapport au paramètre
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
	 * Permet de placer la cabine a la position passé en paramètre
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
