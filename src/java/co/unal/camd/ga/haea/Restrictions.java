package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.FunctionalGroupNode;

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
    public static boolean canBeFunctional(ArrayList<FunctionalGroupNode> functionalGroups) {
        typeFunctional = 0;
        // if the code Row of the group is grater than 1 the group is functional, so the counter increase in one.
        for (int j = 0; j < functionalGroups.size(); j++) {
            FunctionalGroupNode functionalGroup = functionalGroups.get(j);
            CONTRIBUTION_GROUPS.resolveValence(functionalGroup.getRootNode());
            if (CONTRIBUTION_GROUPS.getCodeOfRow() > 1)
                typeFunctional++;
            count(functionalGroup);
        }
        return typeFunctional < 3;
    }

    public static void count(FunctionalGroupNode aFunctionalGroupNode) {
        for (int i = 0; i < aFunctionalGroupNode.getSubGroups().size(); i++) {
            FunctionalGroupNode functionalGroup = aFunctionalGroupNode.getGroupAt(i);
            CONTRIBUTION_GROUPS.resolveValence(functionalGroup.getRootNode());
            if (CONTRIBUTION_GROUPS.getCodeOfRow() > 1)
                typeFunctional++;
            if (functionalGroup.countSubgroups() > 0)
                count(functionalGroup);
        }
    }

    /**
     * this method change the groups root or leaf , for solve the problem funtional-funtional or correct the OH(s,p o t)
     *
     * @param aG1
     * @param newGr
     * @param canBeChangeNewGr
     */

    public static void mayBeFuncFuncOrOH(FunctionalGroupNode aG1, FunctionalGroupNode newGr, boolean canBeChangeNewGr) {
        FunctionalGroupNode gr;
        FunctionalGroupNode gr2 = aG1;
        int aValence = 0;
        gr = newGr;

        int code = newGr.getRootNode();
        switch (code) {
            case 2: {

                if (CONTRIBUTION_GROUPS.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHp");
                    aG1 = new FunctionalGroupNode("OHP");
                }
            }
            case 3: {
                if (CONTRIBUTION_GROUPS.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHs");
                    aG1 = new FunctionalGroupNode("OHS");
                }
            }
            case 4: {
                //System.out.println(aGroups.getGroupAt(i).findGroupName());
                if (CONTRIBUTION_GROUPS.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHt");
                    aG1 = new FunctionalGroupNode("OHT");
                }
            }
            default: {

                aValence = CONTRIBUTION_GROUPS.findGroupValence(newGr.getRootNode());
                //System.out.println("avalence"+aValence);

                CONTRIBUTION_GROUPS.resolveValence(aG1.getRootNode());
                int a = CONTRIBUTION_GROUPS.getCodeOfRow();
                CONTRIBUTION_GROUPS.resolveValence(newGr.getRootNode());
                int b = CONTRIBUTION_GROUPS.getCodeOfRow();

                while (a > 1 && b > 1) {
                    //System.out.println("entra a while_restric");
                    if (canBeChangeNewGr == true) {
                        newGr = new FunctionalGroupNode(MoleculeOperations.findNewRefCode(aValence, false));
                        if (gr.countSubgroups() > 0) {
                            for (int i = 0; i < gr.countSubgroups(); i++) {
                                //aValence=gr.getTemporalValence();
                                //	gr.getGroupAt(i).setValence(aValence+1);//incrementar valencia por qutar grupo
                                newGr.addGroup(gr.getGroupAt(i));
                            }
                        }
                    } else {
                        aG1 = new FunctionalGroupNode(MoleculeOperations.findNewRefCode(1, false));
                    }
                    CONTRIBUTION_GROUPS.resolveValence(aG1.getRootNode());
                    a = CONTRIBUTION_GROUPS.getCodeOfRow();
                    CONTRIBUTION_GROUPS.resolveValence(newGr.getRootNode());
                    b = CONTRIBUTION_GROUPS.getCodeOfRow();
                }
            }
            break;
        }
        newGr.addGroup(aG1);
    }

    /**public void typeOfOH(Groups aGroups){
     for(int i=0;i<aGroups.countSubgroups();i++){
     if(aGroups.findGroupCode()==2){
     System.out.println("cambio?");
     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh1");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else if(aGroups.findGroupCode()==3){

     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh2");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else  if(aGroups.findGroupCode()==4){
     //System.out.println(aGroups.getGroupAt(i).findGroupName());
     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh3");
     aGroups.setGroupAt(i, new Groups("OH3",operator));
     }

     }
     }
     }
     */

}



