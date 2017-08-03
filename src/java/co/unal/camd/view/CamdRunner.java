/**
 *
 */
package co.unal.camd.view;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.properties.estimation.BoilingTemp;
import co.unal.camd.properties.estimation.Density;
import co.unal.camd.properties.estimation.DielectricConstant;
import co.unal.camd.properties.estimation.GibbsEnergy;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.MeltingTemp;
import co.unal.camd.ga.haea.MoleculeEvolution;
import co.unal.camd.ga.haea.MoleculeFitness;
import co.unal.camd.properties.estimation.Molecules;
import co.unal.camd.properties.estimation.Node;
import unalcol.evolution.Environment;
import unalcol.evolution.GenomeLimits;
import unalcol.evolution.Population;
import unalcol.evolution.algorithms.haea.HaeaOperators;
import unalcol.evolution.selections.Elitism;
import unalcol.util.ConsoleTracer;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;

/**
 * @author Kevin Adrián Rodríguez Ruiz
 */
public class CamdRunner extends JFrame {

    private static final long serialVersionUID = 1L;

    protected JTabbedPane tab;
    protected int parentSize;
    protected int maxGroups;
    protected ContributionParametersManager parametersManager;
    protected double temperature;
    protected int maxIterations;
    protected ArrayList<GroupArray> moleculesUser;
    private ArrayList<Molecules> molecules;

