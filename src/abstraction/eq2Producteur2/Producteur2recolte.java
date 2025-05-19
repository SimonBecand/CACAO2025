//Maxime Philippon
package abstraction.eq2Producteur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;

public class Producteur2recolte extends Producteur2Acteur {

    protected List<Plantation> plantations;
    protected HashMap<Feve, Double> feve_recolte;
    protected HashMap<Feve, Double> cout_recolte;
    private Journal Journalterrains;
    private Journal JournalRecolte;

    public Producteur2recolte() {
        super();
        this.plantations = new ArrayList<>();
        this.feve_recolte = new HashMap<Feve, Double>();
        this.cout_recolte = new HashMap<Feve, Double>();
        this.JournalRecolte = new Journal("Journal Recolte Eq2",this);
        this.Journalterrains = new Journal("Journal Terrains Eq2",this);

        for (Feve f : Feve.values()) {
            this.feve_recolte.put(f, 0.0);
            this.cout_recolte.put(f, 0.0);
        }
    }

    /**
     * Ajoute une plantation à la liste.
     */
    public void ajouterPlantation(Plantation p) {
        plantations.add(p);
    }

    /**
     * Produit des fèves à partir de toutes les plantations.
     */
    public void recolteParStep() {
        double Prod_BQ = 0;
        double Prod_BQ_E = 0;
        double Prod_MQ = 0;
        double Prod_MQ_E = 0;
        double Prod_HQ_E = 0;
        double Prod_HQ_BE = 0;
        double cout_BQ = 0;
        double cout_BQ_E = 0;
        double cout_MQ = 0;
        double cout_MQ_E = 0;
        double cout_HQ_E = 0;
        double cout_HQ_BE = 0;
        for (Plantation p : plantations) {
            switch (p.getTypeFeve()) {
                case F_BQ:
                    Prod_BQ += p.prodPlantation();
                    cout_BQ += p.getcout();
                    break;
                case F_BQ_E:
                    Prod_BQ_E += p.prodPlantation();
                    cout_BQ_E += p.getcout_amorti();
                    break;
                case F_MQ:
                    Prod_MQ += p.prodPlantation();
                    cout_MQ += p.getcout();
                    break;
                case F_MQ_E:
                    Prod_MQ_E += p.prodPlantation();
                    cout_MQ_E += p.getcout_amorti();
                    break;
                case F_HQ_E:
                    Prod_HQ_E += p.prodPlantation();
                    cout_HQ_E += p.getcout_amorti();
                    break;
                case F_HQ_BE:
                    Prod_HQ_BE += p.prodPlantation();
                    cout_HQ_BE += p.getcout_amorti();
                    break;
                default:
                    throw new IllegalArgumentException("Type de fève non reconnu !");
            }
        
        }
        this.feve_recolte.put(Feve.F_BQ,Prod_BQ);
        this.feve_recolte.put(Feve.F_BQ_E,Prod_BQ_E);
        this.feve_recolte.put(Feve.F_MQ,Prod_MQ);
        this.feve_recolte.put(Feve.F_MQ_E,Prod_MQ_E);
        this.feve_recolte.put(Feve.F_HQ_E,Prod_HQ_E);
        this.feve_recolte.put(Feve.F_HQ_BE,Prod_HQ_BE);
        this.cout_recolte.put(Feve.F_BQ,cout_BQ);
        this.cout_recolte.put(Feve.F_BQ_E,cout_BQ_E);
        this.cout_recolte.put(Feve.F_MQ,cout_MQ);
        this.cout_recolte.put(Feve.F_MQ_E,cout_MQ_E);
        this.cout_recolte.put(Feve.F_HQ_E,cout_HQ_E);
        this.cout_recolte.put(Feve.F_HQ_BE,cout_HQ_BE);
        JournalRecolte.ajouter(Filiere.LA_FILIERE.getEtape()+" : Recolte de "+Prod_BQ+" feves de BQ, "+Prod_BQ_E+" feves de BQ_E, "+Prod_MQ+" feves de MQ, "+Prod_MQ_E+" feves de HQ_E, "+Prod_HQ_E+" feves de HQ_E et "+Prod_HQ_BE+" feves de HQ_BE");
    }
    
