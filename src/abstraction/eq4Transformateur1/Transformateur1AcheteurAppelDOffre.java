package abstraction.eq4Transformateur1;

import java.util.List;

import abstraction.eqXRomu.appelDOffre.IAcheteurAO;
import abstraction.eqXRomu.appelDOffre.OffreVente;
import abstraction.eqXRomu.appelDOffre.SuperviseurVentesAO;
import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;
 
/**
* @author ABBASSI Rayene
* Non utile pour le moment car aucun producteur ne vend par AO
* Code à revoir
*/
public class Transformateur1AcheteurAppelDOffre extends Transformateur1VendeurEncheres implements IAcheteurAO {
    private SuperviseurVentesAO supAO;

    public Transformateur1AcheteurAppelDOffre() {
		super();
	}


	public void initialiser() {
		super.initialiser();
		this.supAO = (SuperviseurVentesAO)(Filiere.LA_FILIERE.getActeur("Sup.AO"));
	}

	

    //A modifier les conditions et la quantité
	public void next() {
		super.next();
		this.journalTransactions.ajouter("=== STEP "+Filiere.LA_FILIERE.getEtape()+" ====================");
		for (Feve f : lesFeves) {
			if (this.getQuantiteEnStock(f, this.cryptogramme)<9500) {
				int quantite = 10000 ; 
				OffreVente ov = supAO.acheterParAO(this,  cryptogramme, f, quantite);
				journalTransactions.ajouter("   Je lance un appel d'offre de "+quantite+" T de "+f);
				if (ov!=null) { // on a retenu l'une des offres de vente
					journalTransactions.ajouter("AO finalise : on ajoute "+quantite+" T de "+f+" au stock");
                    this.ajouterAuStock(f,quantite,this.cryptogramme);
				}
			}
		}

		// On archive les contrats termines
		this.journalTransactions.ajouter("=================================");
	}

	public OffreVente choisirOV(List<OffreVente> propositions) {

		double cours = prixTFeveStockee.get((Feve) (propositions.get(0).getProduit()));
		if (propositions.size() == 0) {
			journalTransactions.ajouter("Je ne choisis pas l'offre car il n'y a pas d'offre");
			return null;
		}
		journalTransactions.ajouter("props: "+propositions);
		if (propositions.get(0).getPrixT()<=cours) {
			return propositions.get(0);
		} else {
			journalTransactions.ajouter("Je ne choisis pas l'offre car le prix est trop eleve");
			return null;
		}
	}

}
