package main;

import commande.Controleur;
import commande.IControleur;
import commande.IListeTrieeCirculaire;
import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;
import operative.Cabine;
import operative.IUG;
import operative.ICabine;
import operative.IIUG;

public class Main {
    public static void main(String[] args) throws Exception {
    	int nbEtages = 7; // nombre d'étages
        int hauteurEtage = 3; // hauteur d'un étage en nombre de pas
        IIUG iug = new IUG( nbEtages);
        ICabine cabine = new Cabine( 
        		500, // délai d'un pas
        		hauteurEtage, // hauteur d'un étage en nombre de pas
        		4); // délai de l'arrêt cabine
        IListeTrieeCirculaire<Demande> stock = new ListeTrieeCirculaireDeDemandes( nbEtages);
        IControleur controleur = new Controleur( nbEtages, iug, cabine, stock);
        cabine.assignerControleur( controleur);
        iug.assignerControleur( controleur);
    }
}
