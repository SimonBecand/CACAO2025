package abstraction.eq1Producteur1;

import abstraction.eqXRomu.general.Journal;

public class Producteur1Couts {

    private Journal journal;
    private Producteur1 producteur1; 
    private Employes employes; 

    public Producteur1Couts(Producteur1 producteur1, Employes employes) {
        this.producteur1 = producteur1;
        this.employes = employes;
        this.journal = new Journal("Journal Coûts", producteur1);
    }

    // Coût total d'entretien lié aux employés
    public double calculerCoutEmployes() {
        return employes.calculerCoutEntretien();
    }

    // Enregistrement les coûts
    public void next() {
        double cout = calculerCoutEmployes();
        journal.ajouter("Coût total des employés ce step : " + cout);
    }

}

