package tp4;

import java.util.ArrayList;

import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;

public interface IController {
	public void demander(Demande d);
	public void arretUrgence();
	public void signalerChangementDEtage();
	public void setCabine(ICabine cabine);
	public ListeTrieeCirculaireDeDemandes getListe();
	public ArrayList<Demande> getIndefini();
}
