package abstraction.eq7Distributeur1;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import abstraction.eqXRomu.produits.ChocolatDeMarque;
import abstraction.eqXRomu.produits.Chocolat;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.filiere.IActeur;
import abstraction.eqXRomu.produits.IProduit;
import java.lang.reflect.Array;
import abstraction.eqXRomu.general.Journal;

public class Distributeur1Stock extends Distributeur1Acteur{
    protected Map<ChocolatDeMarque, Variable> stocksChocolats;
    protected List<ChocolatDeMarque> chocolats;
	protected Journal journalE;  // Déclaration du journal
	protected Journal journalCC; // Déclaration du journal
	protected Journal journalAO; // Déclaration du journal

    public Distributeur1Stock() // Alexiho
    {
        this.stocksChocolats = new HashMap<>();

        this.chocolats = new ArrayList<ChocolatDeMarque>();

		this.journalE = new Journal("Journal d'enchères de EQ7", this); // Initialisation du journal
		this.journalCC = new Journal("Journal contrat cadre de EQ7", this);
		this.journalAO = new Journal("Journal appel d'offre de EQ7", this);
    }

	public int cdmToInt(ChocolatDeMarque c){ // par Alexiho
		return chocolats.indexOf(c);
	}

	public ChocolatDeMarque intToCdm(int i){ // par Alexiho
		return chocolats.get(i);
	}

    public Variable getStock(ChocolatDeMarque c) { // par Alexiho
		return this.stocksChocolats.get(c);
	}


	public double VolumetoBuy(ChocolatDeMarque choco, int crypto){ // par Ethan
		int etape = Filiere.LA_FILIERE.getEtape();
		double ancient_value_mid = 0.0;
		double val1 = 0.0;
		double val2 = 0.0;
		double val_demand = 0.0;
		
		if (etape > 3){
			val1 = Filiere.LA_FILIERE.getVentes(choco, etape-27)+ Filiere.LA_FILIERE.getVentes(choco, etape-26) + Filiere.LA_FILIERE.getVentes(choco, etape-25) ;
			val2 = Filiere.LA_FILIERE.getVentes(choco, etape-3)+ Filiere.LA_FILIERE.getVentes(choco, etape-2) + Filiere.LA_FILIERE.getVentes(choco, etape-1) ;
			ancient_value_mid = (val2/val1)*Filiere.LA_FILIERE.getVentes(choco, etape-24);
		}
		else{
		ancient_value_mid = Filiere.LA_FILIERE.getVentes(choco, etape-24) ;
		}
		if(getQuantiteEnStock(choco, cryptogramme)>25000.0){
			return 0.0;
		}
		else{
			val_demand = 1.05*ancient_value_mid - getQuantiteEnStock(choco, crypto);
			if (val_demand > 100.0){
				return Math.min(val_demand, 9000.0);
			}
			else{
				return 100.0;
			}
	}
	}

	public Map<ChocolatDeMarque, Variable> getStocksChocolats() { // par Alexiho
		return this.stocksChocolats;
	}

	@Override
	public double getQuantiteEnStock(IProduit p, int cryptogramme) { // par Alexiho
		if (this.cryptogramme==cryptogramme) {
			for (ChocolatDeMarque c : this.stocksChocolats.keySet()) {
				if (c.equals(p)) {
					return this.stocksChocolats.get(c).getValeur();
				}
			}
			return 0;
		} else {
			return 0;
		}
	}

	@Override
	public List<Variable> getIndicateurs(){
		List<Variable> indicateurs = super.getIndicateurs();
		return(indicateurs);
	}
}