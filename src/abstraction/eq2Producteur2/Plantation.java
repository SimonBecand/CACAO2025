//Maxime Philippon
package abstraction.eq2Producteur2;
import java.util.concurrent.ThreadLocalRandom;

import abstraction.eqXRomu.produits.Feve;



public class Plantation {
    
    private Feve typeFeve;          // Type de fèves cultivées
    private int parcelles;          // Nombre de parcelles de 100 ha
    private int age;                // Âge de la plantation en steps
    private final int dureeDeVie;   // Durée de vie maximale avant remplacement
    private final int tempsAvantProduction; // Temps nécessaire avant production
    private final int productionParParcelle; // Nombre de cabosses par parcelle a chaque step
    private double prix_achat; // Prix d'achat de la plantation
    private double prix_vente; // Prix de vente de la plantation
    private double prix_replantation; // Prix de replantation de la plantation
    private double salaire_employe; // Prix que coûtent les employés par step par parcelle
    private boolean replante = false;

    
    public Plantation(Feve typeFeve, int parcelles, int age) {
        super();
        this.typeFeve = typeFeve;
        this.parcelles = parcelles;
        this.age = age; // Plantation récente
        
        // Initialisation des paramètres selon la qualité des fèves
        switch (typeFeve) {
            case F_BQ:
                this.dureeDeVie = 960;  // 40 ans
                this.tempsAvantProduction = 72;  // 3 ans
                this.productionParParcelle = 126666; // 126666 cabosses par parcelle a chaque next
                this.prix_achat = 225000;
                this.prix_vente = 145000;
                this.prix_replantation = 80000;
                this.salaire_employe = 3400;
                break;
            case F_BQ_E:
                this.dureeDeVie = 960;  // 40 ans
                this.tempsAvantProduction = 72;  // 3 ans
                this.productionParParcelle = 126666; // 126666 cabosses par parcelle a chaque next
                this.prix_achat = 225000;
                this.prix_vente = 145000;
                this.prix_replantation = 80000;
                this.salaire_employe = 10000;
                break;
            case F_MQ:
                this.dureeDeVie = 960;
                this.tempsAvantProduction = 96; // 4 ans
                this.productionParParcelle = 93750;
                this.prix_achat = 425000;
                this.prix_vente = 285000;
                this.prix_replantation = 140000;
                this.salaire_employe = 2600;
                break;
            case F_MQ_E:
                this.dureeDeVie = 960;
                this.tempsAvantProduction = 96; // 4 ans
                this.productionParParcelle = 93750;
                this.prix_achat = 425000;
                this.prix_vente = 285000;
                this.prix_replantation = 140000;
                this.salaire_employe = 7500;
                break;
            case F_HQ_E:
                this.dureeDeVie = 960;
                this.tempsAvantProduction = 120; // 5 ans
                this.productionParParcelle = 41666;
                this.prix_achat = 700000;
                this.prix_vente = 465000;
                this.prix_replantation = 235000;
                this.salaire_employe = 5000;
                break;
            case F_HQ_BE:
                this.dureeDeVie = 960;
                this.tempsAvantProduction = 120; // 5 ans
                this.productionParParcelle = 29166;
                this.prix_achat = 700000;
                this.prix_vente = 465000;
                this.prix_replantation = 235000;
                this.salaire_employe = 5000;
                break;
            default:
                throw new IllegalArgumentException("Type de fève non reconnu !");
        }
    }

    /**
     * Avance l'âge de la plantation d'un step et renvoie la quantité de fèves produites.
     */
    public double prodPlantation() {
        if (age == 0) {
            return 0; // Plantation récente, pas encore en production
        }
        else if (age < tempsAvantProduction) {
            return 0; // La plantation n'est pas encore en production
        }
        if (age >= dureeDeVie) {
            return 0; // Plantation morte, nécessite un remplacement
        }

        // Calcul de la production en fèves sèches
        double fevesTotales = parcelles * productionParParcelle * getFevesParCabosse();

        return fevesTotales;
    }

    public void add_age() {
        age++;
    }
    /**
     * Retourne le nombre moyen de fèves par cabosse selon le type.
     */
    private int getFevesParCabosse() {
        switch (typeFeve) {
            case F_BQ:
                return ThreadLocalRandom.current().nextInt(23, 29); // Moyenne entre 23 et 28
            case F_BQ_E:
                return ThreadLocalRandom.current().nextInt(23, 29); // Moyenne entre 23 et 28
            case F_MQ:
                return ThreadLocalRandom.current().nextInt(25, 30); // Moyenne entre 25 et 29
            case F_MQ_E:
                return ThreadLocalRandom.current().nextInt(25, 30); // Moyenne entre 25 et 29
            case F_HQ_E:
                return ThreadLocalRandom.current().nextInt(29, 32); // Moyenne entre 29 et 31
            case F_HQ_BE:
                return ThreadLocalRandom.current().nextInt(29, 32); // Moyenne entre 29 et 31
            default:
                return 0;
        }
    }


    // Getters pour récupérer des informations sur la plantation
    public Feve getTypeFeve() {
        return typeFeve;
    }

    public int getParcelles() {
        return parcelles;
    }

    public int getAge() {
        return age;
    }

    public boolean estEnProduction() {
        return age >= tempsAvantProduction && age < dureeDeVie;
    }

    public boolean estMorte() {
        return age >= dureeDeVie;
    }

    public void Replante() {
        age = 0;
        replante = true;
    }

    public boolean getReplante() {
        return replante;
    }

    public double getprix_achat() {
        return prix_achat;
    }
    public double getprix_replantation() {
        return prix_replantation;
    }
    
    public double getcout() {
        if ((age == 0) && (replante == false)) {
            return parcelles*prix_achat;
        }
        else if ((age == 0) && (replante == true)) {
            return parcelles*prix_replantation;
        }
        else if (age <= dureeDeVie){
            return parcelles*salaire_employe;
        }
        else {
            return 0;
        }
    } 

    public double getcout_amorti() {
        if ((age == 0) && (replante == false)) {
            return parcelles*prix_achat / 960;
        }
        else if ((age == 0) && (replante == true)) {
            return parcelles*prix_replantation / 960;
        }
        else if ((age <= dureeDeVie) && (replante == false)){
            return parcelles*salaire_employe + (parcelles*prix_achat / 960);
        }
        else if ((age <= dureeDeVie) && (replante == true)){
            return parcelles*salaire_employe + (parcelles*prix_replantation / 960);
        }
        else {
            return 0;
        }
    }
    public double get_prix_vente() {
        return prix_vente;
    }
}