// @author  Florian Malveau

package abstraction.eq6Transformateur3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.produits.Feve;

public class Transformateur3StratQuantity extends Transformateur3Acteur {

    // Création des contrats acheteur et vendeur (pour les rendres accessibles dans la classe)
    protected List<ExemplaireContratCadre> ContratsAcheteur;
    protected List<ExemplaireContratCadre> ContratsVendeur;

    protected HashMap<IProduit, Double> coutMoyFeves; //estimation du cout de chaque fèves
    protected List<Long> contratTraite;
    protected HashMap<IProduit, Double> proportionFeves; //proportion de feves dans le chocolat

    //Quantitée de chaque type de fèves reçue au prochain step
    //pour chaque fève, in dispose d'un échéancier sur la quantité total de fèves
	protected HashMap<IProduit, List<Double>> quantityFevesEcheancier;
    // Quantitée de chaque type de choco vendu au prochain step
    protected HashMap<IProduit, List<Double>> quantityChocoEcheancier;
    protected HashMap<IProduit, List<Double>> besoinFeveEcheancier;

    public Transformateur3StratQuantity(){

        //Initialisation des listes de contrats acheteur et vendeur
        this.ContratsAcheteur=new LinkedList<ExemplaireContratCadre>();
        this.ContratsVendeur=new LinkedList<ExemplaireContratCadre>();

        //Initialisation des échanciers de fèves et chocolats
        this.quantityFevesEcheancier = new HashMap<IProduit, List<Double>>();
        this.quantityChocoEcheancier = new HashMap<IProduit, List<Double>>();
        this.besoinFeveEcheancier = new HashMap<IProduit, List<Double>>();
        this.contratTraite = new ArrayList<Long>();
    }

    public void initialiser() {
        super.initialiser();
        
        // Initialisation des échéanciers de fèves et chocolats
        for(IProduit feve : super.lesFeves){
            this.quantityFevesEcheancier.put(feve, new ArrayList<Double>());
            this.quantityFevesEcheancier.get(feve).add(0.0);
            this.quantityFevesEcheancier.get(feve).add(0.0);
            this.quantityFevesEcheancier.get(feve).add(0.0);
            this.quantityFevesEcheancier.get(feve).add(0.0);
        }

        for(IProduit feve : super.fevesUtiles){
            this.besoinFeveEcheancier.put(feve, new ArrayList<Double>());
            this.besoinFeveEcheancier.get(feve).add(0.0);
            this.besoinFeveEcheancier.get(feve).add(0.0);
            this.besoinFeveEcheancier.get(feve).add(0.0);
        }

        for(IProduit choco : super.lesChocolats){
            this.quantityChocoEcheancier.put(choco, new ArrayList<Double>());
            this.quantityChocoEcheancier.get(choco).add(0.0);
            this.quantityChocoEcheancier.get(choco).add(0.0);
            this.quantityChocoEcheancier.get(choco).add(0.0);
            this.quantityChocoEcheancier.get(choco).add(0.0);
        }
    }

    public void next(){
		super.next();
		super.jdb.ajouter("NEXT - STRATQUANTITY");
        super.journalStrat.ajouter("");
        super.journalStrat.ajouter("NEXT - STRATQUANTITY");

        miseAJourEcheanciers();

        // Traitement nouveaux contrats pour actualiser les échéanciers de fèves et chocolats
        this.quantityFevesEcheancier = traiterContrats(this.ContratsAcheteur, this.quantityFevesEcheancier);
        this.quantityChocoEcheancier = traiterContrats(this.ContratsVendeur, this.quantityChocoEcheancier);

        miseAJourEcheanciersBesoins();

        // Affichage des échéanciers de fèves et chocolats
        
        displayEcheancier("Echéancier de chocolats", this.quantityChocoEcheancier, super.lesChocolats);
        displayEcheancier("Echéancier besoin de fèves", this.besoinFeveEcheancier, super.fevesUtiles);
        displayEcheancier("Echéancier de fèves", this.quantityFevesEcheancier, super.fevesUtiles);
        }

    public void miseAJourEcheanciers(){
        //On supprime la ligne du next actuel
        for(IProduit feve : super.lesFeves){
            this.quantityFevesEcheancier.get(feve).remove(0);
            if(this.quantityFevesEcheancier.get(feve).size() <= 2){
                this.quantityFevesEcheancier.get(feve).add(0.0);
            }
        }
        for(IProduit choco : super.lesChocolats){
            this.quantityChocoEcheancier.get(choco).remove(0);
            if(this.quantityChocoEcheancier.get(choco).size() <= 2){
                this.quantityChocoEcheancier.get(choco).add(0.0);
            }
        }
    }

    public void miseAJourEcheanciersBesoins(){
        for(IProduit feve : super.fevesUtiles){
            double proportion = 0.0;
            IProduit choco = null;
            if(feve == Feve.F_BQ){
                proportion = 0.3;
                choco = super.lesChocolats.get(0);
            }else if(feve == Feve.F_BQ_E){
                proportion = 0.3;
                choco = super.lesChocolats.get(1);
            }else if(feve == Feve.F_MQ){
                proportion = 0.5;
                choco = super.lesChocolats.get(2);
            }else if(feve == Feve.F_HQ_E){
                proportion = 1.0;
                choco = super.lesChocolats.get(3);
            }
            for(int i=0; i<3; i++){
                double quantite = this.quantityChocoEcheancier.get(choco).get(i) * proportion;
                this.besoinFeveEcheancier.get(feve).set(i, quantite);
            }
        }
    }

