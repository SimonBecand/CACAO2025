//Equipe
package abstraction.eq2Producteur2;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Producteur2Acteur implements IActeur {
	
	protected HashMap<Feve,Double> prodParStep;
	protected HashMap<Feve,Variable> stock;
	protected HashMap<Feve,Variable> stockvar;
	protected HashMap<Feve,Double> fevesSeches;
	protected int cryptogramme;
	protected Variable stockTotal;
	private int numero = 0;
	protected Journal num = new Journal("Journal Eq2", this);
	protected Journal JournalBanque;

	public Producteur2Acteur() {

		this.JournalBanque = new Journal("Journal Banque Eq2", this);
		this.stockvar = new HashMap<Feve,Variable>();
		this.fevesSeches = new HashMap<Feve, Double>();
    }
	
	public void initialiser() {
		
	}

	public String getNom() {// NE PAS MODIFIER
		return "EQ2";
	}
	
	public String toString() {// NE PAS MODIFIER
		return this.getNom();
	}
	
	////////////////////////////////////////////////////////
	//         En lien avec l'interface graphique         //
	////////////////////////////////////////////////////////

	public void next() {
		
		num.ajouter("Numero : " + numero);
		numero++;
	
	}




	public Color getColor() {// NE PAS MODIFIER
		return new Color(244, 198, 156); 
	}

	public String getDescription() {
		return "Bla bla bla";
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		return res;
	}

	// Renvoie les parametres
	public List<Variable> getParametres() {
		List<Variable> res=new ArrayList<Variable>();
		return res;
	}

	// Renvoie les journaux
	//public List<Journal> getJournaux() {
	//	List<Journal> res=new ArrayList<Journal>();
	//	res.add(num);;
	//	return res;
	//}

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
		if (this.cryptogramme==cryptogramme && this.stock.keySet().contains(p)) { 
			return this.stock.get(p).getValeur((Integer)cryptogramme);
		} else {
			return 0; // Les acteurs non assermentes n'ont pas a connaitre notre stock
		}
	}

	public List<Journal> getJournaux() {
		List<Journal> res=new ArrayList<Journal>();
		res.add(num);
		return res;
	}
}
