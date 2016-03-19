package tp4;

import test.IController;

public interface ICabine {

	void arretProchainNiveau();

	void arreter();

	void descendre();

	void monter();
	
	void setController(IController controller);
}
