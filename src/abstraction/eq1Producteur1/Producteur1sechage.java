package abstraction.eq1Producteur1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;

public class Producteur1sechage extends Producteur1ContratCadre {

    private List<LotFeves> fevesEnSechage;
    private Journal journal;

    public Producteur1sechage() {
        this.fevesEnSechage = new ArrayList<>();
        this.journal = new Journal("Journal Séchage", this);
    }

    public void ajouterFevesEnSechage(Feve feve, double quantite, int dureeSechage) {
        fevesEnSechage.add(new LotFeves(feve, quantite, dureeSechage));
        journal.ajouter("Ajout de " + quantite + " fèves de type " + feve + " en séchage pour " + dureeSechage + " étapes");
    }

    public void miseAJourSechage(Stock stock) {
        Iterator<LotFeves> iter = fevesEnSechage.iterator();
        while (iter.hasNext()) {
            LotFeves lot = iter.next();
            lot.dureeRestante--;
            if (lot.dureeRestante <= 0) {
                stock.ajouter(lot.feve, lot.quantite);
                journal.ajouter("Fèves prêtes après séchage : " + lot.quantite + " de type " + lot.feve + " ajoutées au stock");
                iter.remove();
            }
        }
    }

    public List<Journal> getJournaux() {
        List<Journal> res = new ArrayList<>();
        res.add(journal);
        return res;
    }

    private static class LotFeves {
        Feve feve;
        double quantite;
        int dureeRestante;

        public LotFeves(Feve feve, double quantite, int dureeRestante) {
            this.feve = feve;
            this.quantite = quantite;
            this.dureeRestante = dureeRestante;
        }
    }
} 
