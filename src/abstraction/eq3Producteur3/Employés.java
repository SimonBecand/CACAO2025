package abstraction.eq3Producteur3;
//Par Alice 
public abstract class Employés {
    protected  final int mainOeuvre;
    protected final double salaire;
    protected final double productivité;



    protected Employés(int mainOeuvre, double salaire, double productivité) {
        this.mainOeuvre = mainOeuvre;
        this.salaire = salaire;
        this.productivité = productivité;
    }

    public double getCoutParStep() {
        return mainOeuvre * salaire;
    }
    public double getProductivité() {
        return productivité;
    }
    public int getMainOeuvre() {
        return mainOeuvre;
    }
    public double getSalaire() {
        return salaire;
    }
    
}
