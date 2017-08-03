package unalcol.evolution.binary.operators;

import unalcol.evolution.*;
import unalcol.evolution.binary.VariableLengthBinaryGenotype;
import unalcol.evolution.operators.ArityOne;
import unalcol.random.*;
import unalcol.structures.bitarray.BitArray;
import unalcol.core.Cloner;

import java.util.Vector;

/**
 * <p>Title: AddGen</p>
 * <p>Description: The gene addition operator. Add a gene generated randomly</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class AddGen extends ArityOne<BitArray> {
    /**
     * If the added gene is added to the end of the genome or not (randomly added)
     */
    protected boolean append = true;

    /**
     * Constructor: create an addition gene operator that adds a gene to the end of a genome
     *
     * @param environment The environment
     */
    public AddGen(Environment environment) {
        super(environment);
    }

    /**
     * Constructor: creates an addition gene operator that adds a gene according to
     * the variable _append
     *
     * @param append If the added gene is added to the end of the genome or not (randomly added)
     */
    public AddGen(Environment environment, boolean append) {
        super(environment);
        this.append = append;
    }

    /**
     * Add to the end of the given genome a new gene
     *
     * @param gen Genome to be modified
     * @return The added gene or a String
     */
    public Vector<BitArray> apply(BitArray gen) {
        BitArray genome = (BitArray) Cloner.clone(gen);
        VariableLengthBinaryGenotype genotype = (VariableLengthBinaryGenotype) environment.getGenotype();
        int gene_size = genotype.getDeltaLength();
        int min_length = genotype.getMinLength();
        int max_length = genotype.getMaxLength();
        if (genome.size() < max_length) {
            BitArray gene = new BitArray(gene_size, true);
            if (append) {
                genome.add(gene);
            } else {
                int size = genotype.size(genome);
                IntegerGenerator g = new IntegerGenerator(size + 1);
                int k = g.next();
                if (k == size) {
                    genome.add(gene);
                } else {
                    BitArray right = genome.subBitArray(min_length + k * gene_size);
                    genome.del((size - k) * gene_size);
                    genome.add(gene);
                    genome.add(right);
                }
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
        System.out.println("*** Generating a genome of 21 genes randomly ***");
        BitArray genome = new BitArray(21, true);
        System.out.println(genome.toString());

        System.out.println("*** Generating a Addition Gen operation with gen length of 3 ***");
        AddGen add = new AddGen(new Environment(new VariableLengthBinaryGenotype(21, 27, 3), null));
        System.out.println("*** Applying the addition ***");
        BitArray gene = add.apply(genome).get(0);

        System.out.println("*** Mutated genome ***");
        System.out.println(gene);
    }
}
