package co.unal.camd.properties.estimation;

import co.unal.camd.view.CamdRunner;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.event.TreeModelListener;
import java.util.ArrayList;
import java.util.Vector;


/**
 * this create ramdom molecules
 *
 * @author Nicol�s Moreno
 */
@Data
@Accessors(chain = true)
public class Molecule {

    private FunctionalGroupNode genotype;
    private int size;

    private double fitness;

    private double x; //composición

    private double ge;
    private double bt;
    private double d;
    private double mt;
    private double sl;
    private double dc;
    private double ks;

    private Vector<TreeModelListener> treeModelListeners = new Vector<>();

    public Molecule() {
        genotype = new FunctionalGroupNode(1);
        size = 1;
    }

    public Molecule(Molecule molecule) {
        genotype = molecule.genotype.clone();
        size = molecule.size;
        x = molecule.x;
        fitness = molecule.fitness;
    }

    public Molecule(FunctionalGroupNode root) {
        genotype = root;
        size = 1 + size(genotype);
    }

    public int size(FunctionalGroupNode root) {
        int s = 0;
        if (root != null)
            s = root.countTotalGroups();
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

    @Override
    public Molecule clone() {
        return new Molecule(this);
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

    public String toString() {
        String show = "";
        ArrayList<FunctionalGroupNode> a = getArray();
        for (int i = 0; i < a.size(); i++) {
            show += CamdRunner.CONTRIBUTION_GROUPS.findGroupName(a.get(i).getRootNode()) + "-";
        }
        return show;
    }

    public FunctionalGroupNode getGroupAt(int i) {
        return getArray().get(i);
    }

    private ArrayList<FunctionalGroupNode> getArray() {
        ArrayList<FunctionalGroupNode> array = new ArrayList<>();
        return getArray(genotype, array);
    }

    private ArrayList<FunctionalGroupNode> getArray(FunctionalGroupNode functionalGroupNode, ArrayList<FunctionalGroupNode> array) {
        if (functionalGroupNode != null) {
            array.add(functionalGroupNode);
            if (functionalGroupNode.countSubgroups() > 0)
                for (int i = 0; i < functionalGroupNode.countSubgroups(); i++)
                    getArray(functionalGroupNode.getGroupAt(i), array);
        }
        return array;
    }

    public MoleculeGroups getGroupArray() {
        ArrayList<FunctionalGroupNode> groupsNodes = getArray();
        int n = groupsNodes.size();
        int[] groups = new int[n];
        for (int i = 0; i < n; i++) {
            groups[i] = groupsNodes.get(i).getRootNode();
        }
        MoleculeGroups moleculeGroups = new MoleculeGroups(groups);
        return moleculeGroups;
    }

    public ArrayList<Integer> get2OrderGroupArray() {
        ArrayList<Integer> secondOrderCode = new ArrayList<>();
        secOrderContribution(genotype, secondOrderCode);
        return secondOrderCode;
    }

    private void secOrderContribution(FunctionalGroupNode aRootFunctionalGroupNode, ArrayList<Integer> secondOrderCode) {
        if (aRootFunctionalGroupNode != null)
            identifySecondOrderGroups(aRootFunctionalGroupNode, secondOrderCode);

        if (aRootFunctionalGroupNode.countSubgroups() < 1)
            return;

        for (int i = 0; i < aRootFunctionalGroupNode.countSubgroups(); i++) {
            FunctionalGroupNode leaf = aRootFunctionalGroupNode.getGroupAt(i);
            secOrderContribution(leaf, secondOrderCode);
        }
    }

    private void identifySecondOrderGroups(FunctionalGroupNode root, ArrayList<Integer> secondOrderCode) {
        ArrayList<String[]> secondGroup = CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderGroupCase(root.getRootNode());
        //	System.out.println("DimensionArray: "+s.size());
        int dim = root.countSubgroups();
        int a[] = leavesToVector(root);

        if (secondGroup.size() < 1)
            return;

        for (int i = 0; i < dim; i++) {
            int c = 0;
            int codeColumn = (int) Double.parseDouble(secondGroup.get(c)[2]);
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
                    for (int r = 0; r < a.length; r++)
                        b[r] = r == i ? 0 : a[r];
                    //	System.out.println("prueba7");
                    //if(>0){ //no more groups bond
                    int restric = (int) Double.parseDouble(secondGroup.get(c)[4]);
                    int z = 3;
                    int caseArray = 1;
                    while (caseArray > 0 && z < secondGroup.get(c).length - 2) {
                        //   System.out.println("prueba8");
                        caseArray = (int) Double.parseDouble(secondGroup.get(c)[z]); //revisar esto para ver si puede se mas r�pido
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
                            if (root.getGroupAt(p).getRootNode() != caseOH[0])
                                continue;
                            int[] tempCond2 = new int[1];
                            tempCond2[0] = caseOH[1];
                            flat = sameVector(caseOH, tempCond2);
                            //		System.out.println("prueba5");
                        }
                        if (flat) {
                            //	System.out.println("caso: "+(int)Double.parseDouble(s.get(c)[0]));
                            secondOrderCode.add((int) Double.parseDouble(secondGroup.get(c)[0]));
                            secondGroup.remove(c);
                        }
                    }
                }
                c++;
                codeColumn = secondGroup.size() <= c ? 1000 : (int) Double.parseDouble(secondGroup.get(c)[2]);
                //	System.out.println("C: "+c);
                //System.out.println("Size: "+s.size());
                //System.out.println("Ai"+a[i]);
            }
        }

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
        return n == 0;
    }

    private int[] leavesToVector(FunctionalGroupNode root) {
        int dim = root.countSubgroups();
        int[] a = new int[dim];
        //System.out.println("prueba9");
        for (int i = 0; i < dim; i++)
            a[i] = root.getGroupAt(i).getRootNode();
        return a;
    }
}
