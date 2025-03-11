package abstraction.eq5Transformateur2;

import abstraction.eqXRomu.bourseCacao.IAcheteurBourse;
import abstraction.eqXRomu.produits.Feve;

public class AcheteurBourse extends Transformateur2Acteur implements IAcheteurBourse {

    public AcheteurBourse(){
        super();
}

   
    public double demande(Feve f, double cours) {
        if (f == Feve.F_MQ) { 
            return 80.0; // Achat de 80 tonnes de F_MQ à chaque next
        }
        return 0.0;
    }

    
    private int cryptogramme;

    public void notificationAchat(Feve f, double quantiteEnT, double coursEnEuroParT) {
        super.stockFeve.get(f).ajouter(this, quantiteEnT, this.cryptogramme);
		

	}


   
    public void notificationBlackList(int dureeEnStep) {
        super.journal.ajouter("Blacklist de la bourse pour " + dureeEnStep + " steps");
    }
}
    

