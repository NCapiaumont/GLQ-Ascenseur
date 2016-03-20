package test;

import java.util.ArrayList;

import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;
import outils.Sens;
import tp4.IIUG;

public class DoublureIUG implements IIUG{

	private IController controller;

	public void eteindreTousBoutons() {
		ListeTrieeCirculaireDeDemandes maListe = controller.getListe();
		ArrayList<Demande> maListeIndefinie = controller.getIndefini();
		for (Demande monBouton : maListe.maListe) {
			Demande boutonDeMaDemandeIndefinie = new Demande(monBouton.etage(), Sens.INDEFINI);
			if (maListeIndefinie.contains(boutonDeMaDemandeIndefinie)) {
				System.out.println("Eteindre bouton "+boutonDeMaDemandeIndefinie.toString());
			} else {
				System.out.println("Eteindre bouton "+monBouton.toString());
			}
		}
	}

	public void allumerBouton(Demande d) {
		System.out.println("Allumer bouton "+d.toString());
	}

	public void eteindreBouton(Demande d) {
		ArrayList<Demande> maListeIndefinie = controller.getIndefini();
		Demande boutonDeMaDemandeIndefinie = new Demande(d.etage(), Sens.INDEFINI);
		if (maListeIndefinie.contains(boutonDeMaDemandeIndefinie)) {
			System.out.println("Eteindre bouton "+boutonDeMaDemandeIndefinie.toString());
		} else {
			System.out.println("Eteindre bouton "+d.toString());
		}
	}

	public void changerPosition(int position) {
	}

	/**
	 * Permet de donner un controller a l'IUG
	 */
	public void setController(IController controller) {
		this.controller = controller;
	}
}
