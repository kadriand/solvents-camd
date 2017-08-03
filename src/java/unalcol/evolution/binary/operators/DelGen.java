package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.VariableLengthBinaryGenotype;
import unalcol.evolution.operators.ArityOne;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;


/**
 * <p>Title: DelGen</p>
 * <p>Description: The gene deletion operator.  Deletes the last gene in the genome or
 * one randomly selected</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class DelGen extends ArityOne<BitArray> {
    /**
     * If the last gene is going to be deleted or one randomly selected
     */
    protected boolean del_last_gene = true;

    /**
     * Constructor: create a deletion gene operator that deletes the last gene of a genome
     */
    public DelGen(Environment _environment) {
        super(_environment);
    }

    /**
     * Constructor: create a deletion gene operator that deletes a gene of a genome
     *
     * @param _del_last_gene Determines if the gene to be deleted is the last in
     *                       the genome or not. A true value indicates that the last gene is deleted.
     *                       A false value indiciates that a gene is randomly selected and deleted
     */
    public DelGen(Environment _environment, boolean _del_last_gene) {
        super(_environment);
        del_last_gene = _del_last_gene;
    }

    /**
     * Delete from the given genome the last gene
     *
     * @param gen Genome to be modified
     */
    public Vector<BitArray> apply(BitArray gen) {
        BitArray genome = (BitArray) Cloner.clone(gen);
        VariableLengthBinaryGenotype genotype = (VariableLengthBinaryGenotype) environment.getGenotype();
        int gene_size = genotype.getDeltaLength();
        int min_length = genotype.getMinLength();
        if (genome.size() > min_length + gene_size) {
            if (del_last_gene) {
                genome.del(gene_size);
            } else {
                int size = genotype.size(genome);
                IntegerGenerator g = new IntegerGenerator(size);
                int k = g.next();
                BitArray right = null;
                right = genome.subBitArray(min_length + (k + 1) * gene_size);
                genome.del((size - k) * gene_size);
                genome.add(right);
            }
        }
        Vector<BitArray> v = new Vector<BitArray>();
        v.add(genome);
        return v;
    }

    /**
     * Testing function
     */
    public static void main(String[] argv) {
        System.out.println("*** Generating a genome of 27 genes randomly ***");
        BitArray genome = new BitArray(27, true);
        System.out.println(genome.toString());

        System.out.println("*** Generating a Deletion Gen operation with gen length of 3 ***");
        DelGen del = new DelGen(new Environment(new VariableLengthBinaryGenotype(21, 27, 3), null));

        System.out.println("*** Applying the deletion ***");
        BitArray gene = del.apply(genome).get(0);

        System.out.println("*** Mutated genome ***");
        System.out.println(gene);

    }
}
