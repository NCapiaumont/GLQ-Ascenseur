package test;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import commande.ListeTrieeCirculaireDeDemandes;
import outils.Demande;
import outils.Sens;
import tp4.*;

public class ControllerTest {
	ListeTrieeCirculaireDeDemandes demandes;
	ICabine Cabine;
	IIUG iug;
	Controller controller;
	PrintStream old;
	PrintStream ps;
	ByteArrayOutputStream baos;
	String lineSeparator = System.lineSeparator();
	
	@Before
	public void setUp()  {
		// Create a stream to hold the output
		baos = new ByteArrayOutputStream();
		// IMPORTANT: Save the old System.out!
		old = System.out;
		// Tell Java to use your special stream
		ps = new PrintStream(baos);
		System.setOut(ps);
		
		demandes = new ListeTrieeCirculaireDeDemandes(10);
		Cabine = new DoublureCabine();
		iug  = new DoublureIUG();
		controller = new Controller(demandes);

		// on affecte le controller a l'iug et la Cabine et on affecte la Cabine et l'iug au controller
		controller.setCabine(Cabine);
		controller.setIUG(iug);
		iug.setController(controller);
		Cabine.setController(controller);
	}

	@After
	public void tearDown() {
		demandes = null;
		Cabine = null;
		iug = null;
		controller = null;
		// Put things back
		System.out.flush();
		System.setOut(old);
	}

	@Test
	public void casTest1() {
		controller.setPosition(3);
		controller.demander(new Demande(1, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

		StringBuffer attendu = new StringBuffer("(Cabine en 3)" + lineSeparator
				+ "Appel 1^" + lineSeparator
				+ "Allumer bouton 1^" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 2)" + lineSeparator
				+ "Arrêter prochain étage" + lineSeparator
				+ "Descendre" + lineSeparator
				+ "Signal de franchissement de palier (Cabine en 1)" + lineSeparator
				+ "Eteindre bouton 1^" + lineSeparator);

		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}

	@Test
	public void casTest2() {
		controller.setPosition(2);
		controller.demander(new Demande(5, Sens.INDEFINI));
		controller.demander(new Demande(4, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

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
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}

	@Test
	public void casTest3() {
		controller.setPosition(2);
		controller.demander(new Demande(5, Sens.INDEFINI));
		controller.demander(new Demande(4, Sens.DESCENTE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

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
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}

	@Test
	public void casTest4() {
		controller.setPosition(2);
		controller.demander(new Demande(5, Sens.INDEFINI));
		controller.demander(new Demande(4, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.demander(new Demande(7, Sens.INDEFINI));
		controller.demander(new Demande(6, Sens. DESCENTE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.demander(new Demande(3, Sens.INDEFINI));
		controller.demander(new Demande(5, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.demander(new Demande(4, Sens.DESCENTE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

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
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}

	@Test
	public void casTest5() {
		controller.setPosition(2);
		controller.demander(new Demande(6, Sens.INDEFINI));
		controller.demander(new Demande(5, Sens.DESCENTE));
		controller.demander(new Demande(5, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.demander(new Demande(6, Sens.INDEFINI));
		controller.demander(new Demande(4, Sens.INDEFINI));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

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
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}
	@Test
	public void casTest6(){
		controller.setPosition(2);
		controller.demander(new Demande(3, Sens.INDEFINI));
		controller.demander(new Demande(4, Sens.MONTEE));
		controller.signalerChangementDEtage();
		controller.demander(new Demande(3, Sens.DESCENTE));
		controller.demander(new Demande(2, Sens.INDEFINI));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();
		
		StringBuffer attendu = new StringBuffer("(Cabine en 2)" + lineSeparator
				+"Appel 3-" + lineSeparator
				+"Allumer bouton 3-" + lineSeparator
				+"Monter" + lineSeparator
				+"Appel 4^" + lineSeparator
				+"Arrêter prochain étage" + lineSeparator
				+"Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+"Eteindre bouton 3-" + lineSeparator
				+"Appel 3v" + lineSeparator
				+"Appel 2-" + lineSeparator
				+"Monter" + lineSeparator
				+"Arrêter prochain étage" + lineSeparator
				+"Signal de franchissement de palier (Cabine en 4)" + lineSeparator
				+"Eteindre bouton 4^" + lineSeparator
				+"Descendre" + lineSeparator
				+"Signal de franchissement de palier (Cabine en 3)" + lineSeparator
				+"Arrêter prochain étage" + lineSeparator
				+"Signal de franchissement de palier (Cabine en 2)" + lineSeparator
				+"Eteindre bouton 2-" + lineSeparator);
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());
	}
	@Test
	public void casTest7(){
		controller.setPosition(2);
		controller.demander(new Demande(5, Sens.INDEFINI));
		controller.signalerChangementDEtage();
		controller.demander(new Demande(6, Sens.DESCENTE));
		controller.arretDUrgence();
		controller.demander(new Demande(5, Sens.INDEFINI));
		controller.signalerChangementDEtage();
		controller.signalerChangementDEtage();

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
		assertEquals(attendu.toString(), baos.toString());
		assertTrue(demandes.estVide());

	}

}
