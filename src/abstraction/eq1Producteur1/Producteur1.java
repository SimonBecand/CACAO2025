package abstraction.eq1Producteur1;

import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;

import java.util.List;

import abstraction.eqXRomu.contratsCadres.Echeancier;

// ADRIEN BUCHER

public class Producteur1 extends Producteur1Bourse {

    private Producteur1ContratCadre contratCadre;
    protected Feve typeFeve; // Type de fève géré par ce producteur
   // protected Stock stock; // Instance de Stock pour gérer les stocks

    public Producteur1() {
        super();
        this.contratCadre = new Producteur1ContratCadre(); // Initialisation de l'instance
   //     this.stock = new Stock(); // Initialisation de Stock avec une référence à Producteur1
    }

    @Override
    public boolean vend(IProduit produit) {
        return contratCadre.vend(produit);
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        return contratCadre.contrePropositionDuVendeur(contrat);
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        return contratCadre.propositionPrix(contrat);
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        return contratCadre.contrePropositionPrixVendeur(contrat);
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        contratCadre.notificationNouveauContratCadre(contrat);
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        return contratCadre.livrer(produit, quantite, contrat);
    }
	public List<Variable> getIndicateurs() {
		return super.getIndicateurs();
	}

    @Override
    public void next() {
        super.next(); // mise à jour stock / journal
        //  livraison automatique des contrats cadres
    }

    


}
