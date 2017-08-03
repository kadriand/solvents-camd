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
 * <p>Title: LinearXOver</p>
 * <p>Description:Applies a linear crossover. In this case the alpha is unique
 * for each component, it use two alpha, one for each SparseRealVector</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class LinearXOver extends ArityTwo<SparseRealVector> {
    /**
     * default constructor
     *
     * @param _environment The environment
     */
    public LinearXOver(Environment _environment) {
        super(_environment);
    }


    /**
     * Creates a linear crossover operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public LinearXOver(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }


    /**
     * Apply the 2-ary genetic operator over the individual genomes
     *
     * @param one First Individuals genome to be modified by the genetic operator
     * @param two Second Individuals genome to be modified by the genetic operator
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

        double alpha = Generator.random.next();
        double alpha_1 = Generator.random.next();
        double neg_alpha = 1.0 - alpha;
        double neg_alpha_1 = 1.0 - alpha_1;

        while (iter1.hasMoreElements() && iter2.hasMoreElements()) {
            if (ax == null) {
                ax = iter1.nextElement();
            }
            if (by == null) {
                by = iter2.nextElement();
            }
            if (ax.getIndex() == by.getIndex()) {
                tx = ax.value;
                ty = by.value;
                ax.value = alpha * tx + neg_alpha * ty;
                by.value = alpha_1 * tx + neg_alpha_1 * ty;
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
                    by.value = neg_alpha_1 * ty;
                    vx.add(new SparseReal(by.getIndex(), neg_alpha * ty));
                    vy.add(by);
                    by = null;
                }
            }
        }

        if (ax != null) {
            tx = ax.value;
            ax.value = alpha * tx;
            vx.add(ax);
            vy.add(new SparseReal(ax.getIndex(), alpha_1 * tx));
        }

        if (by != null) {
            ty = by.value;
            by.value = neg_alpha_1 * ty;
            vx.add(new SparseReal(by.getIndex(), neg_alpha * ty));
            vy.add(by);
        }

        while (iter1.hasMoreElements()) {
            ax = iter1.nextElement();
            tx = ax.value;
            ax.value = alpha * tx;
            by = new SparseReal(ax.getIndex(), alpha_1 * tx);
            vx.add(ax);
            vy.add(by);
        }

        while (iter2.hasMoreElements()) {
            by = iter2.nextElement();
            ty = by.value;
            ax = new SparseReal(by.getIndex(), neg_alpha * ty);
            by.value = neg_alpha_1 * ty;
            vx.add(ax);
            vy.add(by);
        }

        Vector<SparseRealVector> v = new Vector<SparseRealVector>();
        v.add(new SparseRealVector(one.dimension(), vx));
        v.add(new SparseRealVector(two.dimension(), vy));
        return v;
    }
}
