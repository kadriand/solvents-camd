package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;

import java.util.ArrayList;


public class SolventLoss {

    private UnifacMethod aMethods;
    private double temp;
    private ArrayList<MoleculeGroups> solventUandD;

    ////////////////////////////////////////Solvent loss/////////////////////////////////////////////////////////
    public SolventLoss(double _temp, ArrayList<MoleculeGroups> targetAndDesignedSolvents) {
        solventUandD = new ArrayList<>(targetAndDesignedSolvents);
        temp = _temp;
    }

    public double getMethodResult() {
        aMethods = new UnifacMethod();
        double unifac = aMethods.getMethodResult(solventUandD, 0, temp);
        double pmSolventUser = PM.getMethodResult((solventUandD.get(1)));
        double pmSolventDesign = PM.getMethodResult((solventUandD.get(0)));
        return 1 / unifac * pmSolventDesign / pmSolventUser;
    }

}
