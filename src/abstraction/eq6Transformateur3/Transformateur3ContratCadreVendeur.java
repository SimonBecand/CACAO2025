package abstraction.eq6Transformateur3;

import java.util.HashMap;
import java.util.LinkedList;

import abstraction.eqXRomu.contratsCadres.Echeancier;
import abstraction.eqXRomu.contratsCadres.ExemplaireContratCadre;
import abstraction.eqXRomu.contratsCadres.IAcheteurContratCadre;
import abstraction.eqXRomu.contratsCadres.IVendeurContratCadre;
import abstraction.eqXRomu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.IProduit;
// @author Henri Roth & Eric Schiltz
public class Transformateur3ContratCadreVendeur extends Transformateur3Fabriquant implements IVendeurContratCadre {
    
    //Capacité de vente de chocolat
    protected HashMap<IProduit, Double> capacite_vente_max;
    
    //des constructeurs
    //on utilise à chaque fois des instances de cette classe quand on fait des contrats cadres
    public Transformateur3ContratCadreVendeur() {
        this.ContratsVendeur=new LinkedList<ExemplaireContratCadre>();
	}

    // @author Florin Malveau
    public void initialiser() {
        super.initialiser();
    }
    //des méthodes
    //à chaque next on va proposer des contrats cadres pour vendre du chocolat
    @Override //@author Henri Roth & Eric SCHILTZ
    public void next(){
        //on récupère le next de tous les pères
        super.next();
        //on remet capacité_vente_max à 0 
        capacite_vente_max = new HashMap<IProduit, Double>();
        capacite_vente_max.put(fraud,(productionMax*0.9)/3);
        capacite_vente_max.put(hypo,(productionMax*0.9)/6);
        capacite_vente_max.put(arna,(productionMax*0.9)/6);
        capacite_vente_max.put(bollo,(productionMax*0.9)/3);
        //on lui enlève tous les contrats cadres en cours  
        //on parcourt tous les chocolats
        for(IProduit choco : super.lesChocolats){
            //somme pour ce chocolat de ce que l'on doit pour l'instant livrer au step suivant
            double a = 0;
            //on parcourt tous les contrats cadres
            for (ExemplaireContratCadre contrat : ContratsVendeur){
                //pour chaque contrat on regarde si il concerne ce produit 
                if (contrat.getProduit()==choco){
                    //si oui on additionne ce que l'on doit pour l'instant livrer au step suivant 
                    a += contrat.getQuantiteALivrerAuStep();
                }
            }
            //on remet alors à jour capacite_vente_max
            capacite_vente_max.replace(choco,capacite_vente_max.get(choco)-a);
        }
        //on crée un nouveau nom plus simple pour le superviseur de contrats cadres
        SuperviseurVentesContratCadre supCCadre = (SuperviseurVentesContratCadre) Filiere.LA_FILIERE.getActeur("Sup.CCadre");
        //on parcourt tous les types de chocolat
        for(IProduit choco : super.lesChocolats){
            //on parcourt les acteurs de la filière
            for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
                //si l'acteur n'est pas nous et si l'acteur achète des contrats cadres et s'il achète
                //du chocolat par contrat cadre
                if (acteur!=this && acteur instanceof IAcheteurContratCadre && ((IAcheteurContratCadre)acteur).achete(choco)) {
                    //on propose un contrat cadre à l'acteur en question qui démarre à l'étape
                    //suivante de la filière, qui dure 10 step avec une quantité livrée de notre 
                    //capacité de production restante
                    //par step et avec ou non tête de gondole ici en fonction du chocolat
                    // pour hypocritolat on le demande pas 
                    if (choco==hypo){
                        double capa = capacite_vente_max.get(hypo);
                        if(capa > 0){// A CORRIGER POUR QUE CA NE SOIT PAS UN BUG
                            supCCadre.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) choco, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, capa), cryptogramme, false);
                        }
                    }
                    //pour tous les autres chocolats on les demande en tête de gondole 
                    //et il faut aussi demander pour les mêmes chocolats en non tête de gondole 
                    //car on nous le refusera dans 90% des cas et donc il faut aussi le demander 
                    //sans tête de gondole
                    else {
                        double capa = capacite_vente_max.get(choco);
                        if(capa > 0){// A CORRIGER POUR QUE CA NE SOIT PAS UN BUG
                            supCCadre.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) choco, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, capa), cryptogramme, true);
                            supCCadre.demandeVendeur((IAcheteurContratCadre)acteur, (IVendeurContratCadre)this,(IProduit) choco, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, capa), cryptogramme, false);
                        }
                    } 
                }
            }
        }
    }

    @Override //@author Eric Schiltz
    public boolean vend(IProduit produit) {
        //On dit oui dès que l'on vend bien de ce produit 
        if(super.lesChocolats.contains(produit)){
            return true;
        }
        else{
            return false;
        }
        //sert à compter les quantités de contrat cadre pour le tour suivant pour chaque produit  
        //double a = 0;
        //for (ExemplaireContratCadre contrat : ContratsVendeur){
        //    if (contrat.getProduit()==produit){
        //        a += contrat.getQuantiteALivrerAuStep();
        //    }
    }

    @Override //@author Eric Schiltz
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        //quand le vendeur propose une tête de gondole on dit toujours oui. On ne le tient 
        //pas en compte. On pourrait les refuser une fois sur 10 par ex mais en fait : 
        //On ne fais pas la différene entre les propositions du vendeur et ses contrepropositions
        //à nos propositions. 
        //Sinon on aurait des problèmes, on risquerait de refuser des contrats 
        //que nous avons nous même émis sans tête de gondole. 
        //on regarde quel produit est concerné par le contrat
        IProduit p = contrat.getProduit();
        //on regarde si on vend/produit bien de ce chocolat
        if(super.lesChocolats.contains(p)){
            // si oui on calcule de la quantite par step de chocolat que l'on veut livrer
            //on regarde quelle est notre capacité de vente du produit 
            double capa = capacite_vente_max.get(p);
            //on récupère l'échéancier
            Echeancier e = contrat.getEcheancier();
            //on récupère le premier step
            int stepdebut = e.getStepDebut();
            //on rearde la quantité à livrer proposée
            double QuantiteStep = e.getQuantite(stepdebut);
            // si la quantité à livrer proposé est dans nos capacités on accepte
            if(QuantiteStep < capa){
                //et on met à jour nos capacités de vente max
                return contrat.getEcheancier();
            }
            //sinon
            else{
                //si elle est vrmt trop petite : on abandonne 
                if(capa < 100){
                    return null;
                }
                //sinon on refait l'échéancier avec ce que l'on a; 
                else{
                    //et on met à jour nos capacités de vente max
                    int nb = e.getNbEcheances();
                    return new Echeancier(Filiere.LA_FILIERE.getEtape()+1, nb, capa);
                }
            }
        }
        //si on ne le vend/produit pas on ne fait rien 
        return null;
    }

    @Override //@author Henri Roth
    public double propositionPrix(ExemplaireContratCadre contrat) {
        // on retourne notre proposition de prix pour chaque 
        IProduit choco = contrat.getProduit();
        if(choco.equals((IProduit)fraud)){
            return 150000;
        }
        if(choco.equals((IProduit)bollo)){
            return 200000;
        }
        if(choco.equals((IProduit)arna)){
            return 300000;
        }
        else{
            return 500000;
        }
    }

    @Override //@author Henri Roth
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        //à chaque fois on propose 1.2 fois le prix proposé par l'acheteur
        return contrat.getPrix()*1.2;
    }

    @Override //@author Henri Roth
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        journalCC.ajouter("Nouveau contrat cadre Vendeur" +contrat);
        ContratsVendeur.add(contrat);
        IProduit produit = contrat.getProduit();
        double QuantiteAuStep = contrat.getQuantiteALivrerAuStep();
        double c_v = capacite_vente_max.get(produit);
        c_v -= QuantiteAuStep;
        capacite_vente_max.replace(produit, c_v);
    }

    @Override //@author Henri Roth
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        double stockActuel = stockChoco.getQuantityOf(produit);
		double aLivre = Math.min(quantite, stockActuel);
		journalCC.ajouter("   Livraison de "+aLivre+" T de "+produit+" sur "+quantite+" exigees pour contrat "+contrat.getNumero());
        stockChoco.remove(produit, aLivre);
		return aLivre;
	}
    
}
