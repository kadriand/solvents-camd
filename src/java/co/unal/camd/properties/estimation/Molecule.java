package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import javax.swing.event.TreeModelListener;
import java.util.ArrayList;
import java.util.Vector;


/**
 * this create ramdom molecules
 *
 * @author Nicol�s Moreno
 */

public class Molecule {

    private FunctionalGroupNode genotype;
    private int size;

    private double x; //composici�n
    private double fitness;

    private double ge;
    private double bt;
    private double d;
    private double mt;
    private double sl;
    private double ks;

    private Vector<TreeModelListener> treeModelListeners =
            new Vector<TreeModelListener>();

    public Molecule() {
        genotype = new FunctionalGroupNode(1);
        size = 1;
    }

    public Molecule(Molecule aMolecule) {
        genotype = aMolecule.genotype.clone();
        size = aMolecule.size;
        x = aMolecule.x;
        fitness = aMolecule.fitness;
    }

    public Molecule(FunctionalGroupNode root) {
        genotype = root;
        size = 1 + size(genotype);
    }


    public int size(FunctionalGroupNode root) {
        int s = 0;
        if (root != null) {
            s = root.getTotalGroupsCount();
        }
        return s;
    }

    private void chemicalFormula(FunctionalGroupNode root, String show) {
        if (root != null) {
            show = show + root.toString() + " - ";
            for (int i = 0; i < root.getSubGroups().size(); i++) {
                chemicalFormula(root.getGroupAt(i), show);
            }
        }
    }

    public Molecule clone() {
        return new Molecule(this); // @TODO: clonar objeto
    }

    /**
     * this method create the molec until valence =0 (first restriction) or
     * the number of molecules is equal to maxNumof Groups allow
     *
     */

    /**
     * the molecule made
     *
     * @return
     */
    public FunctionalGroupNode getMoleculeByRootGroup() {
        return genotype;
    }


    public int getTotalGroups() {
        return size;
    }

    public double getx() {
        return x;
    }

    public void setx(double aX) {
        x = aX;
    }

    public String toString(ContributionParametersManager aGC) {
        String show = "";
        ArrayList<FunctionalGroupNode> a = getArray();
        for (int i = 0; i < a.size(); i++) {
            show += aGC.getName(a.get(i).getRootNode()) + "-";
        }
        return show;
    }

    public FunctionalGroupNode getGroupAt(int i) {
        return getArray().get(i);
    }

    public void setFitness(double aValue) {
        fitness = aValue;
    }

    public double getFitness() {
        return fitness;
    }

    private ArrayList<FunctionalGroupNode> getArray() {
        ArrayList<FunctionalGroupNode> array = new ArrayList<FunctionalGroupNode>();
        return getArray(genotype, array);
    }

    private ArrayList<FunctionalGroupNode> getArray(FunctionalGroupNode aFunctionalGroupNode, ArrayList<FunctionalGroupNode> array) {
        if (aFunctionalGroupNode != null) {
            array.add(aFunctionalGroupNode);
            if (aFunctionalGroupNode.getGroupsCount() > 0) {
                for (int i = 0; i < aFunctionalGroupNode.getGroupsCount(); i++) {
                    getArray(aFunctionalGroupNode.getGroupAt(i), array);
                }
            }
        }
        return array;
    }

    public GroupArray getGroupArray() {
        ArrayList<FunctionalGroupNode> array = getArray();
        int n = array.size();
        int[] groups = new int[n];
        for (int i = 0; i < n; i++) {
            groups[i] = array.get(i).getRootNode();
        }
        GroupArray gA = new GroupArray(groups);

        return gA;
    }

    public ArrayList<Integer> get2OrderGroupArray(ContributionParametersManager aGC) {
        ArrayList<Integer> secondOrderCode = new ArrayList<Integer>();
        secOrderContribution(genotype, secondOrderCode, aGC);
        return secondOrderCode;
    }

    private void secOrderContribution(FunctionalGroupNode aRootFunctionalGroupNode, ArrayList<Integer> secondOrderCode, ContributionParametersManager aGC) {
        if (aRootFunctionalGroupNode != null) {
            identifySecondOrderGroups(aRootFunctionalGroupNode, secondOrderCode, aGC);
        }
        if (aRootFunctionalGroupNode.getGroupsCount() > 0) {
            for (int i = 0; i < aRootFunctionalGroupNode.getGroupsCount(); i++) {
                FunctionalGroupNode leaf = aRootFunctionalGroupNode.getGroupAt(i);
                secOrderContribution(leaf, secondOrderCode, aGC);
                //		System.out.println("prueba1");
            }
        }
    }

