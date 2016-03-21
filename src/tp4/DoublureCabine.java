package tp4;

import operative.ICabine;
import commande.IControleur;

public class DoublureCabine implements ICabine{

	protected int position ;

	private IControleur controleur ; 

	public void arreterProchainNiveau() {
		System.out.println("Arrêter prochain étage");
	}

	public void arreter() {
	}

	public void descendre() {	
		System.out.println("Descendre");
		position--;
	}

	public void monter() {
		System.out.println("Monter");
		position++ ;
	}

	/**
	 * Permet de donner un controleur a l'IUG
	 */
	public void assignerControleur(IControleur controleur) {
		this.controleur = controleur;
	}
}
