package outils;
/**
 * Enumeration de sens d'avance de l'ascenseur
 * @author CHOUKRI
 */
public enum Sens {
	MONTEE("^"), DESCENTE("v"), INDEFINI("-");
	
	private String sens;
	
	Sens(String sens) {this.sens = sens;}
	/**
	 * Retourne un symbole correspondant au sens
	 * @return Sens
	 */
	
	public static Sens getOppose(Sens sens){
		if(sens == DESCENTE)
		return Sens.MONTEE;
		if(sens == MONTEE)
			return Sens.DESCENTE;
		return INDEFINI;		
	}
	
	public String toString() {return this.sens;}
}
