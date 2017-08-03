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
 * <p>Title: SimpleXOver</p>
 * <p>Description:Exchanges the last components of the first individual with
 * the last components of the second individual</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class SimpleXOver extends ArityTwo<SparseRealVector> {
    /**
     * Default constructor
     *
     * @param _environment The environment
     */
    public SimpleXOver(Environment _environment) {
        super(_environment);
    }


    /**
     * Creates a crossover  operation with the given selection strategy for
     * choosing the parents used by the crossover
     *
     * @param _selection   Selection mechanism for parent selection
     * @param _environment The environment
     */
    public SimpleXOver(Environment _environment, Selection _selection) {
        super(_environment, _selection);
    }

    /**
     * Apply the 2-ary genetic operator over the individual genomes
     *
     * @param x First Individuals genome to be modified by the genetic operator
     * @param y Second Individuals genome to be modified by the genetic operator
     * @return extra information of the genetic operator
     */
    public Vector<SparseRealVector> apply(SparseRealVector x, SparseRealVector y) {
        int xd = x.dimension();
        int yd = y.dimension();

        int min = Math.min(xd, yd);

        IntegerGenerator g = new IntegerGenerator(min - 1);
        int pos = g.next() + 1;
        Enumeration ix = x.elements();
        Enumeration iy = y.elements();
        int px = x.locate(new SparseReal(pos, 0.0));
        int py = y.locate(new SparseReal(pos, 0.0));

        Vector vx = new Vector();
        Vector vy = new Vector();

        for (int i = 0; i < px; i++) {
            vx.add(ix.nextElement());
        }

        for (int i = 0; i < py; i++) {
            vy.add(iy.nextElement());
        }

        while (ix.hasMoreElements()) {
            vy.add(ix.nextElement());
        }

        while (iy.hasMoreElements()) {
            vx.add(iy.nextElement());
        }

        Vector<SparseRealVector> v = new Vector<SparseRealVector>();
        v.add(new SparseRealVector(x.dimension(), vx));
        v.add(new SparseRealVector(y.dimension(), vy));
        return v;
    }
}
