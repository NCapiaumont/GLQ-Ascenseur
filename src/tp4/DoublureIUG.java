package tp4;

import java.util.ArrayList;

import outils.Demande;
import outils.Sens;
import tp4.IIUG;
import commande.IControleur;
import commande.ListeTrieeCirculaireDeDemandes;

public class DoublureIUG implements IIUG{

	private IControleur controleur;

	public void eteindreTousBoutons() {
		//ListeTrieeCirculaireDeDemandes maListe = controleur.getListe();
		//ArrayList<Demande> maListeIndefinie = controleur.getIndefini();
		/*for (Demande monBouton : maListe.maListe) {
			Demande boutonDeMaDemandeIndefinie = new Demande(monBouton.etage(), Sens.INDEFINI);
			if (maListeIndefinie.contains(boutonDeMaDemandeIndefinie)) {
				System.out.println("Eteindre bouton "+boutonDeMaDemandeIndefinie.toString());
			} else {
				System.out.println("Eteindre bouton "+monBouton.toString());
			}
		}*/
	}

	public void allumerBouton(Demande d) {
		System.out.println("Allumer bouton "+d.toString());
	}

	public void eteindreBouton(Demande d) {
		//ArrayList<Demande> maListeIndefinie = controleur.getIndefini();
		Demande boutonDeMaDemandeIndefinie = new Demande(d.etage(), Sens.INDEFINI);
		/*if (maListeIndefinie.contains(boutonDeMaDemandeIndefinie)) {
			System.out.println("Eteindre bouton "+boutonDeMaDemandeIndefinie.toString());
		} else {
			System.out.println("Eteindre bouton "+d.toString());
		}*/
	}

	@Override
	public void changerPosition(int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setController(IController controller) {
		//this.controleur = controller;
		
	}
}