    private void identifySecondOrderGroups(FunctionalGroupNode root, ArrayList<Integer> secondOrderCode, ContributionParametersManager aGC) {
        ArrayList<String[]> s = aGC.getSecondOrderGroupCase(root.getRootNode());
        //	System.out.println("DimensionArray: "+s.size());
        int dim = root.getGroupsCount();
        int a[] = leavesToVector(root);
        if (s.size() > 0) {
            for (int i = 0; i < dim; i++) {
                int c = 0;
                int codeColumn = (int) Double.parseDouble(s.get(c)[2]);
                //System.out.println("prueba2: "+codeColumn);
                //System.out.println("prueba11: "+a[i]);
                while (a[i] >= codeColumn) {  //
                    //System.out.println("prueba6");
                    int[] caseOH = new int[2];
                    int[] tempCond = new int[3]; //this is the array of groups (less central group and second) that construct de second order groups
                    //System.out.println("prueba10: "+a[i]);
                    //System.out.println("prueba12: "+codeColumn);
                    if (a[i] == codeColumn) {
                        int[] b = new int[a.length];
                        for (int r = 0; r < a.length; r++) {
                            if (r == i) {
                                b[r] = 0;
                            } else {
                                b[r] = a[r];
                            }
                        }
                        //	System.out.println("prueba7");
                        //if(>0){ //no more groups bond
                        int restric = (int) Double.parseDouble(s.get(c)[4]);
                        int z = 3;
                        int caseArray = 1;
                        while (caseArray > 0 && z < s.get(c).length - 2) {
                            //   System.out.println("prueba8");
                            caseArray = (int) Double.parseDouble(s.get(c)[z]); //revisar todo esto para ver si puede se mas r�pido
                            //  System.out.println("Temp "+i+": "+caseArray);
                            tempCond[z - 2] = caseArray;
                            z = z + 1;
                        }
                        boolean flat = true;
                        if (restric == 14 || restric == 81 || restric == 82) {
                            caseOH[0] = tempCond[0];
                            caseOH[1] = tempCond[1];
                            tempCond[2] = 0;
                        }
                        if (sameVector(tempCond, b)) { //if the leaves are the same that sec order groups, add the code of SOG
                            for (int p = 0; p < dim; p++) {
                                if (root.getGroupAt(p).getRootNode() == caseOH[0]) {
                                    int[] tempCond2 = new int[1];
                                    tempCond2[0] = caseOH[1];
                                    flat = sameVector(caseOH, tempCond2);
                                    //		System.out.println("prueba5");
                                }
                            }
                            if (flat) {
                                //	System.out.println("caso: "+(int)Double.parseDouble(s.get(c)[0]));
                                secondOrderCode.add((int) Double.parseDouble(s.get(c)[0]));
                                s.remove(c);
                            }
                        }
                    }
                    c = c + 1;
                    //	System.out.println("C: "+c);
                    //System.out.println("Size: "+s.size());
                    if (s.size() <= c) {
                        codeColumn = 1000;
                    } else {
                        codeColumn = (int) Double.parseDouble(s.get(c)[2]);
                        // System.out.println("CC"+codeColumn);
                    }
                    //System.out.println("Ai"+a[i]);
                }

            }

        }
        //System.out.println("prueba3");
    }

    private boolean sameVector(int[] vect1, int[] vect2) {
        int n = 0;
        int l = vect1.length;
        //System.out.println("prueba4");
        //for(int i = 0; i < l ; i++){
        //System.out.println("Vtestfirst:"+i+" "+vect1[i]);
        //}
        for (int i = 0; i < l; i++) {
            //System.out.println("V1:"+i+" "+vect1[i]);
            for (int j = 0; j < vect2.length; j++) {
                //System.out.println("V2:"+j+" "+vect2[j]);
                if (vect1[i] == vect2[j]) {
                    vect1[i] = 0;
                    vect2[j] = 0;
                }
            }
        }
        for (int i = 0; i < l; i++) {
            //System.out.println("Vtest:"+i+" "+vect1[i]);
            n = n + vect1[i];
        }
        //System.out.println("n Value"+n);
        if (n == 0) {
            return true;
        } else {
            return false;
        }
    }

    private int[] leavesToVector(FunctionalGroupNode root) {
        int dim = root.getGroupsCount();
        int[] a = new int[dim];
        //System.out.println("prueba9");
        for (int i = 0; i < dim; i++) {
            a[i] = root.getGroupAt(i).getRootNode();
        }
        return a;
    }

    /**
     public static void main(String[] args) {
     // TODO Auto-generated method stub
     GenotypeChemistry Gc= new GenotypeChemistry();
     Gc.getSecondOrderGroupCase(4);
     }
     */
    public double getGe() {
        return ge;
    }

    public Molecule setGe(double ge) {
        this.ge = ge;
        return this;
    }

    public double getBt() {
        return bt;
    }

    public Molecule setBt(double bt) {
        this.bt = bt;
        return this;
    }

    public double getD() {
        return d;
    }

    public Molecule setD(double d) {
        this.d = d;
        return this;
    }

    public double getMt() {
        return mt;
    }

    public Molecule setMt(double mt) {
        this.mt = mt;
        return this;
    }

    public double getSl() {
        return sl;
    }

    public Molecule setSl(double sl) {
        this.sl = sl;
        return this;
    }

    public double getKs() {
        return ks;
    }

    public Molecule setKs(double ks) {
        this.ks = ks;
        return this;
    }


}