    public void cout_plantations() {
        double cout = 0;
        for (Plantation p : plantations) {
            cout += p.getcout();
        }
        Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Cout lié aux plantations (main d'oeuvre, achat, replantation) ", cout);
        JournalBanque.ajouter(Filiere.LA_FILIERE.getEtape()+" : Cout total lié aux plantations : "+cout);
    }

    public boolean seuil_replante(Feve f) {
        double stock_f = stockvar.get(f).getValeur();
        double prod_f = fevesSeches.get(f);

        if (stock_f <= 2 * prod_f) {
            return true;
        } else {
            return false;
        }
    }

    public void action_replante() {
        for (Plantation p : plantations) {
            if (p.estMorte() && seuil_replante(p.getTypeFeve())) {
                p.Replante();
                Journalterrains.ajouter(Filiere.LA_FILIERE.getEtape()+" : Replantation de "+p.getParcelles()+" parcelles de "+p.getTypeFeve());
            }
            else if (p.estMorte() && !seuil_replante(p.getTypeFeve())) {
                //Journalterrains.ajouter(Filiere.LA_FILIERE.getEtape()+" : Pas de replantation de "+p.getTypeFeve()+" car le stock est trop important");
            }
            else{}
        }
    }
    /**
     * Retourne le nombre de parcelles par type de feve.
     */
    public void get_nb_plantations() {
        double nb_BQ = 0;
        double nb_BQ_E = 0;
        double nb_MQ = 0;
        double nb_MQ_E = 0;
        double nb_HQ_E = 0;
        double nb_HQ_BE = 0;
        for (Plantation p : plantations) {
            if (p.estMorte() == false) {
                switch (p.getTypeFeve()) {
                    case F_BQ:
                        nb_BQ+=p.getParcelles();
                        break;
                    case F_BQ_E:
                        nb_BQ_E+=p.getParcelles();
                        break;
                    case F_MQ:
                        nb_MQ+=p.getParcelles();
                        break;
                    case F_MQ_E:
                        nb_MQ_E+=p.getParcelles();
                        break;
                    case F_HQ_E:
                        nb_HQ_E+=p.getParcelles();
                        break;
                    case F_HQ_BE:
                        nb_HQ_BE+=p.getParcelles();
                        break;
                    default:
                        throw new IllegalArgumentException("Type de fève non reconnu !");
                }
            }
        }
        Journalterrains.ajouter(Filiere.LA_FILIERE.getEtape()+" Nombre de parcelles avec arbres : "+nb_BQ+" BQ, "+nb_BQ_E+" BQ_E, "+nb_MQ+" MQ, "+nb_MQ_E+" MQ_E, "+nb_HQ_E+" HQ_E et "+nb_HQ_BE+" HQ_BE");
    }

    public void initialiser() {
        super.initialiser();
        //Ajoute les parcelles de depart
        for (int i = 1; i < 40; i++) {
            int age_init = 1 + (i * 24);
            ajouterPlantation(new Plantation(Feve.F_BQ,257,age_init));
            ajouterPlantation(new Plantation(Feve.F_BQ_E,24,age_init));
            ajouterPlantation(new Plantation(Feve.F_MQ,94,age_init));
            ajouterPlantation(new Plantation(Feve.F_MQ_E,24,age_init));
            ajouterPlantation(new Plantation(Feve.F_HQ_BE,24,age_init));
        }
    }

    public void next() {
        action_replante();
        recolteParStep();
        cout_plantations();
        get_nb_plantations();
        for (Plantation p : plantations) {
            p.add_age();
        }
        super.next();
    }

    public List<Journal> getJournaux() {
		List<Journal> res = super.getJournaux();
		res.add(JournalRecolte);
        res.add(Journalterrains);
        res.add(JournalBanque);
		return res;
	}
}