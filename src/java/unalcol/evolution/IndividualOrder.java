package unalcol.evolution;

import unalcol.abs.Order;

/**
 * <p>Title: IndividualOrder</p>
 * <p>Description: It verify the Individual order</p>
 * <p>Copyright:    Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class IndividualOrder extends Order {
    /**
     * Determines if the object is less than (in some order) the given object
     *
     * @param x Comparison object
     * @return true if the object is less than the given object x. false in other case
     */
    public boolean lessThan(Object x, Object y) {
        return (((Individual) x).getFitness() < ((Individual) y).getFitness());
    }

    /**
     * Determines if the object is equal to the given object
     *
     * @param x Comparison object
     * @return true if the object is equal to the given object x. false in other case
     */
    public boolean equalTo(Object x, Object y) {
        return (((Individual) x).getFitness() == ((Individual) y).getFitness());
    }
}
