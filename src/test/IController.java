package test;

import java.util.ArrayList;

import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;
import tp4.*;
public interface IController {

	public void demander(Demande d);
	public void arretDUrgence();
	public void signalerChangementDEtage();
	public void setCabine(ICabine cabine);
	public ListeTrieeCirculaireDeDemandes getListe();
	public ArrayList<Demande> getIndefini();
}
