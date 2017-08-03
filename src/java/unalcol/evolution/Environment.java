package unalcol.evolution;

/**
 * <p>Title: Enviroment</p>
 * <p>Description: Environment that stores (Genotype,Phenotype,Fitness)</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: Universidad Nacional de Colombia</p>
 *
 * @author Jonatan Gomez Reviewed by (Aurelio Benitez, Giovanni Cantor, Nestor Bohorquez)
 * @version 1.0
 */
public class Environment<P, G> {
    /**
     * The genotype of the Enviroment
     */
    protected Genotype<G> genotype;
    /**
     * The phenotype of the Enviroment
     */
    protected Phenotype<P, G> phenotype;
    /**
     * The fitness of the Enviroment
     */
    protected Fitness<P> fitness;

    /**
     * Default Constructor
     */
    protected Environment() {
    }

    /**
     * Creates a Enviroment with the given genotype, phenotype  and fitness
     *
     * @param genotype  The genotype of the Enviroment
     * @param phenotype The phenotype of the Enviroment
     * @param fitness   The fitness of the Enviroment
     */
    public Environment(Genotype<G> genotype, Phenotype<P, G> phenotype, Fitness<P> fitness) {
        this.genotype = genotype;
        this.phenotype = phenotype;
        this.fitness = fitness;
        link();
    }

    /**
     * Creates a Enviroment with the given genotype and fitness
     *
     * @param genotype The genotype of the Enviroment
     * @param fitness  The fitness of the Enviroment
     */
    public Environment(Genotype<G> genotype, Fitness<P> fitness) {
        this.genotype = genotype;
        this.phenotype = new Phenotype<P, G>();
        this.fitness = fitness;
        link();
    }

    /**
     * References the enviroment in the genotype, phenotype and fitness
     */
    protected void link() {
        if (genotype != null) {
            genotype.environment = this;
        }
        if (phenotype != null) {
            phenotype.environment = this;
        }
        if (fitness != null) {
            fitness.setEnvironment(this);
        }
    }

    /**
     * Returns the genotype
     *
     * @return The genotype
     */
    public Genotype<G> getGenotype() {
        return genotype;
    }

    /**
     * Returns the phenotype
     *
     * @return The phenotype
     */
    public Phenotype<P, G> getPhenotype() {
        return phenotype;
    }

    /**
     * Return the fitness
     *
     * @return The fitness
     */
    public Fitness<P> getFitness() {
        return fitness;
    }

    /**
     * Evaluates the given individual
     *
     * @param ind The individual to evaluate
     * @return The fitness value of the given individual
     */
    public double evaluate(Individual<G, P> ind) {
        return fitness.evaluate(ind.getThing(this));
    }

    /**
     * Sets the genotype of the Enviroment
     *
     * @param genotype The genotype
     */
    public void setGenotype(Genotype<G> genotype) {
        this.genotype = genotype;
        this.genotype.environment = this;
    }
}
