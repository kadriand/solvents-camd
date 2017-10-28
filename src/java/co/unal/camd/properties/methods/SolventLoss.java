package co.unal.camd.properties.methods;

import co.unal.camd.properties.model.MoleculeGroups;

import java.util.ArrayList;

public class SolventLoss {

    private ArrayList<MoleculeGroups> solventUandD;

    ////////////////////////////////////////Solvent loss/////////////////////////////////////////////////////////
    public SolventLoss(ArrayList<MoleculeGroups> targetAndDesignedSolvents) {
        solventUandD = new ArrayList<>(targetAndDesignedSolvents);
    }

    public double compute() {
        UnifacEstimator unifacEstimator = new UnifacEstimator(solventUandD);
        double unifacGamma = unifacEstimator.solve();
        double pmSolventUser = MolecularWeight.compute((solventUandD.get(1)));
        double pmSolventDesign = MolecularWeight.compute((solventUandD.get(0)));
        return 1 / unifacGamma * pmSolventDesign / pmSolventUser;
    }

}
