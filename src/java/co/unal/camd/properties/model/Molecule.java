package co.unal.camd.properties.model;

import co.unal.camd.properties.parameters.unifac.SecondOrderContributionData;
import co.unal.camd.view.CamdRunner;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.swing.event.TreeModelListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


/**
 * this create ramdom molecules
 *
 * @author Nicol�s Moreno
 */
@Data
@Accessors(chain = true)
public class Molecule {

    private ContributionGroupNode genotype;
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

    public Molecule(Molecule molecule) {
        genotype = molecule.genotype.clone();
        size = molecule.size;
        x = molecule.x;
        fitness = molecule.fitness;
    }

    public Molecule(ContributionGroupNode root) {
        genotype = root;
        size = 1 + size(genotype);
    }

    public int size(ContributionGroupNode root) {
        int s = 0;
        if (root != null)
            s = root.countTotalGroups();
        return s;
    }

    private void chemicalFormula(ContributionGroupNode root, String show) {
        if (root == null)
            return;
        show = show + root.toString() + " - ";
        for (int i = 0; i < root.getSubGroups().size(); i++)
            chemicalFormula(root.getGroupAt(i), show);
    }

    /**
     * the molecule made
     *
     * @return
     */
    public ContributionGroupNode getMoleculeByRootGroup() {
        return genotype;
    }


    public int getTotalGroups() {
        return size;
    }

    @Override
    public String toString() {
        String show = "";
        ArrayList<ContributionGroupNode> a = getArray();
        for (int i = 0; i < a.size(); i++) {
            if (i > 0)
                show += "-";
            show += CamdRunner.CONTRIBUTION_GROUPS.findGroupName(a.get(i).getGroupCode());
        }
        return show;
    }

    public ContributionGroupNode getGroupAt(int i) {
        return getArray().get(i);
    }

    private ArrayList<ContributionGroupNode> getArray() {
        ArrayList<ContributionGroupNode> array = new ArrayList<>();
        return getArray(genotype, array);
    }

    private ArrayList<ContributionGroupNode> getArray(ContributionGroupNode functionalGroupNode, ArrayList<ContributionGroupNode> array) {
        if (functionalGroupNode != null) {
            array.add(functionalGroupNode);
            if (functionalGroupNode.countSubgroups() > 0)
                for (int i = 0; i < functionalGroupNode.countSubgroups(); i++)
                    getArray(functionalGroupNode.getGroupAt(i), array);
        }
        return array;
    }

    public MoleculeGroups getGroupsArray() {
        ArrayList<ContributionGroupNode> groupsNodes = getArray();
        int n = groupsNodes.size();
        int[] groups = new int[n];
        for (int i = 0; i < n; i++) {
            groups[i] = groupsNodes.get(i).getGroupCode();
        }
        MoleculeGroups moleculeGroups = new MoleculeGroups(groups);
        return moleculeGroups;
    }

    public ArrayList<Integer> findSecondOrderGroupArray() {
        ArrayList<Integer> secondOrderCodes = new ArrayList<>();
        secOrderContribution(genotype, secondOrderCodes);
        return secondOrderCodes;
    }

    private void secOrderContribution(ContributionGroupNode aRootFunctionalGroupNode, ArrayList<Integer> secondOrderCodes) {
        if (aRootFunctionalGroupNode != null)
            identifySecondOrderGroups(aRootFunctionalGroupNode, secondOrderCodes);

        if (aRootFunctionalGroupNode.countSubgroups() < 1)
            return;

        for (int i = 0; i < aRootFunctionalGroupNode.countSubgroups(); i++) {
            ContributionGroupNode leaf = aRootFunctionalGroupNode.getGroupAt(i);
            secOrderContribution(leaf, secondOrderCodes);
        }
    }

    //    TODO >  IMPROVE
    private void identifySecondOrderGroups(ContributionGroupNode root, ArrayList<Integer> secondOrderCodes) {
        int rootGroupCode = root.getGroupCode();
        List<SecondOrderContributionData> secondOrderContributions = CamdRunner.CONTRIBUTION_GROUPS.getSecondOrderContributionsRoots().get(rootGroupCode);
        if (secondOrderContributions == null)
            return;
        int dim = root.getSubGroups().size();
        int leaves[] = leavesToVector(root);

        //        System.out.println(String.format("***** Second order alt START ******"));
        for (int i = 0; i < dim; i++) {
            final int iFinal = i;
            secondOrderContributions.stream()
                    .filter(secondOrderContribution -> !secondOrderCodes.contains(secondOrderContribution.getGroupsCase()))
                    .forEach(secondOrderContribution -> secondOrderContribution.getGroupsConfigurations().stream()
                            .filter(groupConfiguration -> groupConfiguration[0] == rootGroupCode && leaves[iFinal] == groupConfiguration[1])
                            .forEach(groupConfiguration ->
                                    {
                                        if (secondOrderCodes.contains(secondOrderContribution.getGroupsCase()))
                                            return;

                                        int[] caseOH = new int[2];
                                        int[] tempCond = new int[3]; //this is the array of groups (less central group and second) that construct de second order groups

                                        //                                            System.out.println("Alt enter " + leaves[iFinal] + " with " + secondOrderContribution.getGroupsCase() + "-" + groupConfiguration[1]);
                                        int[] b = new int[leaves.length];

                                        for (int r = 0; r < leaves.length; r++)
                                            b[r] = r == iFinal ? 0 : leaves[r];

                                        for (int z = 1; z < groupConfiguration.length - 1 && z < tempCond.length; z++)
                                            tempCond[z] = groupConfiguration[z + 1]; //revisar esto para ver si puede se mas r�pido

                                        boolean flat = true;
                                        if (secondOrderContribution.getGroupsCase() == 30) {
                                            caseOH[0] = tempCond[0];
                                            caseOH[1] = tempCond[1];
                                            tempCond[2] = 0;
                                        }

                                        if (sameVector(tempCond, b)) { //if the leaves are the same that sec order groups, add the code of SOG
                                            for (int p = 0; p < dim; p++) {
                                                if (root.getGroupAt(p).getGroupCode() != caseOH[0])
                                                    continue;
                                                int[] tempCond2 = new int[1];
                                                tempCond2[0] = caseOH[1];
                                                flat = sameVector(caseOH, tempCond2);
                                                //		System.out.println("prueba5");
                                            }
                                            if (flat) {
                                                //	System.out.println("caso: "+(int)Double.parseDouble(s.get(c)[0]));
                                                //TODO Check if second order groups repetition are being ignored
                                                secondOrderCodes.add(secondOrderContribution.getGroupsCase());
                                                //                                            secondGroup.remove(c);
                                            }
                                        }
                                    }
                            )
                    );
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

    private int[] leavesToVector(ContributionGroupNode root) {
        int dim = root.getSubGroups().size();
        int[] a = new int[dim];
        //System.out.println("prueba9");
        for (int i = 0; i < dim; i++)
            a[i] = root.getGroupAt(i).getGroupCode();
        return a;
    }

    /**
     * this method create the molec until valence =0 (first restriction) or
     * the number of molecules is equal to maxNumof Groups allow
     */
    @Override
    public Molecule clone() {
        return new Molecule(this);
    }
}
