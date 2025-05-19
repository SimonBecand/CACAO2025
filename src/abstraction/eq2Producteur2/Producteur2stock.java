//Jérôme Roth

package abstraction.eq2Producteur2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



import abstraction.eqXRomu.filiere.Filiere;
import abstraction.eqXRomu.general.Journal;
import abstraction.eqXRomu.general.Variable;
import abstraction.eqXRomu.general.VariableReadOnly;
import abstraction.eqXRomu.produits.Feve;
import abstraction.eqXRomu.produits.IProduit;

public class Producteur2stock extends Producteur2sechage {

    protected HashMap<Feve,Double> prodParStep;
	protected HashMap<Feve,HashMap<Integer,Double>> stock;
    //protected HashMap<Feve,Variable> stockvar;
    protected HashMap<Feve,Double> seuil_stock;
	protected Variable stockTotal;
    protected Journal JournalStock;
    protected HashMap<Feve,Double> stock_initial;

    double cout_stockage = 7.5;


	public Producteur2stock() {

        super();
        this.seuil_stock = new HashMap<Feve,Double>();
        this.stock = new HashMap<Feve,HashMap<Integer,Double>>();
        for (Feve f : Feve.values()){
            this.stock.put(f,new HashMap<Integer,Double>());
        }
        //this.stockvar = new HashMap<Feve,Variable>();
		this.prodParStep = new HashMap<Feve, Double>();
        this.JournalStock = new Journal("Journal Stock Eq2",this);
        this.stock_initial = new HashMap<Feve,Double>();

        this.stock_initial.put(Feve.F_BQ,22900.0*2);
        this.stock_initial.put(Feve.F_BQ_E,2100.0*2);
        this.stock_initial.put(Feve.F_MQ,6500.0*2);
        this.stock_initial.put(Feve.F_MQ_E,1600.0*2);
        this.stock_initial.put(Feve.F_HQ_E,0.0*2);
        this.stock_initial.put(Feve.F_HQ_BE,560.0*2);

        double totalInitialStock = 0.0;

        for(Feve f : Feve.values()){ // On initialise la prod de chaque fève à 0 car on a rien de séché au step 1
            SetProdParStep(f,0); 

        }
        
        this.stockTotal = new Variable("Stock total", "Quantité totale de fèves en stock", this, totalInitialStock);
        		
        for (Feve f : Feve.values()) {

            double initialStock = this.stock_initial.get(f); //On commence avec 12000T de chaque fèves
            stockvar.put(f, new VariableReadOnly(this+"Stock"+f.toString().substring(2), "<html>Stock de feves "+f+"</html>",this, 0.0, prodParStep.get(f)*24, initialStock));
            int etape = 0;
            this.stock.get(f).put(etape, initialStock);
            this.stockvar.get(f).setValeur(this, initialStock,cryptogramme);


            totalInitialStock += initialStock;
        }
        SetTotalStock();

    }




    public void initialiser(){
        super.initialiser();
    }

	

    public void next(){
        
        super.next();
        
        UpdateProd();
        SetStockMin(0.1);
        ProdParStep();
        Check();
        TaxeStockage();
        Transfo();
        

    }


    public void SetTotalStock(){ //Met à jour la valeur du stock total si besoin

        double totalstock = 0.0;
        for(Feve f : Feve.values()){

            double nb = 0;

            for(Integer k : this.stock.get(f).keySet()){

                nb += this.stock.get(f).get(k);

            }

            totalstock += nb;
            this.stockvar.get(f).setValeur(this, nb,cryptogramme);

        }

        this.stockTotal.setValeur(this,totalstock,cryptogramme);

    }


    public void SetProdParStep(Feve f, double prod){  //Permet de modifier la production par next d'une fève en particulier

        this.prodParStep.put(f, prod);
       

    }


    public void UpdateProd(){ //Récupère les fèves sechées et les envoie dans le stock

        for(Feve f : Feve.values()){

            double feve_seche = fevesSeches.get(f);
            this.prodParStep.put(f,feve_seche);
            

        }
    }
 
	public void AddStock(Feve f, int step_prod, double prod){ //On ajouter un stock d'une fève en particulier

        stockvar.get(f).ajouter(this, prod, cryptogramme);
        Double actual_value = this.stock.get(f).get(step_prod);
        if(actual_value == null){

            this.stock.get(f).put(step_prod, prod); // On met à jour le stock de la fève
            this.stockTotal.ajouter(this,prod,cryptogramme); // On met à jour le stock 
            JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On ajoute "+ prod+" T de "+f+" au stock");

        }else{
        this.stock.get(f).put(step_prod, prod + actual_value); // On met à jour le stock de la fève
        this.stockTotal.ajouter(this,prod,cryptogramme); // On met à jour le stock 
        JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On ajoute "+ prod+" T de "+f+" au stock");
        }

	}

