package co.unal.camd.model.molecule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class UnifacGroupNodesList<E extends UnifacGroupNode> extends ArrayList<E> {

    private UnifacGroupNode parentGroupNode;

    private static final Logger LOGGER = LoggerFactory.getLogger(UnifacGroupNodesList.class);

    public UnifacGroupNodesList(UnifacGroupNode parentGroupNode) {
        this.parentGroupNode = parentGroupNode;
    }

    @Override
    public boolean add(E element) {
        if (element.getParentGroup() != null && element.getParentGroup() != parentGroupNode)
            LOGGER.debug("Replacing parent group of branched contribution group, BE CAREFUL");
        element.setParentGroup(parentGroupNode);
        return super.add(element);
    }

    @Override
    public void add(int index, E element) {
        if (element.getParentGroup() != null && element.getParentGroup() != parentGroupNode)
            LOGGER.debug("Replacing parent group of branched contribution group, BE CAREFUL");
        element.setParentGroup(parentGroupNode);
        super.add(index, element);
    }

    public boolean add(E... elements) {
        boolean result = true;
        for (E element : elements)
            result = result && this.add(element);
        return result;
    }

    public boolean remove(E element) {
        if (!super.contains(element))
            return false;
        element.setParentGroup(null);
        return super.remove(element);
    }

    @Override
    public E remove(int index) {
        E removedElement = super.remove(index);
        removedElement.setParentGroup(null);
        return removedElement;
    }

}
