package tp4;

import outils.Demande;
import test.IController;

public interface IIUG {


	void eteindreTousBoutons();

	void allumerBouton(Demande d);

	void eteindreBouton(Demande s);
	
	void changerPosition(int position); 
	
	void setController(IController controller);

}
