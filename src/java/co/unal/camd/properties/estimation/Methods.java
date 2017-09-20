package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;

import java.util.ArrayList;

public abstract class Methods {

    public boolean canBeDone = true;

    public abstract double getMethodResult(ArrayList<GroupArray> molecules, int principal, double temp, ContributionGroupsManager aGC);

    public abstract double getMethodResult(Molecule molecules);

    public abstract double getMethodResult(Molecule molecules, double temp);
}

