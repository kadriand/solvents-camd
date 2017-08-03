package unalcol.evolution.real.sparse;

import java.util.Enumeration;
import java.util.Vector;

import unalcol.evolution.*;
import unalcol.evolution.Selection;
import unalcol.evolution.operators.ArityTwo;
import unalcol.math.sparse.SparseReal;
import unalcol.math.sparse.SparseRealVector;
import unalcol.random.*;

/**
 * <p>Title: LinearXOverPerDimension</p>
 * <p>Description:Applies a linear crossover per dimension. in this case each alpha
 * is different for each component</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class LinearXOverPerDimension extends ArityTwo<SparseRealVector> {

    /**
     * Defalut constructor
     *
     * @param _environment The environment
     */
    public LinearXOverPerDimension(Environment _environment) {
        super(_environment);
    }


    /**
     * Creates a linear crossover per dimension operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public LinearXOverPerDimension(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }


    /**
     * Apply the 2-ary genetic operator over the individual genomes
     *
     * @param c1 First Individuals genome to be modified by the genetic operator
     * @param c2 Second Individuals genome to be modified by the genetic operator
     * @return extra information of the genetic operator
     */
    public Vector<SparseRealVector> apply(SparseRealVector one, SparseRealVector two) {

        double tx, ty;
        Enumeration<SparseReal> iter1 = one.elements();
        Enumeration<SparseReal> iter2 = two.elements();
        Vector<SparseReal> vx = new Vector<SparseReal>();
        Vector<SparseReal> vy = new Vector<SparseReal>();
        SparseReal ax = null;
        SparseReal by = null;
        double alpha;
        double alpha_1;
        while (iter1.hasMoreElements() && iter2.hasMoreElements()) {
            if (ax == null) {
                ax = iter1.nextElement();
            }
            if (by == null) {
                by = iter2.nextElement();
            }
            alpha = Generator.random.next();
            alpha_1 = 1.0 - alpha;
            if (ax.getIndex() == by.getIndex()) {
                tx = ax.value;
                ty = by.value;
                ax.value = alpha * tx + alpha_1 * ty;
                by.value = alpha_1 * tx + alpha * ty;
                vx.add(ax);
                vy.add(by);
                ax = null;
                by = null;
            } else {
                if (ax.getIndex() < by.getIndex()) {
                    tx = ax.value;
                    ax.value = alpha * tx;
                    vx.add(ax);
                    vy.add(new SparseReal(ax.getIndex(), alpha_1 * tx));
                    ax = null;
                } else {
                    ty = by.value;
                    by.value = alpha * ty;
                    vx.add(new SparseReal(by.getIndex(), alpha_1 * ty));
                    vy.add(by);
                    by = null;
                }
            }
        }

        if (ax != null) {
            alpha = Generator.random.next();
            alpha_1 = 1.0 - alpha;
            tx = ax.value;
            ax.value = alpha * tx;
            vx.add(ax);
            vy.add(new SparseReal(ax.getIndex(), alpha_1 * tx));
        }

        if (by != null) {
            alpha = Generator.random.next();
            alpha_1 = 1.0 - alpha;
            ty = by.value;
            by.value = alpha * ty;
            vx.add(new SparseReal(by.getIndex(), alpha_1 * ty));
            vy.add(by);
        }

        while (iter1.hasMoreElements()) {
            alpha = Generator.random.next();
            alpha_1 = 1.0 - alpha;
            ax = iter1.nextElement();
            tx = ax.value;
            ax.value = alpha * tx;
            by = new SparseReal(ax.getIndex(), alpha_1 * tx);
            vx.add(ax);
            vy.add(by);
        }

        while (iter2.hasMoreElements()) {
            alpha = Generator.random.next();
            alpha_1 = 1.0 - alpha;
            by = iter2.nextElement();
            ty = by.value;
            by.value = alpha * ty;
            ax = new SparseReal(by.getIndex(), alpha_1 * ty);
            vx.add(ax);
            vy.add(by);
        }

        Vector<SparseRealVector> v = new Vector<SparseRealVector>();
        v.add(new SparseRealVector(one.dimension(), vx));
        v.add(new SparseRealVector(two.dimension(), vy));
        return v;
    }
}