    public HashMap<IProduit, List<Double>> traiterContrats(List<ExemplaireContratCadre> contratsList, HashMap<IProduit, List<Double>> EcheancierParProduit){
        
        int currentStep = Filiere.LA_FILIERE.getEtape(); // On récupère le step actuel

        for(ExemplaireContratCadre contrat : contratsList){
            // On traite le contrat s'il n'a pas déjà été traité
            if(!this.contratTraite.contains(contrat.getNumero())){

                this.contratTraite.add(contrat.getNumero());
                //super.journalStrat.ajouter("----- Traitement du contrat " + contrat.getNumero()+" -----");
                // On ajoute la quantité de fèves reçue au stock
                IProduit prod = contrat.getProduit();
                Echeancier echeancier = contrat.getEcheancier();
                int debutCC = echeancier.getStepDebut(); // On récupère le step de début de l'échéancier
                int t = debutCC-currentStep; // Translation à appliquer à l'échéancier pour le ramener au step actuel
                //super.journalStrat.ajouter("Produit : " + prod.toString());
                //super.journalStrat.ajouter("Echéancier : " + echeancier.toString());
                for (int i = t; i <= echeancier.getNbEcheances()+t; i++) {
                    // Si la liste d'échéance n'est pas assez grande, on l'agrandi
                    double quantite = echeancier.getQuantite(echeancier.getStepDebut()); // Quantité de fèves reçue
                    if(EcheancierParProduit.get(prod).size() <= i){
                        EcheancierParProduit.get(prod).add(quantite);
                    }else{
                        quantite += EcheancierParProduit.get(prod).get(i);
                        EcheancierParProduit.get(prod).set(i, quantite);
                    }
                }
            }
        }
        return EcheancierParProduit;
    }

    public void traiterContratStat(ExemplaireContratCadre contrat){
        
        int currentStep = Filiere.LA_FILIERE.getEtape(); // On récupère le step actuel
        HashMap<IProduit, List<Double>> EcheancierParProduit;

        // On traite le contrat s'il n'a pas déjà été traité
        if(!this.contratTraite.contains(contrat.getNumero())){
            IProduit prod = contrat.getProduit();

            if(super.lesFeves.contains(prod)){
                EcheancierParProduit = this.quantityFevesEcheancier;
            }else{
                EcheancierParProduit = this.quantityChocoEcheancier;
            }

            this.contratTraite.add(contrat.getNumero());
            //super.journalStrat.ajouter("----- Traitement du contrat " + contrat.getNumero()+" -----");
            // On ajoute la quantité de fèves reçue au stock
            
            Echeancier echeancier = contrat.getEcheancier();
            int debutCC = echeancier.getStepDebut(); // On récupère le step de début de l'échéancier
            int t = debutCC-currentStep; // Translation à appliquer à l'échéancier pour le ramener au step actuel
            //super.journalStrat.ajouter("Produit : " + prod.toString());
            //super.journalStrat.ajouter("Echéancier : " + echeancier.toString());
            for (int i = t; i <= echeancier.getNbEcheances()+t; i++) {
                // Si la liste d'échéance n'est pas assez grande, on l'agrandi
                double quantite = echeancier.getQuantite(echeancier.getStepDebut()); // Quantité de fèves reçue
                if(EcheancierParProduit.get(prod).size() <= i){
                    EcheancierParProduit.get(prod).add(quantite);
                }else{
                    quantite += EcheancierParProduit.get(prod).get(i);
                    EcheancierParProduit.get(prod).set(i, quantite);
                }
            }
        
            // On sauvegarde les infos
            if(super.lesFeves.contains(prod)){
                this.quantityFevesEcheancier = EcheancierParProduit;
            }else{
                this.quantityChocoEcheancier = EcheancierParProduit;
            }
        }
    }

    public void displayEcheancier(String title, HashMap<IProduit, List<Double>>Echeancier, List<IProduit> prodList){

        super.journalStrat.ajouter("");
        super.journalStrat.ajouter(title);
        super.journalStrat.ajouter(".................... | .Step +0. | .Step +1.   | .Step +2. |");
        for(IProduit prod : prodList){

            String prodName = miseEnForme(prod.toString(), 20, true);
            String str1 = miseEnForme(Journal.doubleSur(Echeancier.get(prod).get(0).intValue(),1),9, false);
            String str2 = miseEnForme(Journal.doubleSur(Echeancier.get(prod).get(1).intValue(),1),9, false);
            String str3 = miseEnForme(Journal.doubleSur(Echeancier.get(prod).get(2).intValue(),1),9, false);
			this.journalStrat.ajouter(prodName+" | "+str1+" | "+str2+" | "+str3+" |");
            //this.journalStrat.ajouter(Journal.doubleSur(123456789.124, 0));
        
        }
    }

    public String miseEnForme(String str, int size, Boolean left){
        int nbspace = size-str.length();
        String space = "";
        for(int i=0;i<nbspace;i++){
            space=space+".";
        }
        if(left){
            return str+space;
        }
        else{
            return space+str;
        }
    }

    //Estimation prix fèves

    //Estimation production

    //Estimation stock

}
   