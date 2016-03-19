package commande;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import outils.Demande;
import outils.Sens;

public class ListeTrieeCirculaireDeDemandes implements IListeTrieeCirculaire<Demande> {
	private ArrayList<Demande> ref;
	private int etage;
	private int positionCabine;
	public Set<Demande> maListe;

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

	public void setPositionCabine(int position) {
		this.positionCabine = position;
	}

	public int getPositionCabine() {
		return this.positionCabine;
	}

	public int taille() {
		return maListe.size();
	}

	public boolean estVide() {
		return maListe.isEmpty();
	}

	public void vider() {
		this.maListe.clear();
	}

	public boolean contient(Demande e) {
		return this.maListe.contains(e);
	}

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
		//if (!maListe.contains(e)) {
			maListe.add(e);
		//}
	}

	public void supprimer(Demande e) {
		if(! maListe.contains(e)){
			throw new IllegalArgumentException("Demande absente de la liste");
		}
		maListe.remove(e);
	}

	// recherche la premier demande suivante de la demande courante.
	public Demande suivantDe(Demande courant) {

		int iCourantDansRef = this.ref.indexOf(courant);
		int indice;
				
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
}
