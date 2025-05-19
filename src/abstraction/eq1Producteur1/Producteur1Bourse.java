package abstraction.eq1Producteur1;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.bourseCacao.BourseCacao;
import abstraction.eqXRomu.bourseCacao.IVendeurBourse;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;

// ADAM SEBIANE

public class Producteur1Bourse extends Producteur1ContratCadre implements IVendeurBourse {

    private Journal journalBourse;
    protected Feve typeFeve; // Type de fève géré par ce producteur


    public Producteur1Bourse() {
        this.journalBourse = new Journal(getNom() + " - Journal Bourse", this);
    }

    @Override
    public double offre(Feve typeFeve, double prixCourant) {
 
     BourseCacao bourse = (BourseCacao)(Filiere.LA_FILIERE.getActeur("BourseCacao"));
     double stockDisponible = stock.getStock(typeFeve);
     double prixFeve = bourse.getCours(typeFeve).getValeur();
     journalBourse.ajouter("Étape " + Filiere.LA_FILIERE.getEtape() +
         " : Cours de " + typeFeve + " = " + prixFeve + " €/tonne, prix proposé = " + prixCourant + " €/tonne.");
     
     if (stockDisponible <= 0) {
         journalBourse.ajouter("Étape " + Filiere.LA_FILIERE.getEtape() +
             " : Pas d'offre pour les fèves " + typeFeve + ", stock insuffisant.");
         return 0.0; // Pas d'offre si le stock est insuffisant
     }
     
     if (prixCourant < prixFeve * 0.8) { // Par exemple, si le prix proposé est inférieur à 80% du cours
         journalBourse.ajouter("Étape " + Filiere.LA_FILIERE.getEtape() +
             " : Pas d'offre pour les fèves " + typeFeve + ", prix proposé trop bas.");
         return 0.0;
     }
     
     double quantiteOfferte = Math.min(stockDisponible, 1000.0); // Limite à 1000 tonnes
     journalBourse.ajouter("Étape " + Filiere.LA_FILIERE.getEtape() +
         " : Offre bourse de " + quantiteOfferte + " tonnes de fèves " + typeFeve + ".");
     return quantiteOfferte;
    }


    
    @Override
    public double notificationVente(Feve typeFeve, double quantiteVendue, double prixVente) {
        double stockDispo = stock.getStockTotal();
        double quantiteLivree = Math.min(stockDispo, quantiteVendue);
        stock.retirer(typeFeve, quantiteLivree); // méthode ajoutée dans Stock.java
        journalBourse.ajouter("Étape " + Filiere.LA_FILIERE.getEtape() +
            " : VENTE bourse de " + quantiteLivree + " tonnes de " + typeFeve +
            " à " + prixVente + " €/tonne");
        return quantiteLivree;
    }

    @Override
    public void notificationBlackList(int dureeBlacklist) {
        journalBourse.ajouter("Blacklisté pour " + dureeBlacklist + " étapes.");
    }

    @Override
    public void next() {
        super.next();
    }

    @Override
    public List<Journal> getJournaux() {
        List<Journal> res = super.getJournaux();
        res.add(journalBourse);
        System.out.println("journal : "+stock.getJournal());
        res.add(stock.getJournal());
        return res;
    }
}
