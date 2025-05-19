package abstraction.eq4Transformateur1;
import java.awt.Color;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.produits.IProduit; 

/*
 * @author YAOU Reda
 * Cette classe gère les machines et les ouvriers de l'usine, ainsi que les coûts associés.
 */
public class Transformateur1Usine extends Transformateur1Acteur {

	// Paramètres variables Usine
	protected Variable nbOuvriers;
	protected Variable nbMachines;
	protected Variable prodMax; // nombre de tonne de chocolat qu'on peut produire à chaque step
	protected double totalCoutsUsineStep;

	// Paramètres fixes Usine
	protected double prodOuvrier = 15000;
	protected double prodMachine = 1e7;
	protected double salaireOuvrier = 860; // À modifier selon les règles transformateurs
	protected double coutLicenciement = 10000;
	protected double prixAchatMachine = 10000; // À modifier selon les règles transformateurs
    protected double coutAdditionnelFixe = 2e8; // cout additionnel fixe de l'usine selon les règles transformateurs
	protected double coutAddditonnelUnitaire= 0.30*1e4; // 0.3€ pour 100g et on le considère par tonne


	public Transformateur1Usine() {
		super();
		this.nbOuvriers = new Variable("Nombre d'ouvriers", this, 0, 1e7, 33333);
		this.nbMachines = new Variable("Nombre de machines", this, 0, 1e7, 50);
		this.prodMax = new Variable("Production max", this, 0, 1e7, this.getProdMax());
		this.totalCoutsUsineStep = 0;
	}

	public double getProdMax() {
		double prodMaxMachine = this.nbMachines.getValeur() * prodMachine * 1e-4;
		double prodMaxOuvrier = this.nbOuvriers.getValeur() * prodOuvrier * 1e-4;
		return Math.min(prodMaxMachine, prodMaxOuvrier);
	}

	public void initialiser() {
		super.initialiser();
		this.prodMax.setValeur(this, this.getProdMax());
        this.totalCoutsUsineStep = 0;
	}

	public void next() {
		super.next();
		this.totalCoutsUsineStep = 0;
		/*
		 * Ici on peut au besoin acheter des machines ou licencier des ouvriers et ainsi mettre à jour totalCoutsUsineStep
		 */
		// On ajoute le salaire des ouvriers
		this.totalCoutsUsineStep += this.nbOuvriers.getValeur() * salaireOuvrier;

		//On ajoute les couts additionnels de l'usine
		this.totalCoutsUsineStep += coutAdditionnelFixe; 
		this.totalCoutsUsineStep += coutAddditonnelUnitaire * qttProduiteChoco.getValeur();

		//On paie le coût de l'usine
		Filiere.LA_FILIERE.getBanque().payerCout(this, super.cryptogramme, "Coûts Usine", totalCoutsUsineStep);
		this.journalCouts.ajouter(Color.white, Color.black, "Coûts de l'usine : " + totalCoutsUsineStep + " euros.");
	}

	public void achatMachine(int quantite) {
		double cout = quantite * prixAchatMachine;
		if (cout < this.getSolde()) {

			// Selon les règles, les machines ne devraient arriver qu'après 2 nexts, ce que je n'ai pas réalisé ici
			// Donc à modifier
			this.nbMachines.setValeur(this, this.nbMachines.getValeur() + quantite);
			this.prodMax.setValeur(this, this.getProdMax());
			this.totalCoutsUsineStep += cout;
			this.journalCouts.ajouter(Color.white, Color.black, "Achat de " + quantite + " machines pour " + cout + " euros.");
			this.journalCouts.ajouter("\n");
		} else {
			this.journalCouts.ajouter(Color.pink, Color.black, "Achat de " + quantite + " machines impossible : pas assez d'argent.");
			this.journalCouts.ajouter("\n");
		}	
	}

	public void licenciementOuvrier(int quantite) {
		double cout = quantite * coutLicenciement;
		if (cout < this.getSolde()) {
			if (this.nbOuvriers.getValeur() > quantite) {
				this.nbOuvriers.setValeur(this, this.nbOuvriers.getValeur() - quantite);
				this.prodMax.setValeur(this, this.getProdMax());
				this.totalCoutsUsineStep += cout;
				this.journalCouts.ajouter(Color.white, Color.black, "Licenciement de " + quantite + " ouvriers pour " + cout + " euros.");
				this.journalCouts.ajouter("\n");
			} else {
				this.journalCouts.ajouter(Color.pink, Color.black, "Licenciement de " + quantite + " ouvriers impossible : pas assez d'ouvriers.");
				this.journalCouts.ajouter("\n");
			}
		} else {
			this.journalCouts.ajouter(Color.pink, Color.black, "Licenciement de " + quantite + " ouvriers impossible : pas assez d'argent.");
			this.journalCouts.ajouter("\n");
		}
	}
}

