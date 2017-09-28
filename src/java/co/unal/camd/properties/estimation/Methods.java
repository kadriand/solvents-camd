package co.unal.camd.properties.estimation;

import java.util.ArrayList;

public abstract class Methods {

    public boolean canBeDone = true;

    public abstract double getMethodResult(ArrayList<MoleculeGroups> molecules, int principal, double temp);

    public abstract double getMethodResult(Molecule molecules);

    public abstract double getMethodResult(Molecule molecules, double temp);
}

