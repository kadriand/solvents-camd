package unalcol.evolution;

/**
 * <p>Title: Phenotype </p>
 * <p>Description: An individual Phenotype </p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class Phenotype<P, G> {
    /**
     * Environment of the Phenotype
     */
    protected Environment environment;

    /**
     * Default constructor
     */
    public Phenotype() {
    }

    /**
     * Generates a thing from the given genome
     *
     * @param genome Genome of the thing to be expressed
     * @return A thing expressed from the genome
     */
    public P get(G genome) {
        return (P) genome;
    }

    /**
     * Generates a genome from the given thing
     *
     * @param thing A thing expressed from the genome
     * @return Genome of the thing
     */
    public G set(P thing) {
        return (G) thing;
    }

}
