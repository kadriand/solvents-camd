package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.parameters.unifac.ContributionGroupData;
import co.unal.camd.view.CamdRunner;

import java.util.ArrayList;

import static co.unal.camd.view.CamdRunner.CONTRIBUTION_GROUPS;

public class Restrictions {

    private static int counter;
    private static int typeFunctional;

    /**
     * This method count the number of functional groups in the molec and return true if is possible add other funcional group
     *
     * @return
     */
    public static boolean canBeFunctional(ArrayList<ContributionGroupNode> functionalGroups) {
        typeFunctional = 0;
        // if the code Row of the group is grater than 1 the group is functional, so the counter increase in one.
        for (int j = 0; j < functionalGroups.size(); j++) {
            ContributionGroupNode functionalGroup = functionalGroups.get(j);
            Integer mainGroupCode = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(functionalGroup.getGroupCode()).getMainGroup().getCode();
            if (mainGroupCode > 1)
                typeFunctional++;
            count(functionalGroup);
        }
        return typeFunctional < 3;
    }

    public static void count(ContributionGroupNode aFunctionalGroupNode) {
        for (int i = 0; i < aFunctionalGroupNode.getSubGroups().size(); i++) {
            ContributionGroupNode functionalGroup = aFunctionalGroupNode.getGroupAt(i);
            Integer mainGroupCode = CamdRunner.CONTRIBUTION_GROUPS.getContributionGroups().get(functionalGroup.getGroupCode()).getMainGroup().getCode();
            if (mainGroupCode > 1)
                typeFunctional++;
            if (functionalGroup.countSubgroups() > 0)
                count(functionalGroup);
        }
    }

    /**
     * this method change the groups root or leaf , for solve the problem funtional-funtional or correct the OH(s,p o t)
     *
     * @param group
     * @param newGr
     * @param canBeChangeNewGr
     */

    public static void mayBeFuncFuncOrOH(ContributionGroupNode group, ContributionGroupNode newGr, boolean canBeChangeNewGr) {
        ContributionGroupNode originalGroup = newGr;
        ContributionGroupData newGroupContribution = CONTRIBUTION_GROUPS.getContributionGroups().get(newGr.getGroupCode());
        int mainGroupCode = newGroupContribution.getMainGroup().getCode();

        switch (newGroupContribution.getCode()) {
            case 2: {
                /*OH(P) group*/
                if (mainGroupCode == 5)
                    group = new ContributionGroupNode(14);
            }
            case 3: {
                /*OH(S) group*/
                if (mainGroupCode == 5)
                    group = new ContributionGroupNode(81);
            }
            case 4: {
                /*OH(T) group*/
                if (mainGroupCode == 5)
                    group = new ContributionGroupNode(82);
            }
            default: {
                ContributionGroupData groupContribution = CONTRIBUTION_GROUPS.getContributionGroups().get(group.getGroupCode());
                ContributionGroupData newGroupContributionDef = CONTRIBUTION_GROUPS.getContributionGroups().get(newGr.getGroupCode());
                while (groupContribution.getMainGroup().getCode() > 1 && newGroupContributionDef.getMainGroup().getCode() > 1)
                    //System.out.println("entra a while_restric");
                    if (canBeChangeNewGr) {
                        newGr = new ContributionGroupNode(MoleculeOperations.getNewGroupCode(newGroupContribution.getValence(), false));
                        if (originalGroup.countSubgroups() > 0)
                            for (int i = 0; i < originalGroup.countSubgroups(); i++) {
                                //aValence=gr.getTemporalValence();
                                //	gr.getGroupAt(i).setValence(aValence+1);//incrementar valencia por qutar grupo
                                newGr.addGroup(originalGroup.getGroupAt(i));
                            }
                        newGroupContributionDef = CONTRIBUTION_GROUPS.getContributionGroups().get(newGr.getGroupCode());
                    } else {
                        group = new ContributionGroupNode(MoleculeOperations.getNewGroupCode(1, false));
                        groupContribution = CONTRIBUTION_GROUPS.getContributionGroups().get(group.getGroupCode());
                    }
            }
            break;
        }
        newGr.addGroup(group);
    }

    /**public void typeOfOH(Groups aGroups){
     for(int i=0;i<aGroups.countSubgroups();i++){
     if(aGroups.findGroupCode()==2){
     System.out.println("cambio?");
     if(aGroups.getGroupAt(i).getMainGroupCode()==5){
     System.out.println("cambio por oh1");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else if(aGroups.findGroupCode()==3){

     if(aGroups.getGroupAt(i).getMainGroupCode()==5){
     System.out.println("cambio por oh2");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else  if(aGroups.findGroupCode()==4){
     //System.out.println(aGroups.getGroupAt(i).findGroupName());
     if(aGroups.getGroupAt(i).getMainGroupCode()==5){
     System.out.println("cambio por oh3");
     aGroups.setGroupAt(i, new Groups("OH3",operator));
     }

     }
     }
     }
     */

}



