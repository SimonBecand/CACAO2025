package abstraction.eq4Transformateur1.contratCadre;


import abstraction.eqXRomu.filiere.*;

import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eq4Transformateur1.Transformateur1Stocks; 

/*
 * @author MURY Julien
 * Cette classe décrit le comportement de Transfromateur1 lors d'un contrat cadre
 */

public class TransformateurContratCadre extends Transformateur1Stocks {

	protected SuperviseurVentesContratCadre supCCadre;

	public TransformateurContratCadre() {	
		super();		
	}
	


	public void initialiser() {
		super.initialiser();
		this.supCCadre = (SuperviseurVentesContratCadre) (Filiere.LA_FILIERE.getActeur("Sup.CCadre")); //Creation d'un superviseur pour la négociation du contrat cadre
	}

}
