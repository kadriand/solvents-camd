/**
 *
 */
package co.unal.camd.view;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.ga.haea.MoleculeEvolution;
import co.unal.camd.ga.haea.MoleculeFitness;
import co.unal.camd.properties.estimation.BoilingTemp;
import co.unal.camd.properties.estimation.Density;
import co.unal.camd.properties.estimation.DielectricConstant;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.GibbsEnergy;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.MeltingTemp;
import co.unal.camd.properties.estimation.Molecules;
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

    JTabbedPane tab;
    int parentSize;
    int maxGroups;
    ContributionParametersManager parametersManager;
    protected double temperature;
    int maxIterations;
    ArrayList<GroupArray> moleculesUser;
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

    public void designSuitableMolecules() {
        tab.removeAll();
        System.out.println("iterat " + maxIterations);
        double[] weight = {0.2, 0.2, 0.2, 0.2, 0.2};  // ge, bt, d, mt, sloss
        System.out.println("pesos (ge, bt, d, mt, sl)" + weight);
        double[][] constraintsLimits = new double[3][5];

        constraintsLimits[0][0] = 15;  //this is all the B
        constraintsLimits[0][1] = 15;
        constraintsLimits[0][2] = 15;
        constraintsLimits[0][3] = 15;
        constraintsLimits[0][4] = 15;

        //limits
        constraintsLimits[1][0] = 5000;   //this is all the Po
        constraintsLimits[1][1] = 573;
        constraintsLimits[1][2] = 1;
        constraintsLimits[1][3] = 323;
        constraintsLimits[1][4] = 0.1;

        constraintsLimits[2][0] = 0.076;  //this is all the uncertainty
        constraintsLimits[2][1] = 0.0142;
        constraintsLimits[2][2] = 0.1;
        constraintsLimits[2][3] = 0.0723;
        constraintsLimits[2][4] = 0.05;

        Environment environment = MoleculeEvolution.buildEnvironment(temperature, moleculesUser.get(0), moleculesUser.get(1), weight, constraintsLimits, parametersManager, maxGroups);
        HaeaOperators operators = MoleculeEvolution.buildOperators(environment);
        GenomeLimits limits;
        Elitism elitistSelection = new Elitism(environment, 1, false, 1.0, 0.0);
        Population population = MoleculeEvolution.evolve(parentSize, environment, maxIterations, operators, elitistSelection, new ConsoleTracer());

        double best = population.statistics().best;
        double avg = population.statistics().avg;
        double worst = population.statistics().worst;

        System.out.println("best: " + best);
        System.out.println("avg: " + avg);
        System.out.println("worst: " + worst);

        JTree jTree;
        for (int i = 0; i < parentSize; i++) {
            Molecules solvent = (Molecules) population.get(i).getThing();
            FunctionalGroupNode functionalGroupNode = solvent.getMoleculeByRootGroup();
            String name = parametersManager.getName(functionalGroupNode.getRootNode());
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(name);
            jTree = new JTree(moleculeToJtree(functionalGroupNode, n));
//            tree = new MoleculeTree(solvent.getMoleculeByRootGroup());

            int fitness = (int) population.get(i).getFitness();

            ArrayList<Integer> secOrderCodes = solvent.get2OrderGroupArray(parametersManager);
            GibbsEnergy GE = new GibbsEnergy(solvent, secOrderCodes, parametersManager);
            BoilingTemp BT = new BoilingTemp(solvent, secOrderCodes, parametersManager);
            Density D = new Density(solvent, temperature, parametersManager);
            MeltingTemp MT = new MeltingTemp(solvent, secOrderCodes, parametersManager);
            DielectricConstant DC = new DielectricConstant(solvent, secOrderCodes, temperature, parametersManager);
            MoleculeFitness KS = new MoleculeFitness(temperature, moleculesUser.get(0), moleculesUser.get(1), weight, constraintsLimits, parametersManager);

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

            // TODO IS THIS NECESARY?
            tab.addTab("F " + fitness, null, jTree, "molecule number");
        }

    }

    public DefaultMutableTreeNode moleculeToJtree(FunctionalGroupNode molec, DefaultMutableTreeNode node) {
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