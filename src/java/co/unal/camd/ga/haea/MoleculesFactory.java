package co.unal.camd.ga.haea;

import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecule;
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
        ArrayList<FunctionalGroupNode> functionalGroupNodes = new ArrayList<>();
        int min = maxGroupsSize / 2;
        double aProba = 0.4;
        boolean opt = probabilityFunction(functionalGroupNodes, aProba);
        for (int i = 0; i <= random(min); i++) {
            groupCode = findNewGroupCode(1, opt);
            functionalGroupNodes.add(new FunctionalGroupNode(groupCode));
            //System.out.println("Se agrego: "+CONTRIBUTION_GROUPS.findGroupName(auxiliar.get(i).getRootNode())+" "+CONTRIBUTION_GROUPS.getPrincipalGroupCode(auxiliar.get(i).getRootNode()));
            if (probabilityFunction(functionalGroupNodes, aProba)) {
                CamdRunner.CONTRIBUTION_GROUPS.resolveValence(groupCode);
                opt = CamdRunner.CONTRIBUTION_GROUPS.getCodeOfRow() <= 1;
            } else {
                opt = false;
            }
        }
        dim = functionalGroupNodes.size();
        return newMoleculeFromBaseGroups(functionalGroupNodes, dim);
    }

    /**
     * Creates a new genome of a given genotype
     *
     * @return Object The new genome
     */
    public Molecule buildMolecule(ArrayList<FunctionalGroupNode> functionalGroupNodes) {
        int dim = functionalGroupNodes.size();
        return newMoleculeFromBaseGroups(functionalGroupNodes, dim);
    }

    private int findNewGroupCode(int valence, boolean functional) {
        int codeOfRow = 0;
        int refCode = 0;
        if (functional) {
            double proba = Math.random();
            double p = 0;
            int n = CamdRunner.CONTRIBUTION_GROUPS.getTotalNumberOfGroupOfValence(valence);
            while (proba <= 1 - p) {
                codeOfRow = (int) (Math.random() * n) + 1;//random row to choose the group
                p = CamdRunner.CONTRIBUTION_GROUPS.getProbability(valence, codeOfRow);
                //	System.out.println("pruebaaa");
            }
            //	/	System.out.println("pruebaa2");
            refCode = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(valence, codeOfRow);
        } else {
            codeOfRow = 1;//the code of the firs group (Structural group)
            refCode = CamdRunner.CONTRIBUTION_GROUPS.findGroupCode(valence, codeOfRow);
        }
        return refCode;
    }

    private boolean probabilityFunction(ArrayList<FunctionalGroupNode> root, double aProba) {
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
    private Molecule newMoleculeFromBaseGroups(ArrayList<FunctionalGroupNode> leaves, int dim) {
        FunctionalGroupNode group = createRandomGroups(leaves.size() + 1, dim, leaves);
        //System.out.println("GroupNew: "+contributionGroups.findGroupName(gr.getRootNode()));
        boolean next;

        if (leaves.size() <= CamdRunner.CONTRIBUTION_GROUPS.findGroupValence(group.getRootNode())) { // if is the last group
            next = true;
            while (next) {
                //System.out.println("tamaño: "+leaves.size());
                FunctionalGroupNode temporal = leaves.get(0);
                //	System.out.println("Grupo: "+gr.getRootNode());
                Restrictions.mayBeFuncFuncOrOH(temporal, group, true);
                leaves.remove(0);
                next = leaves.size() != 0;
            }
            if (CamdRunner.CONTRIBUTION_GROUPS.findGroupValence(group.getRootNode()) > group.countSubgroups()) {
                int m = group.countSubgroups();
                for (int i = 0; i < CamdRunner.CONTRIBUTION_GROUPS.findGroupValence(group.getRootNode()) - m; i++) {  // en esta parte se corrigió el error de la valencia incompleta
                    FunctionalGroupNode aG = new FunctionalGroupNode(findNewGroupCode(1, probabilityFunction(leaves, 0.4)));
                    //	System.out.println("SUb: "+aG.getRootNode());
                    Restrictions.mayBeFuncFuncOrOH(aG, group, false);
                    dim = dim + 1;
                }
            }
            leaves.add(group);
            dim = dim + 1;
        } else if (leaves.size() > CamdRunner.CONTRIBUTION_GROUPS.findGroupValence(group.getRootNode())) {
            //System.out.println("valence New"+contributionGroups.findGroupValence(gr.getRootNode()));
            next = true;
            while (next) {
                if (leaves.size() > 0) {
                    FunctionalGroupNode temporal = leaves.get(0);
                    Restrictions.mayBeFuncFuncOrOH(temporal, group, true);
                    leaves.remove(0);
                    //System.out.println("Valencia hojas: "+(contributionGroups.findGroupValence(gr.getRootNode())+" hijos: "+gr.countSubgroups()));
                    if (CamdRunner.CONTRIBUTION_GROUPS.findGroupValence(group.getRootNode()) - 1 == group.countSubgroups())
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
    private FunctionalGroupNode createRandomGroups(int sizeAux, int dim, ArrayList<FunctionalGroupNode> leaves) {
        int valence = randomProbability(sizeAux, dim);
        //System.out.println("The valence was"+Valence);
        FunctionalGroupNode newG = new FunctionalGroupNode(findNewGroupCode(valence, probabilityFunction(leaves, 0.4)));
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

}
