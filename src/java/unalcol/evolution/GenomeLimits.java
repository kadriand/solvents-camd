package unalcol.evolution;

import unalcol.random.*;

/**
 * <p>Title: GenomeLimits</p>
 * <p>Description: Abstract class representing the genome information</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */

public class GenomeLimits {
    /**
     * Generates a random number
     */
    protected IntegerGenerator g = null;

    /**
     * Minimum number of genes in the chromosome
     */
    public int min_genes = 1;

    /**
     * Maximum number of genes in the chromosome
     */
    public int max_genes = 1;

    /**
     * Gene size in the chromosome (number of "bits" that defines a gene)
     */
    public int gene_size = 1;

    /**
     * Constructor
     *
     * @param minGenes Min number of genes in the chromosome
     * @param maxGenes Max number of genes in the chromosome
     * @param geneSize Size of each gene in the chromosome
     */
    public GenomeLimits(int minGenes, int maxGenes, int geneSize) {
        gene_size = geneSize;
        min_genes = minGenes;
        max_genes = maxGenes;
        if (min_genes < max_genes) {
            g = new IntegerGenerator(max_genes - min_genes + 1);
        }
    }

    /**
     * Creates a Genome information object indicating the max and min number of
     * genes in the chromosome (the size of the gene is set to 1)
     *
     * @param _min_genes Min number of genes in the chromosome
     * @param _max_genes Max number of genes in the chromosome
     */
    public GenomeLimits(int _min_genes, int _max_genes) {
        min_genes = _min_genes;
        max_genes = _max_genes;
        if (min_genes < max_genes) {
            g = new IntegerGenerator(max_genes - min_genes + 1);
        }
    }

    /**
     * Creates a Genome information object indicating the fixed number of
     * genes in the chromosome (the size of the gene is set to 1)
     *
     * @param n Number of genes in the chromosome
     */
    public GenomeLimits(int n) {
        min_genes = n;
        max_genes = n;
    }

    /**
     * Copy constructor
     *
     * @param source Creates a Genome information object with the same information of
     *               the object given
     */
    public GenomeLimits(GenomeLimits source) {
        gene_size = source.gene_size;
        min_genes = source.min_genes;
        max_genes = source.max_genes;
        g = source.g;
    }

    /**
     * Generates a random number of genes that a chromosome can have (between min and max)
     *
     * @return A number of genes that a chromosome can have
     */
    public int random_genes_number() {
        if (g != null) {
            return (min_genes + g.next());
        } else {
            return min_genes;
        }
    }

    /**
     * Save the Genome information to a String
     *
     * @return String with the information of the genome
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[genes number] " + gene_size + "\n");
        sb.append("[min] " + min_genes + "\n");
        sb.append("[max] " + max_genes + "\n");
        return sb.toString();
    }
}
