package abstraction.eq7Distributeur1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariablePrivee;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.IProduit;

public class Distributeur1Acteur implements IActeur {
	
	protected int cryptogramme;
	protected Map<ChocolatDeMarque, Variable> stocksChocolats;
    protected List<ChocolatDeMarque> chocolats;

	protected Variable stock_C_BQ;
	protected Variable stock_C_BQ_E;
	protected Variable stock_C_MQ;
	protected Variable stock_C_MQ_E;
	protected Variable stock_C_HQ_E;
	protected Variable stock_C_HQ_BE;

	public Distributeur1Acteur() {
		this.stock_C_BQ=new VariablePrivee("EQ7-D1 Stock C_BQ", "<html>Quantite totale de C_BQ en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_BQ_E=new VariablePrivee("EQ7-D1 Stock C_BQ_E", "<html>Quantite totale de C_BQ_E en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_MQ=new VariablePrivee("EQ7-D1 Stock C_MQ", "<html>Quantite totale de C_MQ en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_MQ_E=new VariablePrivee("EQ7-D1 Stock C_MQ_E", "<html>Quantite totale de C_MQ_E en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_HQ_E=new VariablePrivee("EQ7-D1 Stock C_HQ_E", "<html>Quantite totale de C_HQ_E en stock</html>",this, 0.0, 1000000.0, 0.0);
		this.stock_C_HQ_BE=new VariablePrivee("EQ7-D1 Stock C_HQ_BE", "<html>Quantite totale de C_HQ_BE en stock</html>",this, 0.0, 1000000.0, 0.0);
	}
	
	public void initialiser() {
	}

	public String getNom() {// NE PAS MODIFIER
		return "EQ7";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}

	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////
	@Override
	public void next() {
		//par Ethan
		for (int i = 0; i < this.chocolats.size(); i++) {
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("BQ_E")) {
				this.stock_C_BQ_E.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("BQ") && (!stocksChocolats.get(chocolats.get(i)).getNom().contains("BQ_E"))) {
				this.stock_C_BQ.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("MQ_E")) {
				this.stock_C_MQ_E.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("MQ") && (!stocksChocolats.get(chocolats.get(i)).getNom().contains("MQ_E"))) {
				this.stock_C_MQ.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("HQ_E")) {
				this.stock_C_HQ_E.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
			if (stocksChocolats.get(chocolats.get(i)).getNom().contains("HQ_BE")) {
				this.stock_C_HQ_BE.ajouter(this, stocksChocolats.get(chocolats.get(i)).getValeur(), cryptogramme);
			}
		}
	}

	public Color getColor() {// NE PAS MODIFIER
		return new Color(162, 207, 238); 
	}

	public String getDescription() {
		return "Nous sommes HexaFridge, nous sommes unis et indivisible, nous sommes la source de toute consommation";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		res.add(this.stock_C_BQ);
		res.add(this.stock_C_BQ_E);
		res.add(this.stock_C_MQ);
		res.add(this.stock_C_MQ_E);
		res.add(this.stock_C_HQ_E);
		res.add(this.stock_C_HQ_BE);
		return res;
	}

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res = new ArrayList<Variable>();
		return res;
	}

	// Renvoie les journaux
	public List<Journal> getJournaux() {
		List<Journal> res=new ArrayList<Journal>();
		return res;
	}

	////////////////////////////////////////////////////////
	//               En lien avec la Banque               //
	////////////////////////////////////////////////////////

	// Appelee en debut de simulation pour vous communiquer 
	// votre cryptogramme personnel, indispensable pour les
	// transactions.
	public void setCryptogramme(Integer crypto) {
		this.cryptogramme = crypto;
	}

	// Appelee lorsqu'un acteur fait faillite (potentiellement vous)
	// afin de vous en informer.
	public void notificationFaillite(IActeur acteur) {
	}

	// Apres chaque operation sur votre compte bancaire, cette
	// operation est appelee pour vous en informer
	public void notificationOperationBancaire(double montant) {
	}
	
	// Renvoie le solde actuel de l'acteur
	protected double getSolde() {
		return Filiere.LA_FILIERE.getBanque().getSolde(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme);
	}

	////////////////////////////////////////////////////////
	//        Pour la creation de filieres de test        //
	////////////////////////////////////////////////////////

	// Renvoie la liste des filieres proposees par l'acteur
	public List<String> getNomsFilieresProposees() {
		ArrayList<String> filieres = new ArrayList<String>();
		return(filieres);
	}

	// Renvoie une instance d'une filiere d'apres son nom
	public Filiere getFiliere(String nom) {
		return Filiere.LA_FILIERE;
	}

	public double getQuantiteEnStock(IProduit p, int cryptogramme) {
		if (this.cryptogramme==cryptogramme) { // c'est donc bien un acteur assermente qui demande a consulter la quantite en stock
			return 0; // A modifier
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}
}
