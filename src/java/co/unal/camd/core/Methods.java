package co.unal.camd.core;

import co.unal.camd.control.parameters.ContributionParametersManager;

import java.util.ArrayList;

public abstract class Methods {
    public boolean can_be_done = true;

    public abstract double getMethodResult(ArrayList<GroupArray> molecules, int principal, double temp, ContributionParametersManager aGC);

    public abstract double getMethodResult(Molecules molecules);

    public abstract double getMethodResult(Molecules molecules, double temp);
}

