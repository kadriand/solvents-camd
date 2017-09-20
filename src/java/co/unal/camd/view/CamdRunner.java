package co.unal.camd.view;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import co.unal.camd.ga.haea.MoleculeEvolution;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import co.unal.camd.properties.estimation.GroupArray;
import co.unal.camd.properties.estimation.Molecule;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import unalcol.search.population.Population;
import unalcol.search.solution.Solution;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kevin Adrián Rodríguez Ruiz
 */
@Data
public class CamdRunner extends JFrame {

    private static final long serialVersionUID = 1L;

    @Setter(AccessLevel.NONE)
    protected JTabbedPane tab;
    protected int parentSize;
    protected int maxGroups;
    protected ContributionGroupsManager contributionGroups;
    protected double temperature;
    protected int maxIterations;
    protected ArrayList<GroupArray> moleculesUser;

    private double[] weight = {0.2, 0.2, 0.2, 0.2, 0.2};  // ge, bt, d, mt, sloss
    private double[][] constraintsLimits = new double[3][5];
    private ArrayList<Molecule> molecules;

    /**
     * Despliega un JFileChooser y retorna la ruta absoluta del archivo
     * seleccionado
     *
     * @return Ruta absoluta del archivo seleccionado
     */
    public String selectFile() {
        JFileChooser fileChooser = new JFileChooser("./data/Molecules");
        fileChooser.setDialogTitle("Seleccione una molécula");
        int result = fileChooser.showOpenDialog(this);

        if (result != JFileChooser.APPROVE_OPTION)
            return null;
        String selected = fileChooser.getSelectedFile().getAbsolutePath();
        return selected;
    }

    public void designSuitableMolecules() {
        tab.removeAll();
        System.out.println("iterat " + maxIterations);
        System.out.println("pesos (ge, bt, d, mt, sl)" + weight);

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


        MoleculeEvolution moleculeEvolution = new MoleculeEvolution(this);
        System.out.println("parent size" + parentSize);
        System.out.println(" max iter :" + maxIterations);
        Population<Molecule> population = moleculeEvolution.evolve(parentSize, maxIterations);

        //        double best = population.statistics().best;
        //        double avg = population.statistics().avg;
        //        double worst = population.statistics().worst;
        //        System.out.println("best: " + best);
        //        System.out.println("avg: " + avg);
        //        System.out.println("worst: " + worst);

        System.out.println("BEST FITNESS");
        Solution<Molecule> bestSolution = moleculeEvolution.getBestSolution();
        Double bestFitness = bestSolution.object().getFitness();
        System.out.println(bestFitness);

        List<Solution<Molecule>> sortedSolution = Arrays.asList(population.object()).stream().sorted(Comparator.comparingDouble(o -> o.object().getFitness())).collect(Collectors.toList());
        JTree jTree;
        for (int i = 0; i < parentSize; i++) {
            Molecule solvent = population.get(i).object();

            FunctionalGroupNode functionalGroupNode = solvent.getMoleculeByRootGroup();
            String name = contributionGroups.getName(functionalGroupNode.getRootNode());
            DefaultMutableTreeNode n = new DefaultMutableTreeNode(name);
            jTree = new JTree(moleculeToJtree(functionalGroupNode, n));
            //            tree = new MoleculeTree(solvent.getMoleculeByRootGroup());

            // TODO Auto-generated method stub
            double ge = solvent.getGe();
            double bt = solvent.getBt();
            double den = solvent.getD();
            double mt = solvent.getMt();
            //            double dc = solvent.getDc();
            double ks = solvent.getKs();
            double fitness = solvent.getFitness();

            System.out.println("/////////////////////////// " + i + "/////////////////////////////////////");
            System.out.println("Ge: " + ge);
            System.out.println("BT: " + bt);
            System.out.println("Den: " + den);
            System.out.println("MT: " + mt);
            //            System.out.println("DC: " + dc);
            System.out.println("KS: " + ks);
            System.out.println("//////////////////////////////////////////////////////////////");

            // TODO IS THIS NECESARY?
            tab.addTab("F " + fitness, null, jTree, "molecule number");
        }
    }

    public DefaultMutableTreeNode moleculeToJtree(FunctionalGroupNode molec, DefaultMutableTreeNode node) {
        for (int i = 0; i < molec.getGroupsCount(); i++) {
            String n = contributionGroups.getName(molec.getGroupAt(i).getRootNode());
            DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(n);

            moleculeToJtree(molec.getGroupAt(i), aNode);
            node.add(aNode);
        }
        return node;
    }
}