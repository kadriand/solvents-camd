package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.FunctionalGroupNode;
import co.unal.camd.properties.model.Molecule;
import co.unal.camd.properties.model.MoleculeGroups;
import co.unal.camd.view.CamdRunner;

public class Density {
    private Molecule aMolecule;
    private double temperature;

    public Density(Molecule solvent, double temp) {
        temperature = temp;
        aMolecule = solvent;
    }


    public double getMethodResult() {
        double sum = 0;
        double a = 0;
        double b = 0;
        double c = 0;
        for (int i = 0; i < aMolecule.getGroupsArray().size(); i++) {
            /**
             * this are 6 exceptions to create density groups between unifac groups
             */
            if (isBond(aMolecule.getGroupAt(i), 10, 4)) {
                a = 39.37;
                b = -0.2721;
                c = 0.0002492;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else if (isBond(aMolecule.getGroupAt(i), 2, 14)) {
                a = 36.73;
                b = -0.07125;
                c = 0.0001406;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else if (isBond(aMolecule.getGroupAt(i), 3, 81)) {
                a = 14.26;
                b = -0.008187;
                c = 0;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else if (isBond(aMolecule.getGroupAt(i), 4, 82)) {
                a = -95.68;
                b = 0.5935;
                c = -0.0009479;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else if (isBond(aMolecule.getGroupAt(i), 3, 77)) {
                a = 38.23;
                b = -0.1121;
                c = 0.0001665;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else if (isBond(aMolecule.getGroupAt(i), 10, 77)) {
                a = 27.61;
                b = -0.02077;
                c = 0;
                sum = sum + (a + b * temperature + c * temperature * temperature);
            } else {
                for (int j = 0; j <= 3; j++) {
                    a = CamdRunner.CONTRIBUTION_GROUPS.getDensityConstants(aMolecule.getGroupAt(i).getRootNode())[j][0];
                    b = CamdRunner.CONTRIBUTION_GROUPS.getDensityConstants(aMolecule.getGroupAt(i).getRootNode())[j][1];
                    c = CamdRunner.CONTRIBUTION_GROUPS.getDensityConstants(aMolecule.getGroupAt(i).getRootNode())[j][2];
                    sum = sum + (a + b * temperature + c * temperature * temperature);
                    //System.out.println("a "+a);
                    //System.out.println("b "+b);
                    //System.out.println("c "+c);
                    //System.out.println("SUm"+sum);
                }
            }
        }
        //System.out.println(sum);
        MoleculeGroups gr = aMolecule.getGroupsArray();
        gr.optimize();
        //System.out.println("SUmaD"+sum);
        return PM.getMethodResult(gr) / sum;
    }

    public static boolean isBond(FunctionalGroupNode aGroup, int rootGroup, int leafGroup) {
        boolean show = false;

        if (aGroup.getRootNode() == rootGroup && aGroup.getGroupAt(0) != null) {
            for (int i = 0; i < aGroup.countSubgroups(); i++) {
                //System.out.println("grupo:"+aGroup.getRootNode());
                //System.out.println("Subgrupo:"+aGroup.getGroupAt(i).getRootNode());
                if (aGroup.getGroupAt(i).getRootNode() == leafGroup) {
                    show = true;
                }
            }
        }

        return show;
    }
}
