package test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import outils.Demande;
import outils.Sens;
import operative.ICabine;
import operative.IIUG;
import operative.Cabine;
import operative.IUG;
import commande.Controleur;
import commande.ListeTrieeCirculaireDeDemandes;

public class ControleurTest {
	ListeTrieeCirculaireDeDemandes demandes;
	ICabine cabine;
	IIUG iug;
	Controleur controleur;
	PrintStream old;
	PrintStream ps;
	ByteArrayOutputStream baos;
	String lineSeparator = System.lineSeparator();

	@Before
	public void setUp()  {
		// Create a stream to hold the output
		this.baos = new ByteArrayOutputStream();
		// IMPORTANT: Save the old System.out!
		this.old = System.out;
		// Tell Java to use your special stream
		this.ps = new PrintStream(this.baos);
		System.setOut(this.ps);

		this.demandes = new ListeTrieeCirculaireDeDemandes(10);
		//public Cabine(long dureePas, int nbPasParEtage, int dureeArretCabine);
		this.cabine = new Cabine(50L, 5, 10);
		//public IUG(int n, java.lang.String date, boolean synchronisation) throws java.lang.Exception;
		//public IUG(int n) throws java.lang.Exception;
		try {
			this.iug  = new IUG(5);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.controleur = new Controleur(demandes);

		// on affecte le controleur a l'iug et la Cabine et on affecte la Cabine et l'iug au controleur
		this.controleur.setCabine(cabine);
		this.controleur.setIUG(iug);
		this.iug.assignerControleur(controleur);
		this.cabine.assignerControleur(controleur);
	}

	@After
	public void tearDown() {
		this.demandes = null;
		this.cabine = null;
		this.iug = null;
		this.controleur = null;
		// Put things back
		System.out.flush();
		System.setOut(old);
	}

	@Test
	public void casTest1() {
		this.controleur.setPosition(3);
		this.controleur.demander(new Demande(1, Sens.MONTEE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 3)" + lineSeparator
				+ "Appel 1^" + lineSeparator
				+ "Allumer bouton 1^" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 2)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 1)" + lineSeparator
				+ "Eteindre bouton 1^" + lineSeparator);

		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}

	@Test
	public void casTest2() {
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(5, Sens.INDEFINI));
		this.controleur.demander(new Demande(4, Sens.MONTEE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 5-" + lineSeparator
				+ "Allumer bouton 5-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 4^" + lineSeparator
				+ "Allumer bouton 4^" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5-" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}

	@Test
	public void casTest3() {
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(5, Sens.INDEFINI));
		this.controleur.demander(new Demande(4, Sens.DESCENTE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 5-" + lineSeparator
				+ "Allumer bouton 5-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 4v" + lineSeparator
				+ "Allumer bouton 4v" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5-" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4v" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}

	@Test
	public void casTest4() {
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(5, Sens.INDEFINI));
		this.controleur.demander(new Demande(4, Sens.MONTEE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(7, Sens.INDEFINI));
		this.controleur.demander(new Demande(6, Sens. DESCENTE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(3, Sens.INDEFINI));
		this.controleur.demander(new Demande(5, Sens.MONTEE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(4, Sens.DESCENTE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 5-" + lineSeparator
				+ "Allumer bouton 5-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 4^" + lineSeparator
				+ "Allumer bouton 4^" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5-" + lineSeparator
				+ "Appel 7-" + lineSeparator
				+ "Allumer bouton 7-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 6v" + lineSeparator
				+ "Allumer bouton 6v" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 6)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 7)" + lineSeparator
				+ "Eteindre bouton 7-" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 6)" + lineSeparator
				+ "Eteindre bouton 6v" + lineSeparator
				+ "Appel 3-" + lineSeparator
				+ "Allumer bouton 3-" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Appel 5^" + lineSeparator
				+ "Allumer bouton 5^" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Eteindre bouton 3-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 4v" + lineSeparator
				+ "Allumer bouton 4v" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4v" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}

	@Test
	public void casTest5() {
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(6, Sens.INDEFINI));
		this.controleur.demander(new Demande(5, Sens.DESCENTE));
		this.controleur.demander(new Demande(5, Sens.MONTEE));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(6, Sens.INDEFINI));
		this.controleur.demander(new Demande(4, Sens.INDEFINI));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 6-" + lineSeparator
				+ "Allumer bouton 6-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 5v" + lineSeparator
				+ "Allumer bouton 5v" + lineSeparator
				+ "Appel 5^" + lineSeparator
				+ "Allumer bouton 5^" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 6-" + lineSeparator
				+ "Appel 4-" + lineSeparator
				+ "Allumer bouton 4-" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 6)" + lineSeparator
				+ "Eteindre bouton 6-" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5v" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4-" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}
	@Test
	public void casTest6(){
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(3, Sens.INDEFINI));
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(4, Sens.MONTEE));
		this.controleur.demander(new Demande(3, Sens.DESCENTE));
		this.controleur.demander(new Demande(2, Sens.INDEFINI));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();


		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 3-" + lineSeparator
				+ "Allumer bouton 3-" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Eteindre bouton 3-" + lineSeparator
				+ "Appel 4^" + lineSeparator
				+ "Allumer bouton 4^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 3v" + lineSeparator
				+ "Allumer bouton 3v" + lineSeparator
				+ "Appel 2-" + lineSeparator
				+ "Allumer bouton 2-" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Eteindre bouton 4^" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Eteindre bouton 3v" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 2)" + lineSeparator
				+ "Eteindre bouton 2-" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}
	@Test
	public void casTest7(){
		this.controleur.setPosition(2);
		this.controleur.demander(new Demande(5, Sens.INDEFINI));
		this.controleur.signalerChangementDEtage();
		this.controleur.demander(new Demande(6, Sens.DESCENTE));
		this.controleur.arretUrgence();
		this.controleur.demander(new Demande(5, Sens.INDEFINI));
		this.controleur.signalerChangementDEtage();
		this.controleur.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+ "Appel 5-" + lineSeparator
				+ "Allumer bouton 5-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Appel 6v" + lineSeparator
				+ "Allumer bouton 6v" + lineSeparator
				+ "Arrêt d'urgence" + lineSeparator
				+ "Eteindre bouton 5-" + lineSeparator
				+ "Eteindre bouton 6v" + lineSeparator
				+ "Appel 5-" + lineSeparator
				+ "Allumer bouton 5-" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Monter" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 5)" + lineSeparator
				+ "Eteindre bouton 5-" + lineSeparator);
		assertEquals(attendu.toString(), this.baos.toString());
		assertTrue(this.demandes.estVide());
	}

}
