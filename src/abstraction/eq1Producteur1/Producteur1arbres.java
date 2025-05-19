package abstraction.eq1Producteur1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;

public class Producteur1arbres extends Producteur1Bourse {

    private int nb_arbres_total;
    private int nombre_arbes;
    private int temps_avant_production;
    private int duree_de_vie = 960;
    private int besoin_main_doeuvre;
    private int production_par_arbre;
    private int nb_feves_par_cabosse;
    private double poids_feve_par_cabosse_apres_sechage;
    private int temps_de_pousse;
    private int temps_de_sechage;
    private double prix_achat;
    private int prix_replantation;
    private int nombre_hectares;
    private Journal journal;
    private Feve typeFeve;

    public Producteur1arbres(Feve typeFeve) {
        this.typeFeve = typeFeve;
        this.journal = new Journal(getNom() + " - Journal arbres", this);
        initialiserParametres();
        this.nb_arbres_total = nombre_arbes;
    }

    private void initialiserParametres() {
        switch (typeFeve) {
            case F_BQ:
                this.nombre_arbes = 950;
                this.temps_avant_production = 72;
                this.besoin_main_doeuvre = 8;
                this.production_par_arbre = 32;
                this.nb_feves_par_cabosse = 23 + Filiere.random.nextInt(5);
                this.poids_feve_par_cabosse_apres_sechage = 0.753;
                this.temps_de_pousse = 12;
                this.temps_de_sechage = 2;
                this.prix_achat = 2250;
                this.prix_replantation = 800;
                this.nombre_hectares = 840;
                break;
            case F_MQ:
                this.nombre_arbes = 750;
                this.temps_avant_production = 96;
                this.besoin_main_doeuvre = 6;
                this.production_par_arbre = 30;
                this.nb_feves_par_cabosse = 25 + Filiere.random.nextInt(4);
                this.poids_feve_par_cabosse_apres_sechage = 0.750;
                this.temps_de_pousse = 11;
                this.temps_de_sechage = 1;
                this.prix_achat = 4250;
                this.prix_replantation = 1400;
                this.nombre_hectares = 120000;
                break;
            case F_HQ_E:
                this.nombre_arbes = 500;
                this.temps_avant_production = 120;
                this.besoin_main_doeuvre = 4;
                this.production_par_arbre = 20;
                this.nb_feves_par_cabosse = 29 + Filiere.random.nextInt(2);
                this.poids_feve_par_cabosse_apres_sechage = 0.765;
                this.temps_de_pousse = 12;
                this.temps_de_sechage = 1;
                this.prix_achat = 7000;
                this.prix_replantation = 2350;
                this.nombre_hectares = 0;
                break;
            default:
                throw new IllegalArgumentException("Type de fève inconnu : " + typeFeve);
        }
    }

    public double getNombreFevesTotal() {
        return this.nombre_arbes * this.production_par_arbre * this.nb_feves_par_cabosse * this.nombre_hectares;
    }

    public HashMap<Feve, Integer> getNombreArbres() {
        HashMap<Feve, Integer> arbre = new HashMap<>();
        arbre.put(this.typeFeve, this.nombre_arbes);
        return arbre;
    }

    public void miseAJourArbres() {
        // À implémenter si besoin de vieillissement ou mortalité des arbres
    }

    @Override
    public List<Journal> getJournaux() {
        List<Journal> res = super.getJournaux();
        res.add(journal);
        return res;
    }
}