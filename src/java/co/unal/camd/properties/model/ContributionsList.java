package co.unal.camd.properties.model;

import java.util.ArrayList;

public class ContributionsList<E extends ContributionGroupNode> extends ArrayList<E> {

    private ContributionGroupNode parentGroupNode;

    public ContributionsList(ContributionGroupNode parentGroupNode) {
        this.parentGroupNode = parentGroupNode;
    }

    @Override
    public boolean add(E e) {
        e.setParentGroup(parentGroupNode);
        if (e != null)
            return super.add(e);
        else
            return true;
    }
}
