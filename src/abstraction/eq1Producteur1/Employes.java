package abstraction.eq1Producteur1;


import abstraction.eqXRomu.general.Journal;

// AMAL MONCER

public class Employes  { 
    protected int employesBQ; // Nombre d'employés pour la basse qualité 
    protected int employesMQ; // Nombre d'employés pour la moyenne qualité
    protected int employesHQ; // Nombre d'employés pour la haute qualité
    protected int employesPermanents; // Nombre d'employés permanents


    protected int enfants; // Nombre d'enfants employés
    protected int adultesNonFormes; // Nombre d'adultes non formés employés
    protected int adultesFormes; // Nombre d'adultes formés employés


    protected final double coutEnfant = 2.0; // Coût d'entretien par step pour un enfant
    protected final double coutAdulteNonForme = 5.0; // Coût d'entretien par step pour un adulte non formé
    protected final double coutAdulteForme = 12.5; // Coût d'entretien par step pour un adulte formé

    private Journal journal; // Journal pour enregistrer les opérations
    private Producteur1 Producteur1; // Référence au Producteur1

    public Employes() {
        // Initialisation des employés selon les besoins en main-d'œuvre (V1)
        this.employesBQ = 8; // BQ : 8 employés/ha (dont min. 2 adultes)
        this.employesMQ = 6; // MQ : 6 employés/ha (dont min. 2 adultes)
        this.employesHQ = 4; // HQ : 4 employés/ha (dont min. 2 adultes)
        this.employesPermanents = 2; // Main-d'œuvre permanente : 2 employés/ha

        // Initialisation des types d'employés
        this.enfants = 0;
        this.adultesNonFormes = 0;
        this.adultesFormes = 0;
        this.journal = new Journal("Journal Employés", Producteur1); // Initialisation du journal
    }

    // Méthode pour obtenir le nombre d'employés pour la basse qualité
    public int getEmployesBQ() {
        return employesBQ;
    }

    // Méthode pour obtenir le nombre d'employés pour la moyenne qualité
    public int getEmployesMQ() {
        return employesMQ;
    }

    // Méthode pour obtenir le nombre d'employés pour la haute qualité
    public int getEmployesHQ() {
        return employesHQ;
    }

    // Méthode pour obtenir le nombre d'employés permanents
    public int getEmployesPermanents() {
        return employesPermanents;
    }

    // Méthode pour obtenir le nombre d'enfants employés
    public int getEnfants() {
        return enfants;
    }

    // Méthode pour obtenir le nombre d'adultes non formés employés
    public int getAdultesNonFormes() {
        return adultesNonFormes;
    }

    // Méthode pour obtenir le nombre d'adultes formés employés
    public int getAdultesFormes() {
        return adultesFormes;
    }

    // Méthode pour mettre à jour les besoins en main-d'œuvre selon V2
    public void updateBesoinsMainOeuvreV2() {
        this.employesBQ = 10; // BQ : 10 employés/ha pour la récolte
        this.employesMQ = 8; // MQ : 8 employés/ha pour la récolte
        this.employesHQ = 6; // HQ : 6 employés/ha pour la récolte
        this.employesPermanents = 2; // Main-d'œuvre permanente : 2 employés/ha
    }

    // Méthode pour calculer le coût total d'entretien par step
    public double calculerCoutEntretien() {
        return (enfants * coutEnfant) + (adultesNonFormes * coutAdulteNonForme) + (adultesFormes * coutAdulteForme);
    }

    public void next(){
        calculerCoutEntretien();
        journal.ajouter("Coût d'entretien total par step : " + calculerCoutEntretien());
    }

    // Méthode pour ajouter des enfants employés
    public void ajouterEnfants(int nombre) {
        this.enfants += nombre;
    }

    // Méthode pour ajouter des adultes non formés employés
    public void ajouterAdultesNonFormes(int nombre) {
        this.adultesNonFormes += nombre;
    }

    // Méthode pour ajouter des adultes formés employés
    public void ajouterAdultesFormes(int nombre) {
        this.adultesFormes += nombre;
    }
}
