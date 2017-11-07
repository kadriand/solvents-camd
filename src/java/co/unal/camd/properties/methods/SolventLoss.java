package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;

import java.util.ArrayList;

public class SolventLoss {

    private double temp;
    private ArrayList<MoleculeGroups> solventUandD;

    ////////////////////////////////////////Solvent loss/////////////////////////////////////////////////////////
    public SolventLoss(double _temp, ArrayList<MoleculeGroups> targetAndDesignedSolvents) {
        solventUandD = new ArrayList<>(targetAndDesignedSolvents);
        temp = _temp;
    }

    public double getMethodResult() {
        UnifacEstimator unifacEstimator = new UnifacEstimator(solventUandD);
        double unifacGamma = unifacEstimator.solve(temp);
        double pmSolventUser = MolecularWeight.compute((solventUandD.get(1)));
        double pmSolventDesign = MolecularWeight.compute((solventUandD.get(0)));
        return 1 / unifacGamma * pmSolventDesign / pmSolventUser;
    }

}
