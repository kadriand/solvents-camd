package co.unal.camd.core;

import java.util.ArrayList;

import co.unal.camd.control.parameters.ContributionParametersManager;
import unalcol.evolution.*;

public class MoleculeGenotype extends Genotype<Molecules> {
    protected int maxNumGroups;
    protected ContributionParametersManager aGC;

    public MoleculeGenotype(int _maxNmGroups, ContributionParametersManager _aGC) {
        maxNumGroups = _maxNmGroups;
        aGC = _aGC;
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
        ArrayList<Node> auxiliar = new ArrayList<Node>();
        int min = (maxNumGroups) / 2;
        double aProba = 0.4;
        boolean opt = probafunc(auxiliar, aProba);
        for (int i = 0; i < random(min) + 1; i++) {
            refCode = getNewRefCode(1, aGC, opt);
            auxiliar.add(new Node(refCode));
            //System.out.println("Se agrego: "+aGC.getName(auxiliar.get(i).getRootNode())+" "+aGC.getPrincipalGroupCode(auxiliar.get(i).getRootNode()));
            if (probafunc(auxiliar, aProba) == true) {
                aGC.getCodeOfRowBNameOrRefCode(refCode);
                if (aGC.getCodeOfRow() > 1) {
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
        if (functional == true) {
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

    public boolean probafunc(ArrayList<Node> root, double aProba) {
        boolean show = false;
        double random = Math.random();
        if (random <= aProba && Restrictions.canBeFunctional(root, aGC)) {
            show = true;
        } else {
            show = false;
        }
        return show;
    }


    public Molecules addGroupToMolec(ArrayList<Node> leaves, int dim) {
        Node gr;
        gr = createRandomGroups(leaves.size() + 1, dim, leaves);
        //System.out.println("GroupNew: "+aGC.getName(gr.getRootNode()));
        boolean next = false;

        if (leaves.size() <= aGC.getValence(gr.getRootNode())) { // if is the last group
            next = true;
            while (next == true) {
                //System.out.println("tama�o: "+leaves.size());
                Node temporal = leaves.get(0);
                //	System.out.println("Grupo: "+gr.getRootNode());
                Restrictions.mayBeFuncFuncOrOH(temporal, gr, true, aGC);
                leaves.remove(0);
                if (leaves.size() == 0) {
                    next = false; //
                }
            }
            if (aGC.getValence(gr.getRootNode()) > gr.getGroupsCount()) {
                int m = gr.getGroupsCount();
                for (int i = 0; i < aGC.getValence(gr.getRootNode()) - m; i++) {  // en esta parte se corrigi� el error de la valencia incompleta
                    Node aG = new Node(getNewRefCode(1, aGC, probafunc(leaves, 0.4)));
                    //	System.out.println("SUb: "+aG.getRootNode());
                    Restrictions.mayBeFuncFuncOrOH(aG, gr, false, aGC);
                    dim = dim + 1;
                }
            }
            leaves.add(gr);
            dim = dim + 1;
        } else if (leaves.size() > aGC.getValence(gr.getRootNode())) {
            //System.out.println("valence New"+aGC.getValence(gr.getRootNode()));
            next = true;
            while (next == true) {
                if (leaves.size() > 0) {
                    Node temporal = leaves.get(0);
                    Restrictions.mayBeFuncFuncOrOH(temporal, gr, true, aGC);
                    leaves.remove(0);

                    //System.out.println("Valencia hojas: "+(aGC.getValence(gr.getRootNode())+" hijos: "+gr.getGroupsCount()));
                    if (aGC.getValence(gr.getRootNode()) - 1 == gr.getGroupsCount()) {
                        next = false; //
                    }

                } else {
                    next = false;
                }

            }
            int val = gr.getGroupsCount() - aGC.getValence(gr.getRootNode());
            //System.out.println("resta: "+val);
            leaves.add(gr);
            dim = dim + 1;
            if (leaves.size() >= 1) {
                addGroupToMolec(leaves, dim);
            }
        }
        //if(leaves.size()==1 && aGC.getValence(leaves.get(0))-leaves.get(0).getGroupsCount()==0){
        //return new Molecules(leaves.get(0));
        //}
        return new Molecules(leaves.get(0));
    }

    /**
     * public  void addGroupToMolec(Molecules aGenotype, ArrayList<Node> leaves){
     * ArrayList<Node> aux=new ArrayList();
     * Node gr;
     * boolean next=false;
     * int sizeAux=leaves.size()+1;/////////pendiente si conserva el valor para el c�lculo
     * while(leaves.size()>0 && dim<maxNumGroups){
     * //si solo falta uno despues de los iniciales
     * if(maxNumGroups-dim==1){
     * gr=new Node(getNewRefCode(aux.size()+leaves.size(), aGC, probafunc(leaves,0.3)));
     * next=true;
     * } else{ //si no....
     * gr=createRandomGroups(sizeAux, dim, leaves);
     * if(gr.freeValence(aGC)){next=true;}
     * }
     * while(next==true){
     * if(leaves.size()==0 && gr.getTemporalValence()>=1 && aux.size()>0){
     * Restrictions.mayBeFuncFuncOrOH(aux.get(aux.size()-1),gr,true,aGC);
     * aux.remove(aux.size()-1);
     * } 	else if(leaves.size()==0 && gr.getTemporalValence()>=1 && aux.size()==0){
     * Node aGroup=new Node(getNewRefCode(1, aGC, probafunc(leaves,0.3)));
     * gr.addGroup(aGroup);
     * dim=dim+1;
     * //System.out.println("Se agrego: "+aGroup.toString());
     * } else if(leaves.size()>0){
     * Node temporal=leaves.get(leaves.size()-1);
     * Restrictions.mayBeFuncFuncOrOH(temporal,gr,true,aGC);
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
     * Node aG=new Node(getNewRefCode(1, aGC, probafunc(leaves,0.3)));
     * <p>
     * Restrictions.mayBeFuncFuncOrOH(aG,aux.get(0),false,aGC);
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
    private Node createRandomGroups(int sizeAux, int dim, ArrayList<Node> leaves) {
        int Valence = randomProba(sizeAux, dim);
        //System.out.println("The valence was"+Valence);
        Node newG = new Node(getNewRefCode(Valence, aGC, probafunc(leaves, 0.4)));
        return newG;
    }

    /**
     * public  void createMolecAromaticOrCyclic(int type,int maxNumGroups, GenotypeChemistry aGC){
     * //5 is aromatic and 6 is cyclical
     * <p>
     * int amountOfType=0;
     * if(type==5){
     * amountOfType=6;
     * } else{ amountOfType=(int)random(12);}
     * <p>
     * ArrayList<Node> auxiliar=new ArrayList<Node>();
     * int min=(maxNumGroups)/2;
     * <p>
     * boolean opt=probafunc(0.3,aGC);
     * for(int i=0; i<amountOfType; i++){
     * auxiliar.add(new Node(type,getNewRefCode(type, aGC)));
     * }
     * addGroupToMolecAorC(auxiliar,maxNumGroups,aGC);
     * }
     * <p>
     * <p>
     * <p>
     * /**public void addGroupToMolecAorC(ArrayList<Node> leaves,int maxNumGroups,GenotypeChemistry aGC){
     * ArrayList<Node> aux=new ArrayList();
     * Node gr;
     * int sizeAux=leaves.size()+1;
     * for(int i=1;i<aPhenotype.size()-1;i++){
     * while(aPhenotype.get(i).getValence()>=0){
     * gr=createRandomGroups(aPhenotype,maxNumGroups,aGC,sizeAux);
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

    public static int getSize(Node root) {
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
