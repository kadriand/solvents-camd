package co.unal.camd.ga.haea;

import java.util.ArrayList;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecules;
import unalcol.evolution.*;

public class MoleculeGenotype extends Genotype<Molecules> {
    protected int maxNumGroups;
    protected ContributionParametersManager parametersManager;

    public MoleculeGenotype(int _maxNmGroups, ContributionParametersManager _aGC) {
        maxNumGroups = _maxNmGroups;
        parametersManager = _aGC;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public Molecules newInstance() {
        Molecules aGenotype = new Molecules();
        int dim = 0;
        int refCode = 0;
        ArrayList<FunctionalGroupNode> auxiliar = new ArrayList<FunctionalGroupNode>();
        int min = (maxNumGroups) / 2;
        double aProba = 0.4;
        boolean opt = probafunc(auxiliar, aProba);
        for (int i = 0; i < random(min) + 1; i++) {
            refCode = getNewRefCode(1, parametersManager, opt);
            auxiliar.add(new FunctionalGroupNode(refCode));
            //System.out.println("Se agrego: "+parametersManager.getName(auxiliar.get(i).getRootNode())+" "+parametersManager.getPrincipalGroupCode(auxiliar.get(i).getRootNode()));
            if (probafunc(auxiliar, aProba) == true) {
                parametersManager.getCodeOfRowBNameOrRefCode(refCode);
                if (parametersManager.getCodeOfRow() > 1) {
                    opt = false;
                } else {
                    opt = true;
                }
            } else {
                opt = false;
            }
        }
        dim = auxiliar.size();
        return addGroupToMolec(auxiliar, dim);
    }

    /**
     * Returns the number of genes in the individual's genome
     *
     * @param genome
     * @return Number of genes in the individual's genome
     */
    public int size(Molecules genome) {
        return genome.getTotalGroups();
    }

    public static int getNewRefCode(int valence, ContributionParametersManager aGC, boolean functional) {
        int codeOfRow = 0;
        int refCode = 0;
        if (functional) {
            double proba = Math.random();
            double p = 0;
            int n = aGC.getTotalNumberOfGroupOfValence(valence);
            while (proba <= 1 - p) {
                codeOfRow = (int) (Math.random() * n) + 1;//random row to choose the group
                p = aGC.getProbability(valence, codeOfRow);
                //	System.out.println("pruebaaa");
            }
//	/	System.out.println("pruebaa2");
            refCode = aGC.getRefCode(valence, codeOfRow);
        } else {
            codeOfRow = 1;//the code of the firs group (Structural group)
            refCode = aGC.getRefCode(valence, codeOfRow);
        }
        return refCode;
    }

    public static int getNewRefCode(int type, ContributionParametersManager aGC) {
        int codeOfRow = 0;
        int refCode = 0;

        return refCode;
    }

    public boolean probafunc(ArrayList<FunctionalGroupNode> root, double aProba) {
        boolean show = false;
        double random = Math.random();
        if (random <= aProba && Restrictions.canBeFunctional(root, parametersManager)) {
            show = true;
        } else {
            show = false;
        }
        return show;
    }


    public Molecules addGroupToMolec(ArrayList<FunctionalGroupNode> leaves, int dim) {
        FunctionalGroupNode gr;
        gr = createRandomGroups(leaves.size() + 1, dim, leaves);
        //System.out.println("GroupNew: "+parametersManager.getName(gr.getRootNode()));
        boolean next = false;

        if (leaves.size() <= parametersManager.getValence(gr.getRootNode())) { // if is the last group
            next = true;
            while (next == true) {
                //System.out.println("tama�o: "+leaves.size());
                FunctionalGroupNode temporal = leaves.get(0);
                //	System.out.println("Grupo: "+gr.getRootNode());
                Restrictions.mayBeFuncFuncOrOH(temporal, gr, true, parametersManager);
                leaves.remove(0);
                if (leaves.size() == 0) {
                    next = false; //
                }
            }
            if (parametersManager.getValence(gr.getRootNode()) > gr.getGroupsCount()) {
                int m = gr.getGroupsCount();
                for (int i = 0; i < parametersManager.getValence(gr.getRootNode()) - m; i++) {  // en esta parte se corrigi� el error de la valencia incompleta
                    FunctionalGroupNode aG = new FunctionalGroupNode(getNewRefCode(1, parametersManager, probafunc(leaves, 0.4)));
                    //	System.out.println("SUb: "+aG.getRootNode());
                    Restrictions.mayBeFuncFuncOrOH(aG, gr, false, parametersManager);
                    dim = dim + 1;
                }
            }
            leaves.add(gr);
            dim = dim + 1;
        } else if (leaves.size() > parametersManager.getValence(gr.getRootNode())) {
            //System.out.println("valence New"+parametersManager.getValence(gr.getRootNode()));
            next = true;
            while (next == true) {
                if (leaves.size() > 0) {
                    FunctionalGroupNode temporal = leaves.get(0);
                    Restrictions.mayBeFuncFuncOrOH(temporal, gr, true, parametersManager);
                    leaves.remove(0);

                    //System.out.println("Valencia hojas: "+(parametersManager.getValence(gr.getRootNode())+" hijos: "+gr.getGroupsCount()));
                    if (parametersManager.getValence(gr.getRootNode()) - 1 == gr.getGroupsCount()) {
                        next = false; //
                    }

                } else {
                    next = false;
                }

            }
            int val = gr.getGroupsCount() - parametersManager.getValence(gr.getRootNode());
            //System.out.println("resta: "+val);
            leaves.add(gr);
            dim = dim + 1;
            if (leaves.size() >= 1) {
                addGroupToMolec(leaves, dim);
            }
        }
        //if(leaves.size()==1 && parametersManager.getValence(leaves.get(0))-leaves.get(0).getGroupsCount()==0){
        //return new Molecules(leaves.get(0));
        //}
        return new Molecules(leaves.get(0));
    }

    /**
     * public  void addGroupToMolec(Molecules aGenotype, ArrayList<FunctionalGroupNode> leaves){
     * ArrayList<FunctionalGroupNode> aux=new ArrayList();
     * FunctionalGroupNode gr;
     * boolean next=false;
     * int sizeAux=leaves.size()+1;/////////pendiente si conserva el valor para el c�lculo
     * while(leaves.size()>0 && dim<maxNumGroups){
     * //si solo falta uno despues de los iniciales
     * if(maxNumGroups-dim==1){
     * gr=new FunctionalGroupNode(getNewRefCode(aux.size()+leaves.size(), parametersManager, probafunc(leaves,0.3)));
     * next=true;
     * } else{ //si no....
     * gr=createRandomGroups(sizeAux, dim, leaves);
     * if(gr.freeValence(parametersManager)){next=true;}
     * }
     * while(next==true){
     * if(leaves.size()==0 && gr.getTemporalValence()>=1 && aux.size()>0){
     * Restrictions.mayBeFuncFuncOrOH(aux.get(aux.size()-1),gr,true,parametersManager);
     * aux.remove(aux.size()-1);
     * } 	else if(leaves.size()==0 && gr.getTemporalValence()>=1 && aux.size()==0){
     * FunctionalGroupNode aGroup=new FunctionalGroupNode(getNewRefCode(1, parametersManager, probafunc(leaves,0.3)));
     * gr.addGroup(aGroup);
     * dim=dim+1;
     * //System.out.println("Se agrego: "+aGroup.toString());
     * } else if(leaves.size()>0){
     * FunctionalGroupNode temporal=leaves.get(leaves.size()-1);
     * Restrictions.mayBeFuncFuncOrOH(temporal,gr,true,parametersManager);
     * leaves.remove((leaves.size()-1));
     * }
     * if(gr.getTemporalValence()==1){next=false;}//other group can be added
     * if(maxNumGroups-dim==1){next=true;}//is the last group
     * if(gr.getTemporalValence()==0){next=false;}//the last group is full
     * }
     * //System.out.println("Se agrego: "+gr.toString());
     * aux.add(gr);
     * dim=dim+1;
     * }
     * if(aux.size()==1 && aux.get(0).getTemporalValence()>1){
     * FunctionalGroupNode aG=new FunctionalGroupNode(getNewRefCode(1, parametersManager, probafunc(leaves,0.3)));
     * <p>
     * Restrictions.mayBeFuncFuncOrOH(aG,aux.get(0),false,parametersManager);
     * System.out.println("Se agrego: "+aG.toString());
     * <p>
     * aGenotype=new Molecules(aux.get(0));
     * //System.out.println(aGenotype.getMoleculeByRootGroup()+"root");
     * }else if(aux.size()==1 && aux.get(0).getTemporalValence()==0){
     * aGenotype=new Molecules(aux.get(0));
     * }else if(aux.size()>1){
     * addGroupToMolec(aGenotype,aux,dim);
     * }
     * }
     * <p>
     * <p>
     * /**
     * Create random groups
     *
     * @param aGroup
     * @return
     */
    private FunctionalGroupNode createRandomGroups(int sizeAux, int dim, ArrayList<FunctionalGroupNode> leaves) {
        int Valence = randomProba(sizeAux, dim);
        //System.out.println("The valence was"+Valence);
        FunctionalGroupNode newG = new FunctionalGroupNode(getNewRefCode(Valence, parametersManager, probafunc(leaves, 0.4)));
        return newG;
    }

    /**
     * public  void createMolecAromaticOrCyclic(int type,int maxNumGroups, GenotypeChemistry parametersManager){
     * //5 is aromatic and 6 is cyclical
     * <p>
     * int amountOfType=0;
     * if(type==5){
     * amountOfType=6;
     * } else{ amountOfType=(int)random(12);}
     * <p>
     * ArrayList<FunctionalGroupNode> auxiliar=new ArrayList<FunctionalGroupNode>();
     * int min=(maxNumGroups)/2;
     * <p>
     * boolean opt=probafunc(0.3,parametersManager);
     * for(int i=0; i<amountOfType; i++){
     * auxiliar.add(new FunctionalGroupNode(type,getNewRefCode(type, parametersManager)));
     * }
     * addGroupToMolecAorC(auxiliar,maxNumGroups,parametersManager);
     * }
     * <p>
     * <p>
     * <p>
     * /**public void addGroupToMolecAorC(ArrayList<FunctionalGroupNode> leaves,int maxNumGroups,GenotypeChemistry parametersManager){
     * ArrayList<FunctionalGroupNode> aux=new ArrayList();
     * FunctionalGroupNode gr;
     * int sizeAux=leaves.size()+1;
     * for(int i=1;i<aPhenotype.size()-1;i++){
     * while(aPhenotype.get(i).getValence()>=0){
     * gr=createRandomGroups(aPhenotype,maxNumGroups,parametersManager,sizeAux);
     * aPhenotype.get(i).addGroup(gr);
     * gr.setControlCode(aPhenotype.size()-1);
     * aPhenotype.add(gr);
     * }
     * }
     * }
     * <p>
     * /**
     * Generate random numbers between 0-3 (valence Group), the probability change with
     * the amount of groups in the molecule
     *
     * @param numG
     * @return
     */
    public int randomProba(int sizeAux, int dim) {
        int valence = 0;
        int x = maxNumGroups - dim; // groups to complete the molec
        int y = 2 * x + 2;
        double probOf2or4 = Math.random();
        if (sizeAux >= y) {
            valence = 4;
        } else {
            if (probOf2or4 < 0.35) {
                valence = random(2) + 3;
            } else {
                valence = 2;
            }
        }
        return valence;
    }

    /**
     * this method return a random number between 0 and range-1
     *
     * @param range
     * @return
     */
    public static int random(int range) {
        return (int) (Math.random() * range);
    }

    public static int getSize(FunctionalGroupNode root) {
        int counter = 0;
        if (root != null) {
            counter = counter + root.getSubGroups().size();
            for (int i = 0; i < root.getSubGroups().size(); i++) {
                getSize(root.getGroupAt(i));
            }
        }
        return counter;
    }

    /**
     public void otherCreateMolec(){
     int noGroups=random(maxNumGroups);
     double funcProba=3/noGroups;
     int valence =random(4);
     Group aGroup= new Group(valence,operator,probafunc(funcProba));
     array.add(aGroup);
     //initialize molecule
     for(int i=0;i<valence+1;i++){
     aGroup=new Group(1,operator,probafunc(funcProba));
     array.add(aGroup);
     }
     while(array.size()<noGroups){
     //restriction for number of groups
     int dif=noGroups-array.size();
     if(dif==1){
     valence=2;
     } else if(dif==2){
     valence=random(2)+2;
     } else if(dif>=3){
     valence =random(3)+2;
     }

     aGroup= new Group(valence,operator,probafunc(funcProba));
     array.add(aGroup);
     for(int i=0;i<valence-2;i++){
     aGroup=new Group(1,operator,probafunc(funcProba));
     array.add(aGroup);
     }
     }
     }
     public void builtMolec(ArrayList<Group> anArray){
     ArrayList<Group> auxArray=new ArrayList(anArray);
     rootGroup=anArray.get(0);
     auxArray.remove(0);

     for(int i=0;i<anArray.size();i++){
     int r=random(auxArray.size());

     while(rootGroup.getCodeOfRow()>1 && auxArray.get(r).getCodeOfRow()>1){
     r=random(auxArray.size());
     }
     while(rootGroup.getValence()>0){
     rootGroup.addGroup(auxArray.get(r));
     }
     }
     }

     */


}
