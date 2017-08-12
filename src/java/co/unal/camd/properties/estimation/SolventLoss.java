package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.ArrayList;


public class SolventLoss {

    private Methods aMethods;
    private double temp;
    private ArrayList<GroupArray> solventUandD;
    private ContributionParametersManager aGC;

    ////////////////////////////////////////Solvent loss/////////////////////////////////////////////////////////
    public SolventLoss(double _temp, ContributionParametersManager _aGC, ArrayList<GroupArray> solventObjectAndSolventOfUser) {
        solventUandD = new ArrayList<GroupArray>(solventObjectAndSolventOfUser);
        temp = _temp;
        aGC = _aGC;
    }

    public double getMethodResult() {
        aMethods = new Unifac();
        double unifac = aMethods.getMethodResult(solventUandD, 0, temp, aGC);
        double pmSolventUser = PM.getMethodResult((solventUandD.get(1)), aGC);
        double pmSolventDesign = PM.getMethodResult((solventUandD.get(0)), aGC);
        return 1 / unifac * pmSolventDesign / pmSolventUser;
    }

}