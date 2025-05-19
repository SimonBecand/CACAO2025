package abstraction.eq6Transformateur3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.filiere.*;
import abstraction.eqXRomu.produits.IProduit;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre; 
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.produits.Feve;

// @author Eric Schiltz & Henri Roth & Florian Malveau

public class Transformateur3ContratCadreAcheteur extends Transformateur3ContratCadreVendeur implements IAcheteurContratCadre{
	protected List<ExemplaireContratCadre> contratsObsoletes;
    protected HashMap<IProduit, Double> coutMoyFeves; //estimation du cout de chaque fèves
	protected HashMap<IProduit, Double> fevesReceptionneesThisStep; //estimation du cout de chaque fèves
	protected HashMap<IProduit, Double> demande_vente;
	public Transformateur3ContratCadreAcheteur() {
		this.ContratsAcheteur=new LinkedList<ExemplaireContratCadre>();
		this.contratsObsoletes =new LinkedList<ExemplaireContratCadre>();
		//on crée la variable/tableau demande_vente qui répertorie les demandes de la ventes 
		//en les différentes fèves pour le step d'après
		//on va commander pour satisfaire cette commande en ventes c'est notre stratégie actuelle.
		demande_vente = new HashMap<IProduit, Double>();
		//on initialise juste pour les différentes fèves on s'en fout de la valeur initiale
		demande_vente.put(Feve.F_BQ,(productionMax*0.9)/3);
        demande_vente.put(Feve.F_BQ_E,(productionMax*0.9)/6);
        demande_vente.put(Feve.F_MQ,(productionMax*0.9)/6);
        demande_vente.put(Feve.F_HQ_E,(productionMax*0.9)/3);
	}
	//@author Henri Roth
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		//on regarde quel produit est concerné par le contrat
        IProduit p = contrat.getProduit();
        //on regarde si on vend/produit bien de ce chocolat
        if(super.fevesUtiles.contains(p)){
            // si oui on calcule de la quantite par step de chocolat que l'on veut livrer
            //on regarde quelle est notre demande de vente du produit 
            double vente = demande_vente.get(p);
            //on récupère l'échéancier
            Echeancier e = contrat.getEcheancier();
            //on récupère le premier step
            int stepdebut = e.getStepDebut();
            //on rearde la quantité  que l'on peut obtenir proposée
            double QuantiteStep = e.getQuantite(stepdebut);
            // si la quantité que l'on nous propose est inférieure à ce que l'on a besoin on accepte
            if(QuantiteStep < vente){
                //et on met à jour nos capacités de vente max
                return contrat.getEcheancier();
            }
            //sinon
            else{
                //si notre demande est vrmt trop petite : on abandonne 
                if(vente < 100){
                    return null;
                }
                //sinon on refait l'échéancier avec ce que l'on a; 
                else{
                    //et on met à jour nos capacités de vente max
                    int nb = e.getNbEcheances();
                    return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, nb, vente);
                }
            }
        }
        //si on ne le vend/produit pas on ne fait rien 
        return null;
    }
	
	//@author Henri Roth
	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		//journalCC.ajouter("## NEGOCIATION "+contrat.getNumero()+" - Prix proposé : "+contrat.getPrix());
		return contrat.getPrix()*1.2;
	}

	//@author Florian Malveau
	public void initialiser(){
		super.initialiser();
		
		//Initialisation estimation coûts de fèves
		this.coutMoyFeves = new HashMap<IProduit, Double>();
		for(IProduit feve : super.stockFeves.getListProduitSorted()){
			this.coutMoyFeves.put(feve,0.0);
		}
	}

	
	public void next() {
		super.next();
		SuperviseurVentesContratCadre supCCadre = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");


		//gestion de la suppression des contrats obsolètes
		journalCC.ajouter("======= Contrats cadres finis période"+Filiere.LA_FILIERE.getEtape()+"=======");
		// On enleve les contrats obsolete (nous pourrions vouloir les conserver pour "archive"...)
		for (ExemplaireContratCadre contrat : this.ContratsAcheteur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
				journalCC.ajouter("CONTRAT n°"+contrat.getNumero()+" SUPPRIME");
			}
		}
		this.ContratsAcheteur.removeAll(contratsObsoletes);
		for (ExemplaireContratCadre contrat : this.ContratsVendeur) {
			if (contrat.getQuantiteRestantALivrer()==0.0 && contrat.getMontantRestantARegler()==0.0) {
				contratsObsoletes.add(contrat);
			}
		}
		this.ContratsVendeur.removeAll(contratsObsoletes);
		journalCC.ajouter("======================");

		// @author Eric Schiltz
		//on parcourt toutes les fèves
		for(IProduit feve : super.fevesUtiles){
			//on parcourt tous les chocolats et on va regarder pour tous les chocolats que l'on vend
			//et pour chaque on va noter combien de notre fève il faut produire pour remplir la 
			//demande
			//quantité de la fève que l'on doit produire
			double b = 0; 
			for(IProduit choco : super.lesChocolats){
				//somme pour ce chocolat de ce que l'on doit pour l'instant livrer au step suivant
				double a = 0;
				//on parcourt tous les contrats cadres
				for (ExemplaireContratCadre contrat : ContratsVendeur){
					//pour chaque contrat on regarde si il concerne ce produit 
					if (contrat.getProduit()==choco){
						//si oui on additionne ce que l'on doit pour l'instant livrer au step suivant 
						a += contrat.getQuantiteALivrerAuStep();
					}
				}
				//on convertit la somme de chocolat en quantité de fève si il s'agit 
				//bien d'un chocolat qui dépend de notre fève
				//chaque chocolat éta,t rattché à une fève particulière
				//on pourrait faire autrement et ne pas parcourir la liste des chocolats du coup
				//optimisation mineure à faire
				if (choco==bollo && feve == Feve.F_BQ_E){
					b+= a*((15500)/52000);
				}
				if (choco==fraud && feve == Feve.F_BQ){
					b+= a*((15500)/52000);
				}
				if (choco==hypo && feve == Feve.F_HQ_E){
					b+= a;
				}
				if (choco==arna && feve == Feve.F_MQ){
					b+= a*((13000)/26000);
				}
			//on met alors à jour demande_vente
			demande_vente.replace(feve,b);
			}
		}


	//maintenant on fait des propositions de contrat cadre
		//on parcourt tous les types de chocolat
        for(IProduit feve : super.fevesUtiles){
            //on parcourt les acteurs de la filière
			// Pour tous les types de fèves
			// Proposition d'un nouveau contrat a tous les vendeurs possibles
			// Fève utilisée : BQ / BQ_E / MQ / HQ_E
            for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
                //si l'acteur n'est pas nous et si l'acteur achète des contrats cadres et s'il achète
                //du chocolat par contrat cadre
                if (acteur!=this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(feve)) {
                    //on propose un contrat cadre à l'acteur en question qui démarre à l'étape
                    //suivante de la filière, qui dure 10 step 
					double demande = demande_vente.get(feve);
					//besoin de vérifier que la demande est bien strictement supérieur à 0
					if(demande != 0){
                    	supCCadre.demandeAcheteur((IAcheteurContratCadre)this, ((IVendeurContratCadre)acteur), feve, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, demande), cryptogramme, false);
					}
                }
            }
        }


		//@author Florian Malveau
		//A partir des données des contrats on estime coûts fèves
		for(IProduit feve : coutMoyFeves.keySet()){
			double quantityXprix = 0.0; //numérateur pour moyenne
			double quantitytotale = 0.0; //dénominateur moyenne
			for (ExemplaireContratCadre contrat : this.ContratsAcheteur){
				if(contrat.getProduit() == feve){
					quantityXprix += contrat.getQuantiteTotale()*contrat.getPrix();
					quantitytotale += contrat.getQuantiteTotale();
					journalCC.ajouter(feve+"("+contrat.getNumero()+") : "+contrat.getQuantiteALivrerAuStep());
				}

			this.coutMoyFeves.replace(feve, quantityXprix/quantitytotale);
			}
		}

		//Statistique sur les achats de fèves
		jdb.ajouter("");
		jdb.ajouter("########################################");
		jdb.ajouter("-- COUT ESTIME --");
		for(IProduit feve : coutMoyFeves.keySet()){
			jdb.ajouter("- "+feve+" : "+coutMoyFeves.get(feve));
		}
	}

	//@author Henri Roth
	public void receptionner(IProduit produit, double quantiteEnTonnes, ExemplaireContratCadre contrat) {
		journalCC.ajouter("Reception de "+quantiteEnTonnes+" de T de " + produit + " en provenance du contrat "+contrat.getNumero());
		super.stockFeves.addToStock(produit, quantiteEnTonnes);
	}
	//@author Henri Roth
	public boolean achete(IProduit produit) {
		//on vérifie qu'il s'agit bien de la fève que l'on achète
		if(produit == Feve.F_BQ
		|| produit == Feve.F_BQ_E
		|| produit == Feve.F_MQ
		|| produit == Feve.F_HQ_E){
			//on vérifie que l'on en veut bien
				return true; 
		}
		return false;
	}
	//@author Henri Roth
	public String toString() {
		return this.getNom();
	}
	//@author Henri Roth
	public int fixerPourcentageRSE(IAcheteurContratCadre acheteur, IVendeurContratCadre vendeur, IProduit produit,
			Echeancier echeancier, long cryptogramme, boolean tg) {
		return 5;
	}

	@Override //@author Henri Roth
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
	journalCC.ajouter("Nouveau contrat cadre Acheteur : " +contrat);
	journalCC.ajouter("Prix (€/t) : " +contrat.getPrix());
	// Trie des contrats cadres en fonction du produit
	if(super.lesFeves.contains(contrat.getProduit())) {
		super.ContratsAcheteur.add(contrat);
	} else if(super.lesChocolats.contains(contrat.getProduit())) {
		super.ContratsVendeur.add(contrat);
	} else {
		jdb.ajouter("#### ATTENTION : ON ACHETE N'IMPORTE QUOI ####");
	}

	//ContratsAcheteur.add(contrat);
	}
}
