package co.unal.camd.methods.unifac.properties;

import co.unal.camd.methods.unifac.UnifacSubGroup;

public class InsufficientParametersException extends Exception {
    public InsufficientParametersException(String errorMessage) {
        super(errorMessage);
    }

    InsufficientParametersException(UnifacSubGroup nGroup, UnifacSubGroup mGroup) {
        super(String.format("No parameters for interaction %s:%s (%s:%s)", nGroup.getGroupName(), mGroup.getGroupName(), nGroup.getMainGroup().getCode(), mGroup.getMainGroup().getCode()));
    }
}
