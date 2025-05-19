package abstraction.eq1Producteur1;

import java.util.HashMap;
import java.util.Map;

import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Stock {

    private Map<Feve, Double> stocks; // Map pour gérer les stocks de fèves
    private Journal journalStock; // Journal pour enregistrer les opérations

    public Stock(IActeur a) {
        this.stocks = new HashMap<>();
        
        // Initialisation des stocks pour chaque type de fève
        this.stocks.put(Feve.F_BQ, 2 * 50000.0);
        this.stocks.put(Feve.F_MQ, 2 * 30000.0);
        this.stocks.put(Feve.F_HQ_E, 2 * 20000.0);

        this.journalStock = new Journal("Journal de Stock", a);
    }

    public Journal getJournal() {
        return journalStock;
    }

    // Ajouter une quantité pour une fève donnée
    public void ajouter(IProduit produit, double quantite) {
        if (produit instanceof Feve) {
            if (quantite < 0) {
                journalStock.ajouter("Erreur : Quantité négative pour " + produit);
                return;
            }
            Feve feve = (Feve) produit;
            double actuel = stocks.getOrDefault(feve, 0.0);
            stocks.put(feve, actuel + quantite);
            journalStock.ajouter("Ajout de " + quantite + " au stock de " + feve + " → Total : " + (actuel + quantite));
        }
    }

    // Retirer une quantité si possible
    public boolean retirer(IProduit produit, double quantite) {
        if (produit instanceof Feve) {
            Feve feve = (Feve) produit;
            double actuel = stocks.getOrDefault(feve, 0.0);
            if (quantite < 0) {
                journalStock.ajouter("Erreur : Retrait négatif pour " + produit);
                return false;
            } else if (actuel < quantite) {
                journalStock.ajouter("Erreur : Retrait trop grand pour " + produit);
                return false;
            } else {
                stocks.put(feve, actuel - quantite);
                journalStock.ajouter("Retrait de " + quantite + " du stock de " + feve + " → Reste : " + (actuel - quantite));
                return true;
            }
        }
        return false;
    }

    // Obtenir le stock d'une fève
    public double getStock(Feve feve) {
        return stocks.getOrDefault(feve, 0.0);
    }

    // Obtenir le stock total
    public double getStockTotal() {
        return stocks.values().stream().mapToDouble(Double::doubleValue).sum();
    }

    // Ajouter d’un coup plusieurs fèves (optionnel)
    public void ajouterStock(double quantiteFMQ, double quantiteFBQ, double quantiteFHQ) {
        ajouter(Feve.F_MQ, quantiteFMQ);
        ajouter(Feve.F_BQ, quantiteFBQ);
        ajouter(Feve.F_HQ_E, quantiteFHQ);
    }
}
