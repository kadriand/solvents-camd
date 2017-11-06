package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

public class MoleculesFactory {

    private int maxGroupsSize;

    public MoleculesFactory(int maxGroupsSize) {
        this.maxGroupsSize = maxGroupsSize;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public Molecule buildNewMolecule() {
        int dim;
        int groupCode;
        ArrayList<ContributionGroupNode> functionalGroupNodes = new ArrayList<>();
        int min = maxGroupsSize / 2;
        double aProba = 0.4;
        boolean opt = probabilityFunction(functionalGroupNodes, aProba);
        for (int i = 0; i <= random(min); i++) {
            groupCode = MoleculeOperations.getNewGroupCode(1, opt);
            functionalGroupNodes.add(new ContributionGroupNode(groupCode));
            //System.out.println("Se agrego: "+CONTRIBUTION_GROUPS.findGroupName(auxiliar.get(i).getCode())+" "+CONTRIBUTION_GROUPS.getPrincipalGroupCode(auxiliar.get(i).getCode()));
            if (probabilityFunction(functionalGroupNodes, aProba)) {
                Integer mainGroupCode = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(groupCode).getMainGroup().getCode();
                opt = mainGroupCode <= 1;
            } else
                opt = false;
        }
        dim = functionalGroupNodes.size();
        return newMoleculeFromBaseGroups(functionalGroupNodes, dim);
    }

    /**
     * Creates a new genome of a given genotype
     *
     * @return Object The new genome
     */
    public Molecule buildMolecule(ArrayList<ContributionGroupNode> functionalGroupNodes) {
        int dim = functionalGroupNodes.size();
        return newMoleculeFromBaseGroups(functionalGroupNodes, dim);
    }

    private boolean probabilityFunction(ArrayList<ContributionGroupNode> root, double aProba) {
        double random = Math.random();
        /*TODO CHECK WHY THE 1ST PART*/
        boolean isFunctional = Restrictions.canBeFunctional(root);
        return random <= aProba && isFunctional;
    }

    /**
     * Builds a new molecule from some pre-existent groups
     *
     * @param leaves
     * @param dim
     * @return
     */
    private Molecule newMoleculeFromBaseGroups(ArrayList<ContributionGroupNode> leaves, int dim) {
        ContributionGroupNode group = createRandomGroups(leaves.size() + 1, dim, leaves);
        //System.out.println("GroupNew: "+contributionGroups.findGroupName(gr.getCode()));
        boolean next;

        int groupValence = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(group.getGroupCode()).getValence();

        if (leaves.size() <= groupValence) { // if is the last group
            next = true;
            while (next) {
                //System.out.println("tamaño: "+leaves.size());
                ContributionGroupNode temporal = leaves.get(0);
                //	System.out.println("Grupo: "+gr.getCode());
                Restrictions.mayBeFuncFuncOrOH(temporal, group, true);
                leaves.remove(0);
                next = leaves.size() != 0;
            }

            int valence = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(group.getGroupCode()).getValence();
            if (valence > group.countSubgroups()) {
                int m = group.countSubgroups();
                for (int i = 0; i < valence - m; i++) {  // en esta parte se corrigió el error de la valencia incompleta
                    ContributionGroupNode randomGroup = new ContributionGroupNode(MoleculeOperations.getNewGroupCode(1, probabilityFunction(leaves, 0.4)));
                    //	System.out.println("SUb: "+aG.getCode());
                    Restrictions.mayBeFuncFuncOrOH(randomGroup, group, false);
                    dim = dim + 1;
                }
            }
            leaves.add(group);
        } else if (leaves.size() > groupValence) {
            //System.out.println("valence New"+contributionGroups.findGroupValence(gr.getCode()));
            next = true;
            while (next) {
                if (leaves.size() > 0) {
                    ContributionGroupNode temporal = leaves.get(0);
                    Restrictions.mayBeFuncFuncOrOH(temporal, group, true);
                    leaves.remove(0);
                    //System.out.println("Valencia hojas: "+(contributionGroups.findGroupValence(gr.getCode())+" hijos: "+gr.countSubgroups()));
                    if (CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(group.getGroupCode()).getValence() - 1 == group.countSubgroups())
                        next = false; //
                } else {
                    next = false;
                }
            }
            leaves.add(group);
            dim = dim + 1;

            if (leaves.size() >= 1)
                newMoleculeFromBaseGroups(leaves, dim);
        }
        //if(leaves.size()==1 && contributionGroups.findGroupValence(leaves.get(0))-leaves.get(0).countSubgroups()==0){
        //return new Molecules(leaves.get(0));
        //}
        return new Molecule(leaves.get(0));
    }

    /**
     * Create random groups
     *
     * @return
     */
    private ContributionGroupNode createRandomGroups(int sizeAux, int dim, ArrayList<ContributionGroupNode> leaves) {
        int valence = randomProbability(sizeAux, dim);
        //System.out.println("The valence was"+Valence);
        ContributionGroupNode newG = new ContributionGroupNode(MoleculeOperations.getNewGroupCode(valence, probabilityFunction(leaves, 0.4)));
        return newG;
    }

    /**
     * Generate random numbers between 0-3 (valence Group), the probability change with
     * the amount of groups in the molecule
     *
     * @return
     */
    private int randomProbability(int sizeAux, int dim) {
        int valence = 0;
        int x = maxGroupsSize - dim; // groups to complete the molec
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

    public static int getSize(ContributionGroupNode root) {
        int counter = 0;
        if (root != null) {
            counter = counter + root.getSubGroups().size();
            for (int i = 0; i < root.getSubGroups().size(); i++) {
                getSize(root.getGroupAt(i));
            }
        }
        return counter;
    }

}
