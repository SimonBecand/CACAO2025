//BRUN Thomas
package abstraction.eq2Producteur2;

import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.Gamme;
import abstraction.eqXRomu.produits.IProduit;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.LinkedList;
import java.awt.Color;



public class Producteur2VenteCC extends Producteur2couts implements IVendeurBourse, IVendeurContratCadre{
	
    private SuperviseurVentesContratCadre supCC;
	private List<ExemplaireContratCadre> contratsEnCours;
	private List<ExemplaireContratCadre> contratsTermines;
	private Journal JournalEQ2CC;
   
		private int PRIX_DEFAUT = 5000; // Default price initialized to 5000 (example value)
	   
		public Producteur2VenteCC() {
        super();
        this.contratsEnCours=new LinkedList<ExemplaireContratCadre>();
		this.contratsTermines=new LinkedList<ExemplaireContratCadre>();
		this.JournalEQ2CC = new Journal("JournalCCEq2",this);
		//System.out.println("Producteur2VenteCC initialisé !");

    }
    // CONTRAT CADRE //

	public void initialiser() {
		super.initialiser();
		this.supCC = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
		this.JournalEQ2CC.ajouter("Journal initialisé pour Producteur2VenteCC");
		//System.out.println("Méthode initialiser appelée pour Producteur2VenteCC");
	}
   

    
	public void next() {
		super.next();
		JournalEQ2CC.ajouter("=== STEP "+Filiere.LA_FILIERE.getEtape()+" ====================");
		for (Feve f : stock.keySet()) { // pas forcement equitable : on avise si on lance un contrat cadre pour tout type de feve
			if (stockvar.get(f).getValeur()-restantDu(f)>1200) { // au moins 100 tonnes par step pendant 6 mois
				this.JournalEQ2CC.ajouter("   "+f+" suffisamment en stock pour passer un CC");
				double parStep = Math.max(100, (stockvar.get(f).getValeur()-restantDu(f))/24); // au moins 100, et pas plus que la moitie de nos possibilites divisees par 2
				// Amélioration possible mais il faut définir une variable aléatoire gobale pour bien identifier l'échéancier Echeancier e = new Echeancier(Filiere.LA_FILIERE.getEtape()+1, ThreadLocalRandom.current().nextInt(12, 25), parStep); //CC d'une durée aléatoire entre 12 et 24 steps
				Echeancier e = new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 12, parStep); //CC d'une durée aléatoire de 12 steps
				List<IAcheteurContratCadre> acheteurs = supCC.getAcheteurs(f);
				if (acheteurs.size()>0) {
					IAcheteurContratCadre acheteur = acheteurs.get(Filiere.random.nextInt(acheteurs.size()));
					JournalEQ2CC.ajouter("   "+acheteur.getNom()+" retenu comme acheteur parmi "+acheteurs.size()+" acheteurs potentiels");
					ExemplaireContratCadre contrat = supCC.demandeVendeur(acheteur, this, f, e, cryptogramme, false);
					if (contrat==null) {
						JournalEQ2CC.ajouter(Color.RED, Color.white,"   echec des negociations");
					} else {
						this.contratsEnCours.add(contrat);
						JournalEQ2CC.ajouter(Color.GREEN, acheteur.getColor(), "   contrat signe");
					}
				} else {
					JournalEQ2CC.ajouter("pas d'acheteur");
				}}
			else {
				JournalEQ2CC.ajouter(Color.RED, Color.white,"   Pas assez de stock de " +f+ " pour passer un CC");
			}
		}
		// On archive les contrats termines
		/*
		for (ExemplaireContratCadre c : this.contratsEnCours) {
			if (c.getQuantiteRestantALivrer()==0.0) {
				this.contratsTermines.add(c);
			}
		}
		for (ExemplaireContratCadre c : this.contratsTermines) {
			this.contratsEnCours.remove(c);
		}
		this.JournalEQ2CC.ajouter("---------------------------------");
		for (ExemplaireContratCadre cc: this.contratsEnCours) {
			this.JournalEQ2CC.ajouter(cc.toString());
		}
		for (Feve f : stock.keySet()) { // pas forcement equitable : on avise si on lance un contrat cadre pour tout type de feve
			this.JournalEQ2CC.ajouter("Feve "+f+" en stock="+stockvar.get(f).getValeur()+" restant du="+restantDu(f));
		}
		this.JournalEQ2CC.ajouter("=================================");
		*/
	}


    public double restantDu(Feve f) { //Calcule le reste à livrer pour une fève donnée
		double res=0;
		//JournalEQ2CC.ajouter("RESTANT DU "+f+" ----");
		for (ExemplaireContratCadre c : this.contratsEnCours) {
			if (c.getProduit().equals(f)) {
				res+=c.getQuantiteRestantALivrer();
			}
			//this.JournalEQ2CC.ajouter("contrat "+c.getNumero()+" feve "+c.getProduit()+" vs "+f+" RD="+res);
		}
		return res;
	}

    public double prix(Feve f) {
		double res=0;
		List<Double> lesPrix = new LinkedList<Double>();
		for (ExemplaireContratCadre c : this.contratsEnCours) {
			if (c.getProduit().equals(f)) {
				lesPrix.add(c.getPrix()); // la variable lesPrix contient le prix de la fève du contrat en cours
			}
		}
		for (ExemplaireContratCadre c : this.contratsTermines) {
			if (c.getProduit().equals(f)) {
				lesPrix.add(c.getPrix()); // la variable lesPrix contient le pri de la fève du contrat terminé
			}
		}
		if (lesPrix.size()>0) {
			double somme=0;
			for (Double d : lesPrix) {
				somme+=d;
			}
			res=somme/lesPrix.size(); //effectue la moyenne du prix de la fève sur les contrats en cours et terminés
		}
		return res;
	}

    public List<Journal> getJournaux() { //Mets à jour les journaux
		List<Journal> res = super.getJournaux();
		res.add(JournalEQ2CC);
		return res;
	}

    public boolean vend(IProduit produit) {
		return produit.getType().equals("Feve") && stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit)>1200;
	}



    
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
		JournalEQ2CC.ajouter(" On effectue une contreProposition("+contrat.getProduit()+" avec echeancier "+contrat.getEcheancier());
		Echeancier ec = contrat.getEcheancier();
		IProduit produit = contrat.getProduit();
		Echeancier res = ec;
		JournalEQ2CC.ajouter("Echeancier contrat #"+contrat.getNumero()+" volume total="+ec.getQuantiteTotale()+" stock="+stockvar.get((Feve)produit).getValeur()+" restantdu="+restantDu((Feve)produit));
		boolean acceptable = produit.getType().equals("Feve")
				&& ec.getQuantiteTotale()>=1200  // au moins 100 tonnes par step pendant 6 mois
				&& ec.getStepFin()-ec.getStepDebut()>=11   // duree totale d'au moins 12 etapes
				&& ec.getStepDebut()<Filiere.LA_FILIERE.getEtape()+8 // ca doit demarrer dans moins de 4 mois
				&& ec.getQuantiteTotale()<stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit);
				JournalEQ2CC.ajouter("Echeancier contrat # acceptable ? "+acceptable);
				
				if (!acceptable) {
					if (!produit.getType().equals("Feve") || stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit)<1200) {
						if (!produit.getType().equals("Feve")) {
							JournalEQ2CC.ajouter("      ce n'est pas une feve : je retourne null");
						} else {
							JournalEQ2CC.ajouter("      je n'ai que "+(stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit))+" de disponible (moins de 1200) : je retourne null");
						}
						return null;
					}
					if (ec.getQuantiteTotale()<=stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit)) {
						JournalEQ2CC.ajouter(" On propose "+new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 12,  (int)(ec.getQuantiteTotale()/12)));
						return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 12,  (int)(ec.getQuantiteTotale()/12));
					} else {
						JournalEQ2CC.ajouter(" On propose "+new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 12,  (int)(((stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit))/12))));
						res = new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 12,  (int)(((stockvar.get((Feve)produit).getValeur()-restantDu((Feve)produit))/12)));
						return res.getQuantiteTotale()>=1200 ? res : null;
					}
				}
				JournalEQ2CC.ajouter(" On accepte l'echeancier");
				return res;
	}

	public double propositionPrix(ExemplaireContratCadre contrat) {
		if (!contrat.getProduit().getType().equals("Feve")) {
			return 0; // ne peut pas etre le cas normalement 
		}
		BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
		double cours = ((Feve)(contrat.getProduit())).isEquitable() ? 0.0 : bourse.getCours((Feve)contrat.getProduit()).getValeur();
		double prixCC = cout_unit_t.get(contrat.getProduit());
		if (prixCC==0.0) {
			PRIX_DEFAUT=(int)(PRIX_DEFAUT*0.98); // on enleve 2% tant qu'on n'a pas passe un contrat
		}
		JournalEQ2CC.ajouter("Le cours de la feve est de "+cours);
		double res = prixCC>cours ? prixCC*1.2 : (int)(cours*1.5);
		JournalEQ2CC.ajouter("La PropositionPrix de la première négociation est "+res);
		return res;
	}

	public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
		JournalEQ2CC.ajouter("      contreProposition a partir de : "+contrat.getPrix());
		if (!contrat.getProduit().getType().equals("Feve")) {
			return 0; // ne peut pas etre le cas normalement 
		}
		List<Double> prix = contrat.getListePrix();
		if (prix.get(prix.size()-1)>=0.995*prix.get(0))// Vérifie si le dernier prix de la liste est supérieur ou égal à 99,5 % du premier prix
															// Cela permet de s'assurer que la variation du prix reste faible (moins de 0,5 % de baisse) 
															{
			JournalEQ2CC.ajouter("2      contrePropose le prix demande : "+contrat.getPrix());
			return contrat.getPrix();
		} else {
			int percent = (int)(100* Math.pow((contrat.getPrix()/prix.get(0)), prix.size()/2));
			int alea = Filiere.random.nextInt(100);
			JournalEQ2CC.ajouter(" le pourcentage de négo du contrat: "+percent);
			if (alea< percent) { // d'autant moins de chance d'accepter que le prix est loin de ce qu'on proposait
				if (Filiere.random.nextInt(100)<5) { // 1 fois sur 20 on accepte
					JournalEQ2CC.ajouter("3      contrePropose le prix demande : "+contrat.getPrix());
					return contrat.getPrix();
				} else {
					double res = (prix.get(prix.size()-2)+contrat.getPrix())/2.0; // la mmoyenne des deux derniers prix
					JournalEQ2CC.ajouter("4      contreproposition à ("+contrat.getPrix()+") contre "+res);
					return res;
				}
			} else {
				JournalEQ2CC.ajouter("5      contreproposition("+contrat.getPrix()+") contre "+prix.get(0)*1.05);
				return prix.get(0)*1.05;
			}
		}
	}

	public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
		this.contratsEnCours.add(contrat);
	}

	public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
		double stockActuel = stockvar.get(produit).getValeur((Integer)cryptogramme);
		double aLivre = Math.min(quantite, stockActuel);
		//JournalEQ2CC.ajouter("   Livraison de "+aLivre+" T de "+produit+" sur "+quantite+" exigees pour contrat "+contrat.getNumero());
		JournalStock.ajouter("Vente lié aux contrats cadres : ");
		DeleteStock((Feve)produit, aLivre);
		return aLivre;
	}


	@Override
	public double offre(Feve f, double cours) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'offre'");
	}



	@Override
	public double notificationVente(Feve f, double quantiteEnT, double coursEnEuroParT) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'notificationVente'");
	}



	@Override
	public void notificationBlackList(int dureeEnStep) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'notificationBlackList'");
	}
    


}
