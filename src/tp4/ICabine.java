package tp4;

public interface ICabine {
	void arretProchainNiveau();
	void arreter();
	void descendre();
	void monter();
	void setController(IController controller);
}
