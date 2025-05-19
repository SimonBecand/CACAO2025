// @author Florian Malveau
package abstraction.eq6Transformateur3;
import java.util.HashMap;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;

import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.filiere.IActeur;

public class Transformateur3Stock {
    //private int dureePeremption = 6*2; //6 mois avant péremption (12 steps)
    private double stockTotal;
    private List<IProduit> listProduitSorted;
    private HashMap<IProduit, Double> stockProduit; //
    private HashMap<IProduit, Variable> dicoIndicateur;//Assosciation indicateur et quantité produits
    private Journal journalStock;
    private IActeur monActeur;
    private String nomProduit;

    public Transformateur3Stock(Transformateur3Acteur acteur, Journal journal, String nomProduit, double initial_value, List<IProduit> listProduit, HashMap<IProduit, Variable> indicateurs){
        
        // Récupère les variables associés au stock
        this.journalStock = journal;
        this.monActeur = acteur;
        this.dicoIndicateur = indicateurs;
        this.nomProduit = nomProduit;
        
        // Initialise le stockProduit
        this.stockProduit = new HashMap<IProduit, Double>();
        
        // Initialise les quantitée pour chaque produit
        this.stockTotal = 0.0;

        //Trie des fèves par ordre décroissant de qualité
        Collections.sort(listProduit, new SortbyQuality());
        this.listProduitSorted = listProduit;
        
        for (IProduit prod : listProduit) {
			stockProduit.put(prod, initial_value);
            dicoIndicateur.get(prod).setValeur(monActeur, initial_value);
            this.stockTotal += initial_value;
		}

        journalStock.ajouter("Initialisation du stock de "+this.nomProduit);
    }

    public double getQuantityOf(IProduit prod){
        if(stockProduit.containsKey(prod)){
            return stockProduit.get(prod);
        }
        else{
            return 0.0;
        }
    }

    public boolean contains(IProduit prod){
        return stockProduit.containsKey(prod);
    }

    public double getStockTotal(){
        return this.stockTotal;
    }

    public void addToStock(IProduit prod, double quantity){//int Etape
        if(quantity>0.0){
            //Si le produit n'existe pas on l'ajoute
            if(!stockProduit.containsKey(prod)){
                journalStock.ajouter("### Ajout nouveau produit : "+prod.toString());
                this.stockProduit.put(prod, 0.0);
            }
            

            stockProduit.put(prod, stockProduit.get(prod)+quantity);
            this.stockTotal += quantity;
            
            //Mise à jour indicateur
            if(dicoIndicateur.containsKey(prod)){
                dicoIndicateur.get(prod).setValeur(monActeur, stockProduit.get(prod));
            }

        }
        else {
            this.journalStock.ajouter("ERROR_stock_add : ajout avec quantité négative ou nulle");
        }
    }

    public void remove(IProduit prod, double quantity){
        if(quantity>0.0){
            //Si la quantité demandée est supérieur à celle disponible en stock
            if(stockProduit.get(prod)-quantity < 0.0){
                this.journalStock.ajouter("ERROR_stock_rmv : stock négatif");
                //this.stockTotal -= stockProduit.get(prod); //Retire le stock restant
                //stockProduit.put(prod, stockProduit.get(prod)+quantity);
            }
            else{
                stockProduit.put(prod, stockProduit.get(prod)-quantity);
                this.stockTotal -= quantity;
            }
            
            //Mise à jour indicateur
            dicoIndicateur.get(prod).setValeur(monActeur, stockProduit.get(prod));

        }
        else {
            this.journalStock.ajouter("ERROR_stock_rmv : ajout avec quantité négative ou nulle");
        }
    }

    protected void display() {
        if (nomProduit=="fèves"){
            journalStock.ajouter("STOCK DE FEVES DE CACAO (en t)");
        }
        else{
            journalStock.ajouter("STOCK DE CHOCOLAT (en t)");
        }
		
		for (IProduit prod : this.listProduitSorted) {
			int nbspace = 20-prod.toString().length();
			String space = "";
			for(int i=0;i<nbspace;i++){
				space=space+".";
			}
			this.journalStock.ajouter(prod+space+" : "+Math.round(this.stockProduit.get(prod)));
		}
		journalStock.ajouter("");
	}

    protected List<IProduit> getListProduitSorted(){
        return this.listProduitSorted;
    }
}

class SortbyQuality implements Comparator<IProduit> 
{ 
    // Used for sorting in ascending order of 
    // roll number 
    public int compare(IProduit a, IProduit b) 
    { 
        if(a.greaterThan(b)){
            return -1;
        }else{
            return 1;
        }
    } 
} 
