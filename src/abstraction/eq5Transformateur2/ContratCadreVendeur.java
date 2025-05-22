//Simon

package abstraction.eq5Transformateur2;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.IProduit;
import java.util.ArrayList;
import java.util.List;

public class ContratCadreVendeur extends DecisionsActeur implements IVendeurContratCadre{
	protected List<ExemplaireContratCadre> ContratsVendeur = new ArrayList<>(); // Liste des contrats cadres en cours
	protected List<IProduit> chocolatsProduits = new ArrayList<>(); // Liste des produits (chocolats) proposés

	public ContratCadreVendeur() {
		super();
		// Initialisation des chocolats produits (à adapter selon vos besoins)
		chocolatsProduits.add(Chocolat.C_HQ_BE);
		chocolatsProduits.add(Chocolat.C_HQ_E);
		chocolatsProduits.add(Chocolat.C_MQ);
		chocolatsProduits.add(Chocolat.C_MQ_E);
	}

    /**
	 * Methode appelee par le superviseur afin de savoir si l'acheteur
	 * est pret a faire un contrat cadre sur le produit indique.
	 * @param produit
	 * @return Retourne false si le vendeur ne souhaite pas etablir de contrat 
	 * a cette etape pour ce type de produit (retourne true si il est pret a
		for(IProduit cm : this.chocolatsProduits){
	 */

	 public void next(){
        super.next();
        //on regarde ce qu'on doit livrer au step suivant
		for(IProduit cm : this.chocolatsProduits){
			double livraison = 0;
			for (ExemplaireContratCadre contrat : ContratsVendeur){ 
			if (contrat.getProduit()==cm){
				livraison += contrat.getQuantiteALivrerAuStep();
			}
			}
		}
        SuperviseurVentesContratCadre CCvendeur = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("CCvendeur");
        for(IProduit cm : this.chocolatsProduits){
            for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
                if (acteur!=this && acteur instanceof IAcheteurContratCadre && ((IAcheteurContratCadre)acteur).achete(cm)) {
                    
                        double capacite_vente_max = 500;
                            CCvendeur.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) cm, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 20, capacite_vente_max), super.cryptogramme, true);
                            CCvendeur.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) cm, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 20, capacite_vente_max), super.cryptogramme, false);
							//this.journalContrat.ajouter("Demande de contrat cadre envoyee pour " + cm + " a " + acteur.getNom() + " avec une capacite de vente de " + capacite_vente_max);
							// On enregistre le contrat dans la liste des contrats en cours
							//ExemplaireContratCadre contrat = CCvendeur.getContratCadre((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) cm);
							//if (contrat != null) {
								//ContratsVendeur.add(contrat);
								//this.journalContrat .ajouter("Contrat cadre en cours pour " + cm + " avec " + acteur.getNom());
							//} else {
								//this.journalContrat.ajouter("Aucun contrat cadre en cours pour " + cm + " avec " + acteur.getNom());
							}
                    } 
                }
            }
        
    

	public boolean vend(IProduit produit){
            if (produit == Chocolat.C_HQ_BE){
				return true;
			}
			if (produit == Chocolat.C_HQ_E){
				return true;
			}
			if (produit == Chocolat.C_MQ){
				return true;
			}
			if (produit == Chocolat.C_MQ_E){
				return true;
			}
		return false;
        }

	/**
	 * Methode appelee par le SuperviseurVentesContratCadre lors de la phase de negociation
	 * sur l'echeancier afin de connaitre la contreproposition du vendeur. Le vendeur
	 * peut connaitre les precedentes propositions d'echeanciers via un appel a la methode
	 * getEcheanciers() sur le contrat. Un appel a getEcheancier() sur le contrat retourne 
	 * le dernier echeancier que l'acheteur a propose.
	 * @param contrat
	 * @return Retourne null si le vendeur souhaite mettre fin aux negociations en renoncant a
	 * ce contrat. Retourne l'echeancier courant du contrat (contrat.getEcheancier()) si il est
	 * d'accord avec cet echeancier. Sinon, retourne un autre echeancier qui est une contreproposition.
	 */
	public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat){
        Echeancier e=contrat.getEcheancier();
		int nbrStep = e.getNbEcheances();
		int stepDebut = e.getStepDebut();
		double quantite = e.getQuantite(stepDebut); 

		return e;
    }
	
	/**
	 * Methode appele par le SuperviseurVentesContratCadre apres une negociation reussie
	 * sur l'echeancier afin de connaitre le prix a la tonne que le vendeur propose.
	 * @param contrat
	 * @return La proposition initale du prix a la tonne.
	 */
	public double propositionPrix(ExemplaireContratCadre contrat){
        Chocolat c = (Chocolat) contrat.getProduit();
		return 2000;
    }

	/**
	 * Methode appelee par le SuperviseurVentesContratCadre apres une contreproposition
	 * de prix different de la part de l'acheteur, afin de connaitre la contreproposition
	 * de prix du vendeur.
	 * @param contrat
	 * @return Retourne un nombre inferieur ou egal a 0.0 si le vendeur souhaite mettre fin
	 * aux negociation en renoncant a ce contrat. Retourne le prix actuel a la tonne du 
	 * contrat (contrat.getPrix()) si le vendeur est d'accord avec ce prix.
	 * Sinon, retourne une contreproposition de prix.
	 */
	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat){
        Chocolat c = (Chocolat) contrat.getProduit();
		double prixSouhaite = 2000;
		double prix = contrat.getPrix();
		if (prix >= 0.99*prixSouhaite){
			return prix;
		}
		return 0.99*prixSouhaite;
    }
	
	/**
	 * Methode appelee par le SuperviseurVentesContratCadre afin de notifier le
	 * vendeur de la reussite des negociations sur le contrat precise en parametre
	 * qui a ete initie par l'acheteur.
	 * Le superviseur veillera a l'application de ce contrat (des appels a livrer(...) 
	 * seront effectues lorsque le vendeur devra livrer afin d'honorer le contrat, et
	 * des transferts d'argent auront lieur lorsque l'acheteur paiera les echeances prevues)..
	 * @param contrat
	 */
	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat){
		this.journalContrat.ajouter("Nouveau contrat cadre de vente : "+contrat.toString());
    }

	/**
	 * Methode appelee par le SuperviseurVentesContratCadre lorsque le vendeur doit livrer 
	 * quantite tonnes de produit afin d'honorer le contrat precise en parametre. 
	 * @param produit
	 * @param quantite
	 * @param contrat
	 * @return Retourne la quantite livree. Une penalite est prevue si cette quantite
	 *  est inferieure a celle precisee en parametre
	 */
	
	public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat){
		this.retirerStock(this, produit, quantite, super.cryptogramme);
		return quantite;
	}

}