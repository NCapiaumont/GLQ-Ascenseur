package test;

import outils.Sens;
import tp4.ICabine;

public class DoublureCabine implements ICabine{

	protected Sens sens ; 
	protected int position ;

	private IController controller ; 

	public void arretProchainNiveau() {
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
	 * Permet de donner un controller a l'IUG
	 */
	public void setController(IController controller) {
		this.controller = controller;
	}

}
