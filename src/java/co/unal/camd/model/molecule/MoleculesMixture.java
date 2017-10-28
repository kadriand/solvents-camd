package co.unal.camd.model.molecule;

import co.unal.camd.methods.unifac.UnifacSubGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoleculesMixture<E extends Molecule> extends ArrayList<E> {

    private List<UnifacSubGroup> solutionGroups;

    private Double totalGroupsFractions;

    public MoleculesMixture(E molecule) {
        this.add(molecule);
    }

    public MoleculesMixture(E... molecules) {
        this.add(molecules);
    }

    public List<UnifacSubGroup> getSolutionGroups() {
        if (solutionGroups == null) {
            solutionGroups = new ArrayList<>();
            this.forEach(
                    molecule -> molecule.pickAllGroups().forEach(
                            unifacGroup -> solutionGroups.add(unifacGroup.getUnifacSubGroup())));
        }
        return solutionGroups;
    }

    public Double getTotalGroupsFractions() {
        if (totalGroupsFractions == null)
            totalGroupsFractions = this.stream().mapToDouble(
                    molecule -> molecule.pickAllGroups().size() * molecule.getComposition())
                    .sum();
        return totalGroupsFractions;
    }

    @Override
    public boolean add(E element) {
        this.solutionGroups = null;
        this.totalGroupsFractions = null;
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> elements) {
        this.solutionGroups = null;
        this.totalGroupsFractions = null;
        return super.addAll(elements);
    }

    public final void add(E... elements) {
        this.solutionGroups = null;
        this.totalGroupsFractions = null;
        boolean result = true;
        for (E element : elements)
            result = result && this.add(element);
    }


}
