package outils;

/**
 * Enumeration de sens d'avance de l'ascenseur
 */
public enum Sens {
	MONTEE("^"), DESCENTE("v"), INDEFINI("-");

	private String sens;

	/**
	 * Constructeur de notre enum Sens
	 * @param sens
	 */
	Sens(String sens) {this.sens = sens;}

	/**
	 * Retourne le sens oppos� du sens qui est pass� en param�tre, reste ind�fini si le sens est ind�fini
	 * @param sens
	 * @return Sens
	 */
	public static Sens getOppose(Sens sens){
		if(sens == DESCENTE)
			return Sens.MONTEE;
		if(sens == MONTEE)
			return Sens.DESCENTE;
		return INDEFINI;		
	}

	/**
	 * Retourne un symbole correspondant au sens
	 * @return Sens
	 */
	public String toString() {return this.sens;}
}
