package outils;

public class Demande {
	private Integer palier;
	private Sens sens;

	/**
	 * Constructeur de la demande prend en paramètre un étage et un sens
	 * @param p correspond au palier demandé
	 * @param s correspond au sens d'avancement de la cabine
	 */
	public Demande(int p, Sens s){
		this.palier = p;
		this.sens = s;
	}

	/**
	 * Constructeur qui crée une demande par default
	 * Initialise le palier ou se trouve la cabine par défaut à 0 et le sens de la cabine à Sens.INDEFINI
	 */
	public Demande() {
		this.palier = 0;
		this.sens = Sens.INDEFINI;
	}

	/**
	 * Retourne true si le sens de l'ascenseur est indefini 
	 * @return boolean sens
	 */
	public boolean estIndefini() {
		return this.sens == Sens.INDEFINI;
	}

	/**
	 * retourne le palier de la demande 
	 * @return int palier
	 */
	public Integer etage() {
		return this.palier;
	}

	/**
	 * retourne si la demande est pour monter
	 * @return boolean Sens.MONTEE
	 */
	public boolean enMontee() {
		return this.sens == Sens.MONTEE;
	}

	/**
	 * retourne si la demande est pour descendre 
	 * @return boolean Sens.DENSCENTE
	 */
	public boolean enDescente() {
		return this.sens == Sens.DESCENTE;
	}

	/**
	 * passer au etage suivant selon le sens de la demande
	 * @throws ExceptionCabineArretee
	 */
	public void passeEtageSuivant() throws ExceptionCabineArretee {
		if(this.sens == Sens.MONTEE)
			this.palier++;
		else if(this.sens == Sens.DESCENTE)
			this.palier--;
		else if(this.sens == Sens.INDEFINI)
			throw new ExceptionCabineArretee();
	} 
	/**
	 * retourne le sens de la demande
	 * @return Sens sens
	 */
	public Sens sens() {
		return this.sens;
	}

	/** 
	 * changer le sens de la demande
	 * @param s Sens 
	 */
	public void changeSens(Sens s) {
		this.sens = s;
	}

	/**
	 * Affiche le le palier et le sens de la demande
	 */
	@Override
	public String toString() {
		return this.palier+this.sens.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + palier;
		result = prime * result + ((sens == null) ? 0 : sens.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Demande other = (Demande) obj;
		if (palier != other.palier)
			return false;
		if (sens != other.sens)
			return false;
		return true;
	}
}