    public void DeleteStock(Feve f, double prod){ //On enlève le stock d'une fève en particulier

        stockvar.get(f).retirer(this, prod, cryptogramme);
        this.stockTotal.retirer(this,prod,cryptogramme);
        JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On enlève "+ prod+" T de "+f+" au stock");

        HashMap<Integer,Double> actualStocks = stock.get(f);
        Integer balayage = Filiere.LA_FILIERE.getEtape() - 8;
        boolean fait = false;
        while(!fait){

          
            Double premierStock = actualStocks.get(balayage);

            if(balayage > Filiere.LA_FILIERE.getEtape()){

                JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On a enlever plus de "+f+" que ce qu'on a dans le stock /!");
            }

            if(premierStock == null){

                balayage += 1;

            }else{

                if(premierStock < prod){

                prod = prod - premierStock;
                stock.get(f).put(balayage,0.);
                balayage += 1;

                }else{

                    stock.get(f).put(balayage,premierStock - prod);
                    fait = true;
                }
            }

        }
    }


    public void Check(){ //Supprime les fèves dont la date de stockage est dépassée

        for (Feve f : Feve.values()){

            List<Integer> step_a_delete = new ArrayList<>();

            for (Integer step : stock.get(f).keySet()){

                if(Filiere.LA_FILIERE.getEtape() - step > 8){

                    Double tonnes = stock.get(f).get(step);
                    step_a_delete.add(step);
                    stockvar.get(f).retirer(this,tonnes,cryptogramme);
                    this.stockTotal.retirer(this,tonnes,cryptogramme);
                    JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+": Suppresion de "+tonnes+"T de "+f+" car date de stockage dépassées");

                }
            }

            for(Integer step : step_a_delete){

                stock.get(f).remove(step);

            }

        }
    }

    public void Transfo_HQ(){

        Feve f = Feve.F_HQ_BE;
        HashMap<Integer,Double> actualStocks = stock.get(Feve.F_MQ_E);
        Integer balayage = Filiere.LA_FILIERE.getEtape() - 8;
        Double prod = 0.0;
        while(balayage < Filiere.LA_FILIERE.getEtape() - 5){

          
            Double premierStock = actualStocks.get(balayage);

            if(balayage > Filiere.LA_FILIERE.getEtape()){

                JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On a enlever plus de "+f+" que ce qu'on a dans le stock /!");
            }

            if(premierStock == null){

                balayage += 1;

            }else{

                Double prod_this_step = stock.get(f).get(balayage);

                stock.get(Feve.F_MQ).put(balayage,stock.get(Feve.F_MQ).get(balayage) + prod_this_step);
                stock.get(f).put(balayage,0.);
                balayage += 1;
                prod += prod_this_step;

            }

        }

        stockvar.get(f).retirer(this, prod, cryptogramme);
        stockvar.get(Feve.F_MQ).ajouter(this, prod, cryptogramme);
        JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On échange "+ prod+" T de "+ f +"contre du "+Feve.F_MQ);
    }

    public void Transfo_MQ_E(){


        Feve f = Feve.F_MQ_E;
        HashMap<Integer,Double> actualStocks = stock.get(Feve.F_MQ_E);
        Integer balayage = Filiere.LA_FILIERE.getEtape() - 8;
        Double prod = 0.0;
        while(balayage < Filiere.LA_FILIERE.getEtape()*1.0 - 5){

          
            Double premierStock = actualStocks.get(balayage);

            if(balayage > Filiere.LA_FILIERE.getEtape()){

                JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On a enlever plus de "+f+" que ce qu'on a dans le stock /!");
            }

            if(premierStock == null){

                balayage += 1;

            }else{

                Double prod_this_step = stock.get(f).get(balayage);

                stock.get(Feve.F_MQ).put(balayage,stock.get(Feve.F_MQ).get(balayage) + prod_this_step);
                stock.get(f).put(balayage,0.);
                balayage += 1;
                prod += prod_this_step;

            }

        }

        stockvar.get(f).retirer(this, prod, cryptogramme);
        stockvar.get(Feve.F_MQ).ajouter(this, prod, cryptogramme);
        JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : On échange "+ prod+" T de "+ f +"contre du "+Feve.F_MQ);
    }

    public void Transfo(){

        Transfo_HQ();
        Transfo_MQ_E();

    }

    public void ProdParStep(){  // On produit pour un next
{}
        JournalStock.ajouter(Filiere.LA_FILIERE.getEtape()+" : Ajout du à la production");
        for(Feve f : Feve.values()){ 
            AddStock(f,Filiere.LA_FILIERE.getEtape(),this.prodParStep.get(f));
        }

    }

    public void SetStockMin(double pourcentage){

        for(Feve f : Feve.values()){

            double prod = stockvar.get(f).getValeur();
            this.seuil_stock.put(f, pourcentage*prod);


        }


    }

    public double getQuantiteEnStock(IProduit p, int cryptogramme) { // Permet d'avoir la quantité totale en stock
		return super.getQuantiteEnStock(p, cryptogramme);
	}

    public void TaxeStockage(){

        double tonnes = this.stockTotal.getValeur(cryptogramme);
        Filiere.LA_FILIERE.getBanque().payerCout(this, cryptogramme, "Stockage", tonnes*cout_stockage);
        JournalBanque.ajouter("On paie "+tonnes*cout_stockage+" à la banque due au stockage");


    }

    public List<Variable> getIndicateurs() { //Modifie correctement les affichages
		List<Variable> res = super.getIndicateurs();

        res.addAll(stockvar.values());
		res.add(this.stockTotal);
		return res;
	}

    
	public List<Journal> getJournaux() { //Mets à jour les journaux
		List<Journal> res = super.getJournaux();
		res.add(JournalStock);
		return res;
	}

}
