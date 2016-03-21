/** Classes et interfaces de la partie commande de l'ascenseur. */
package commande;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import outils.Demande;
import outils.Sens;

/**
 * ListeTrieeCirculaireDeDemandes
 */
public class ListeTrieeCirculaireDeDemandes implements IListeTrieeCirculaire<Demande> {
	private ArrayList<Demande> ref;
	private int etage;
	private int positionCabine;
	public Set<Demande> maListe;

	/**
	 * Constructeur de la class ListeTrieeCirculaireDeDemandes
	 * Initialise 
	 * <ul>
	 * 	<li>la position de la cabine à 0</li>
	 * 	<li>La taille de la liste de 2 fois le nombre d'étage (param) moins 1</li>
	 * 	<li>Une liste de référence de la même taille que notre liste</li>
	 * 	<li>Le nombre d'étage</li>
	 * </ul>
	 * @param i représentant le nombre d'étage 
	 */
	public ListeTrieeCirculaireDeDemandes(int i) {
		this.positionCabine = 0;
		ref = new ArrayList<Demande>(2 * (i - 1));
		maListe = new HashSet<Demande>(2 * (i - 1));

		// Remplir la liste de reference
		for (int c = 0; c < i - 1; c++) {
			ref.add(new Demande(c, Sens.MONTEE));
		}
		for (int c = i - 1; c > 0; c--) {
			ref.add(new Demande(c,Sens.DESCENTE));
		}
		this.etage = i;
	}

	/**
	 * Change la position de la cabine en fonction du paramètre
	 * @param int représentant la position
	 */
	public void setPositionCabine(int position) {
		this.positionCabine = position;
	}
	
	/**
	 * Retourne la position de la cabine
	 * @return int, positif
	 */
	public int getPositionCabine() {
		return this.positionCabine;
	}

	/**
	 * Retourne le nombre de demande présente dans la liste
	 * @return int
	 */
	public int taille() {
		return maListe.size();
	}

	/**
	 * Regarde si la liste est vide
	 * @return true si la liste est vide, false sinon
	 */
	public boolean estVide() {
		return maListe.isEmpty();
	}

	/**
	 * Vide entièrement la liste
	 */
	public void vider() {
		this.maListe.clear();
	}

	/**
	 * Regarde si la demande se trouve dans la liste
	 * @return true si la demande est dans la liste, false sinon
	 */
	public boolean contient(Demande e) {
		return this.maListe.contains(e);
	}

	/**
	 * Insere une demande dans la liste
	 * @throws IllegalArgumentException si l'étage demandé est :
	 * <ul>
	 * 	<li>supérieur au nombre d'étage</li>
	 * 	<li>égale à 0 et que la demande est en descente</li>
	 * 	<li>égale au nombre d'étage moins 1 et que la demande est en monté</li>
	 * 	<li>inférieur à 0</li>
	 * 	<li>supérieur au nombre d'étage -1</li>
	 * <ul>
	 * Ou si la demande est de type Sens.INDEFINI 
	 */
	public void inserer(Demande e) {
		if ((e.etage() > this.etage)
				|| (e.etage() == 0 && e.enDescente()) 
				|| (e.estIndefini())
				|| (e.etage() == this.etage - 1 && e.enMontee()) 
				|| (e.etage() < 0)
				|| (e.etage() > this.etage - 1) 
				) {
			throw new IllegalArgumentException();
		}
		maListe.add(e);
	}

	/**
	 * Supprime une demande de la liste
	 * @throws IllegalArgumentException en cas de liste vide
	 */
	public void supprimer(Demande e) {
		if(! maListe.contains(e)){
			throw new IllegalArgumentException("Demande absente de la liste");
		}
		maListe.remove(e);
	}

	/**
	 * Retourne la première demande suivante de la demande courante.
	 * Si aucune demande n'est trouvé elle retourne null
	 * @return Demande
	 */
	public Demande suivantDe(Demande courant) {
		int iCourantDansRef = this.ref.indexOf(courant);
		int indice = 0;
		for (int i = 0; i < ref.size(); i++) {
			indice = iCourantDansRef + i ;
			if(indice >= ref.size()){
				indice = indice % (ref.size()) ;
			}
			if(indice < 0){
				indice = ref.size() - indice; 
			}
			if(maListe.contains(this.ref.get(indice))){
				return this.ref.get(indice);
			}
		}
		return null;
	}

	/**
	 * Redéfinition de la méthode toString() pour afficher notre liste de demande sous la forme
	 * [demande 1, demande 2 , demande 3, demande n]
	 */
	@Override
	public String toString() {
		String s = "";
		for (Demande d : this.ref) {
			if (maListe.contains(d)) {
				s += d.toString() + ",";
			}
		}
		if (!s.isEmpty())
			s = "[" + s.substring(0, s.length() - 1) + "]";
		else
			s = "[]";
		return s;
	}
	
	/**
	 * Permet de modifier le nombre d'étage
	 * @param int etage
	 */
	public void setEtage(int etage) {
		this.etage = etage;
	}
	
	/**
	 * Retourne le nombre d'étage
	 * @return int nombre d'étage
	 */
	public int getEtage() {
		return this.etage;
	}

	public Set<Demande> getMaListe() {
		return maListe;
	}

	public void setMaListe(Set<Demande> maListe) {
		this.maListe = maListe;
	}
}
