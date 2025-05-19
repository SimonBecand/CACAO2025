package abstraction.eq6Transformateur3;
// @author Henri Roth
import java.util.ArrayList;
import java.util.List;

import abstraction.eqXRomu.filiere.IMarqueChocolat;

public class Transformateur3Marques extends Transformateur3StratQuantity implements IMarqueChocolat {
    protected List<String> marques;
    
    public Transformateur3Marques(){
        super();
        this.marques = new ArrayList<String>();
        this.marques.add("Fraudolat");
        this.marques.add("Bollorolat");
        this.marques.add("Hypocritolat");
        this.marques.add("Arnaquolat");
    }

    public List<String> getMarquesChocolat(){
        return marques;
    }
}
