// @author  Florian Malveau

package abstraction.eq6Transformateur3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import abstraction.eqXRomu.produits.IProduit;
import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.general.Journal;

public class Transformateur3StratPrix extends Transformateur3AO {

    protected HashMap<IProduit, Double> coutMoyFeves; //estimation du cout de chaque fèves
    protected List<Long> contratTraite;

    // Quantitée de chaque type de fèves reçue au prochain step
    // pour chaque fève, on dispose d'un échéancier sur la quantité total de fèves
	//protected HashMap<IProduit, List<Double>> quantityFevesEcheancier;
    // Quantitée de chaque type de choco vendu au prochain step
    //protected HashMap<IProduit, List<Double>> quantityChocoEcheancier;

    public Transformateur3StratPrix(){
        //Initialisation des échanciers de fèves et chocolats
        super.quantityFevesEcheancier = new HashMap<IProduit, List<Double>>();
        super.quantityChocoEcheancier = new HashMap<IProduit, List<Double>>();
        this.contratTraite = new ArrayList<Long>();
    }

    public void initialiser() {
        super.initialiser();
        
        // Initialisation des échéanciers de fèves et chocolats
        for(IProduit feve : super.lesFeves){
            super.quantityFevesEcheancier.put(feve, new ArrayList<Double>());
            super.quantityFevesEcheancier.get(feve).add(0.0);
            super.quantityFevesEcheancier.get(feve).add(0.0);
            super.quantityFevesEcheancier.get(feve).add(0.0);
            super.quantityFevesEcheancier.get(feve).add(0.0);
        }
        for(IProduit choco : super.lesChocolats){
            super.quantityChocoEcheancier.put(choco, new ArrayList<Double>());
            super.quantityChocoEcheancier.get(choco).add(0.0);
            super.quantityChocoEcheancier.get(choco).add(0.0);
            super.quantityChocoEcheancier.get(choco).add(0.0);
            super.quantityChocoEcheancier.get(choco).add(0.0);
        }
    }

    public void next(){
		super.next();
		super.jdb.ajouter("NEXT - STRATPRIX");
        super.journalStrat.ajouter("NEXT - STRATPRIX");

        miseAJourEcheanciers();

        // Traitement nouveaux contrats pour actualiser les échéanciers de fèves et chocolats
        super.quantityFevesEcheancier = traiterContrat(super.ContratsAcheteur, super.quantityFevesEcheancier);
        super.quantityChocoEcheancier = traiterContrat(super.ContratsVendeur, super.quantityChocoEcheancier);

        // Affichage des échéanciers de fèves et chocolats
        displayEcheancier("Echéancier de fèves", super.quantityFevesEcheancier, super.fevesUtiles);
        displayEcheancier("Echéancier de chocolats", super.quantityChocoEcheancier, super.lesChocolats);



        }

    public void miseAJourEcheanciers(){
        //On supprime la ligne du next actuel
        for(IProduit feve : super.lesFeves){
            super.quantityFevesEcheancier.get(feve).remove(0);
            if(super.quantityFevesEcheancier.get(feve).size() <= 2){
                super.quantityFevesEcheancier.get(feve).add(0.0);
            }
        }
        for(IProduit choco : super.lesChocolats){
            super.quantityChocoEcheancier.get(choco).remove(0);
            if(super.quantityChocoEcheancier.get(choco).size() <= 2){
                super.quantityChocoEcheancier.get(choco).add(0.0);
            }
        }
    }

    public HashMap<IProduit, List<Double>> traiterContrat(List<ExemplaireContratCadre> contratsList, HashMap<IProduit, List<Double>> EcheancierParProduit) {
        
        for(ExemplaireContratCadre contrat : contratsList){
            // On traite le contrat s'il n'a pas déjà été traité
            if(!this.contratTraite.contains(contrat.getNumero())){

                this.contratTraite.add(contrat.getNumero());
                //super.journalStrat.ajouter("----- Traitement du contrat " + contrat.getNumero()+" -----");
                // On ajoute la quantité de fèves reçue au stock
                IProduit prod = contrat.getProduit();
                Echeancier echeancier = contrat.getEcheancier();
                //super.journalStrat.ajouter("Produit : " + prod.toString());
                //super.journalStrat.ajouter("Echéancier : " + echeancier.toString());
                for (int i = 1; i <= echeancier.getNbEcheances(); i++) {
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
   