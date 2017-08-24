package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.Molecule;

import java.util.ArrayList;

public class MoleculesFactory {
    protected int maxGroupsSize;
    protected ContributionParametersManager parametersManager;

    public MoleculesFactory(int maxGroupsSize, ContributionParametersManager contributionParametersManager) {
        this.maxGroupsSize = maxGroupsSize;
        this.parametersManager = contributionParametersManager;
    }

    /**
     * Creates a new genome of the given genotype
     *
     * @return Object The new genome
     */
    public Molecule buildNewMolecule() {
        int dim = 0;
        int refCode = 0;
        ArrayList<FunctionalGroupNode> functionalGroupNodes = new ArrayList<FunctionalGroupNode>();
        int min = (maxGroupsSize) / 2;
        double aProba = 0.4;
        boolean opt = probabilityFunction(functionalGroupNodes, aProba);
        for (int i = 0; i < random(min) + 1; i++) {
            refCode = findNewRefCode(1, parametersManager, opt);
            functionalGroupNodes.add(new FunctionalGroupNode(refCode));
            //System.out.println("Se agrego: "+parametersManager.getName(auxiliar.get(i).getRootNode())+" "+parametersManager.getPrincipalGroupCode(auxiliar.get(i).getRootNode()));
            if (probabilityFunction(functionalGroupNodes, aProba) == true) {
                parametersManager.getCodeOfRowBNameOrRefCode(refCode);
//                opt=parametersManager.getCodeOfRow() <= 1;
                if (parametersManager.getCodeOfRow() > 1) {
                    opt = false;
                } else {
                    opt = true;
                }
            } else {
                opt = false;
            }
        }
        dim = functionalGroupNodes.size();
        return moleculeFromGroups(functionalGroupNodes, dim);
    }

    private static int findNewRefCode(int valence, ContributionParametersManager aGC, boolean functional) {
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

    public boolean probabilityFunction(ArrayList<FunctionalGroupNode> root, double aProba) {
        boolean show = false;
        double random = Math.random();
        if (random <= aProba && Restrictions.canBeFunctional(root, parametersManager)) {
            show = true;
        } else {
            show = false;
        }
        return show;
    }


    public Molecule moleculeFromGroups(ArrayList<FunctionalGroupNode> leaves, int dim) {
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
                    FunctionalGroupNode aG = new FunctionalGroupNode(findNewRefCode(1, parametersManager, probabilityFunction(leaves, 0.4)));
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
                moleculeFromGroups(leaves, dim);
            }
        }
        //if(leaves.size()==1 && parametersManager.getValence(leaves.get(0))-leaves.get(0).getGroupsCount()==0){
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
        int Valence = randomProbability(sizeAux, dim);
        //System.out.println("The valence was"+Valence);
        FunctionalGroupNode newG = new FunctionalGroupNode(findNewRefCode(Valence, parametersManager, probabilityFunction(leaves, 0.4)));
        return newG;
    }

    /**
     * Generate random numbers between 0-3 (valence Group), the probability change with
     * the amount of groups in the molecule
     *
     * @return
     */
    public int randomProbability(int sizeAux, int dim) {
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
