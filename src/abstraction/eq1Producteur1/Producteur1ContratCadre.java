package abstraction.eq1Producteur1;

import java.util.*;

import abstraction.eqXRomu.contratsCadres.*;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Producteur1ContratCadre extends Producteur1Acteur implements IVendeurContratCadre {

    private Producteur1 vendeur;
    private List<ExemplaireContratCadre> contrats;
    private Journal journal;

    public Producteur1ContratCadre() {
        this.journal = new Journal(getNom() + " - Journal Contrat Cadre", this);
        this.contrats = new ArrayList<>();
    }

    @Override
    public boolean vend(IProduit produit) {
        return false;//produit instanceof Feve;
    }

    @Override
    public Echeancier contrePropositionDuVendeur(ExemplaireContratCadre contrat) {
        IProduit produit = contrat.getProduit();
return null;   /* 
        if (!(produit instanceof Feve)) {
            journal.ajouter("Erreur : Produit non reconnu pour la contre-proposition.");
            return null;
        }

        Feve feve = (Feve) produit;
        Echeancier echeancierPropose = contrat.getEcheancier();
        double stockDispo = stock.getStock(feve);
        double quantiteMax = 0.25 * stockDispo;

        double masseTotale = echeancierPropose.getQuantiteTotale();
        int duree = echeancierPropose.getStepFin() - echeancierPropose.getStepDebut() + 1;

        if (masseTotale < 100 || duree < 1) {
            journal.ajouter("Refus de l'échéancier : masse totale < 100T ou durée < 1");
            return null;
        }

        double quantiteMinParStep = masseTotale / (10.0 * duree);

        if (masseTotale > quantiteMax) {
            double nouvelleMasse = quantiteMax;
            double quantiteParStep = Math.max(nouvelleMasse / duree, quantiteMinParStep);

            try {
                Echeancier contreProp = new Echeancier(echeancierPropose.getStepDebut(), duree, quantiteParStep);
                for (int i = 1; i < duree; i++) {
                    contreProp.ajouter(quantiteParStep);
                }
                journal.ajouter("Contre-proposition envoyée : " + contreProp);
                return contreProp;
            } catch (IllegalArgumentException e) {
                journal.ajouter("Erreur à la création de l’échéancier : " + e.getMessage());
                return null;
            }
        }

        for (int step = echeancierPropose.getStepDebut(); step <= echeancierPropose.getStepFin(); step++) {
            if (echeancierPropose.getQuantite(step) < quantiteMinParStep) {
                journal.ajouter("Refus : quantité trop faible à l'étape " + step);
                return null;
            }
        }

        journal.ajouter("Échéancier proposé accepté : " + echeancierPropose);
        return echeancierPropose; // */
    }

    @Override
    public double propositionPrix(ExemplaireContratCadre contrat) {
        IProduit produit = contrat.getProduit();

        if (produit.equals(Feve.F_BQ)) return 1200;
        if (produit.equals(Feve.F_MQ)) return 1800;
        if (produit.equals(Feve.F_HQ_BE)) return 2500;

        return 1.0;
    }

    @Override
    public double contrePropositionPrixVendeur(ExemplaireContratCadre contrat) {
        double prixPropose = contrat.getPrix();
        double contreProposition = prixPropose * 1.10;
        journal.ajouter("Contre-proposition de prix : " + contreProposition + " (initialement " + prixPropose + ")");
        return contreProposition;
    }

    @Override
    public void notificationNouveauContratCadre(ExemplaireContratCadre contrat) {
        this.contrats.add(contrat);
        journal.ajouter("Nouveau contrat cadre accepté : " + contrat);
    }

    @Override
    public double livrer(IProduit produit, double quantite, ExemplaireContratCadre contrat) {
        double quantiteEnStock = stock.getStock((Feve) produit);
        double quantiteLivree = Math.min(quantite, quantiteEnStock);

        if (quantiteLivree <= 0) {
            journal.ajouter("Échec de livraison : stock vide pour " + produit);
            return 0.0;
        }

        boolean retraitOk = stock.retirer(produit, quantiteLivree);

        if (retraitOk) {
            journal.ajouter("Livraison de " + quantiteLivree + " de " + produit + " pour le contrat " + contrat);
            return quantiteLivree;
        } else {
            journal.ajouter("Erreur de retrait lors de la livraison de " + produit);
            return 0.0;
        }
    }

    @Override
    public List<Journal> getJournaux() {
        List<Journal> res = super.getJournaux();
        res.add(journal);
        return res;
    }
} 