    /**
     * Despliega un JFileChooser y retorna la ruta absoluta del archivo
     * seleccionado
     *
     * @return Ruta absoluta del archivo seleccionado
     */
    public String readRootFile() {
        JFileChooser fc = new JFileChooser("./data/Molecules");
        fc.setDialogTitle("Seleccione una molécula");
        int resultado = fc.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            String selected = fc.getSelectedFile().getAbsolutePath();
            return selected;
        }
        return null;
    }

    public void createMolec() {
        tab.removeAll();
        System.out.println("iterat" + maxIterations);
        double[] weight = {0.2, 0.2, 0.2, 0.2, 0.2};  // ge, bt, d,mt, sl
        System.out.println("pesos (ge, bt, d, mt, sl)" + weight);
        double[][] lim = new double[3][5];

        lim[0][0] = 15;  //this is all the B
        lim[0][1] = 15;
        lim[0][2] = 15;
        lim[0][3] = 15;
        lim[0][4] = 15;

        lim[1][0] = 5000;   //this is all the Po
        lim[1][1] = 573;
        lim[1][2] = 1;
        lim[1][3] = 323;
        lim[1][4] = 0.1;

        lim[2][0] = 0.076;  //this is all the inc
        lim[2][1] = 0.0142;
        lim[2][2] = 0.1;
        lim[2][3] = 0.0723;
        lim[2][4] = 0.05;

        Environment env = MoleculeEvolution.getEnvironment(temperature, moleculesUser.get(0), moleculesUser.get(1), weight, lim, parametersManager, maxGroups);
        HaeaOperators operators = MoleculeEvolution.getOperators(env);
        GenomeLimits limits;


        Population population = MoleculeEvolution.evolve(parentSize, env, maxIterations, operators, new Elitism(env, 1, false, 1.0, 0.0), new ConsoleTracer());

        double best = population.statistics().best;
        double avg = population.statistics().avg;
        double worst = population.statistics().worst;
        System.out.println("best: " + best);
        System.out.println("avg: " + avg);
        System.out.println("worst: " + worst);
        JTree jTree;
        for (int i = 0; i < parentSize; i++) {
            Molecules solvent = (Molecules) population.get(i).getThing();
            Node node = solvent.getMoleculeByRootGroup();
            String name = parametersManager.getName(node.getRootNode());
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(name);
            jTree = new JTree(moleculeToJtree(node, n));
            //tree=new MoleculeTree(solvent.getMoleculeByRootGroup());

            int fitness = (int) population.get(i).getFitness();

            ArrayList<Integer> secOrderCodes = solvent.get2OrderGroupArray(parametersManager);
            GibbsEnergy GE = new GibbsEnergy(solvent, secOrderCodes, parametersManager);
            BoilingTemp BT = new BoilingTemp(solvent, secOrderCodes, parametersManager);
            Density D = new Density(solvent, temperature, parametersManager);
            MeltingTemp MT = new MeltingTemp(solvent, secOrderCodes, parametersManager);
            DielectricConstant DC = new DielectricConstant(solvent, secOrderCodes, temperature, parametersManager);
            MoleculeFitness KS = new MoleculeFitness(temperature, moleculesUser.get(0), moleculesUser.get(1), weight, lim, parametersManager);

            // TODO Auto-generated method stub
            double ge = GE.getMethodResult();
            double bt = BT.getMethodResult();
            double den = D.getMethodResult();
            double mt = MT.getMethodResult();
            double dc = DC.getDielectricConstant();
            double ks = KS.getKS(solvent);

            System.out.println("/////////////////////////// " + i + "/////////////////////////////////////");
            System.out.println("Ge: " + ge);
            System.out.println("BT: " + bt);
            System.out.println("Den: " + den);
            System.out.println("MT: " + mt);
            System.out.println("DC: " + dc);
            System.out.println("KS: " + ks);
            System.out.println("//////////////////////////////////////////////////////////////");

            tab.addTab("F " + fitness, null, jTree, "molecule number");
        }

        /**
         tab.removeAll();
         molecules = new ArrayList<Molecules>();
         MoleculeGenotype aMoleculeGenotype=new MoleculeGenotype(maxGroups,aGC);
         for(int i=0;i<parents;i++){
         molecules.add(aMoleculeGenotype.newInstance());
         while(molecules.get(i).getTotalGroups()>maxGroups || molecules.get(i).getTotalGroups()==0){
         molecules.remove(i);
         molecules.add(aMoleculeGenotype.newInstance());
         }
         tree=new MoleculeTree(molecules.get(i).getMoleculeByRootGroup());
         System.out.println("final"+molecules.get(i).getTotalGroups());
         tab.addTab("Molec"+i,null,tree,"molecule number");

         }*/

    }

    public void createMolecNu() {

       /* // Search Space definition
        int DIM = 10;
        double[] min = DoubleArray.create(DIM, -5.12);
        double[] max = DoubleArray.create(DIM, 5.12);
        Space<double[]> space = new HyperCube( min, max );

        // Optimization Function
        OptimizationFunction<double[]> function = new Rastrigin();
        Goal<double[],Double> goal = new OptimizationGoal<double[]>(function); // minimizing, add the parameter false if maximizing

        // Variation definition
        DoubleGenerator random = new SimplestSymmetricPowerLawGenerator(); // It can be set to Gaussian or other symmetric number generator (centered in zero)
        PickComponents pick = new PermutationPick(DIM/2); // It can be set to null if the mutation operator is applied to every component of the solution array
        IntensityMutation mutation = new IntensityMutation( 0.1, random, pick );
        RealArityTwo xover = new LinearXOver();

        // Search method
        int POPSIZE = 10;
        int MAXITERS = 5;
        HaeaOperators<double[]> operators = new SimpleHaeaOperators<double[]>(mutation, xover);
        EAFactory<double[]> factory = new EAFactory<double[]>();
        PopulationSearch<double[],Double> search =
                factory.HAEA(POPSIZE, operators, new Tournament<double[]>(4), MAXITERS );

        // Tracking the goal evaluations
        WriteDescriptors write_desc = new WriteDescriptors();
        Write.set(Population.class, write_desc);
        Write.set(double[].class, new DoubleArrayPlainWrite(false));
        Write.set(HaeaStep.class, new WriteHaeaStep<double[]>());
        Descriptors.set(Population.class, new PopulationDescriptors<double[]>());
        Descriptors.set(HaeaOperators.class, new SimpleHaeaOperatorsDescriptor<double[]>());
        Write.set(HaeaOperators.class, write_desc);

        ConsoleTracer tracer = new ConsoleTracer();
//        Tracer.addTracer(goal, tracer);  // Uncomment if you want to trace the function evaluations
//        FileTracer file = new FileTracer("prueba-lab.txt");
        Tracer.addTracer(search, tracer); // Uncomment if you want to trace the hill-climbing algorithm
//        Tracer.addTracer(search, file);
        // Apply the search method
        Solution<double[]> solution = search.solve(space, goal);

        System.out.println(solution.info(Goal.class.getName()));
        tab.removeAll();*/

        /**
         tab.removeAll();
         molecules = new ArrayList<Molecules>();
         MoleculeGenotype aMoleculeGenotype=new MoleculeGenotype(maxGroups,aGC);
         for(int i=0;i<parents;i++){
         molecules.add(aMoleculeGenotype.newInstance());
         while(molecules.get(i).getTotalGroups()>maxGroups || molecules.get(i).getTotalGroups()==0){
         molecules.remove(i);
         molecules.add(aMoleculeGenotype.newInstance());
         }
         tree=new MoleculeTree(molecules.get(i).getMoleculeByRootGroup());
         System.out.println("final"+molecules.get(i).getTotalGroups());
         tab.addTab("Molec"+i,null,tree,"molecule number");

         }*/
    }

    public DefaultMutableTreeNode moleculeToJtree(Node molec, DefaultMutableTreeNode node) {
        for (int i = 0; i < molec.getGroupsCount(); i++) {
            String n = parametersManager.getName(molec.getGroupAt(i).getRootNode());
            DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(n);

            moleculeToJtree(molec.getGroupAt(i), aNode);
            node.add(aNode);
        }
        return node;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
