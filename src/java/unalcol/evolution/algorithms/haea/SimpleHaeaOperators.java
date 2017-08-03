package unalcol.evolution.algorithms.haea;

import java.util.Enumeration;
import java.util.Vector;

import unalcol.evolution.Operator;
import unalcol.random.*;

/**
 * <p>Title: </p>
 * <p>
 * <p>Description: </p>
 * <p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SimpleHaeaOperators extends HaeaOperators {

    protected Operator[] opers;

    public SimpleHaeaOperators(Operator[] opers) {
        this.opers = opers;
    }

    public int numberOfOperatorsPerIndividual() {
        return opers.length;
    }

    public int numberOfOperators() {
        return opers.length;
    }


    public Operator getOperator(int indIndex, int operIndex) {
        return opers[operIndex];
    }
}
