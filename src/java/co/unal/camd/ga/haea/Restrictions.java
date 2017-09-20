package co.unal.camd.ga.haea;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import co.unal.camd.properties.estimation.FunctionalGroupNode;

import java.util.ArrayList;

public class Restrictions {

    private static int counter;
    private static int typeFunctional;

    /**
     * This method count the number of functional groups in the molec and return true if is possible add other funcional group
     *
     * @return
     */
    public static boolean canBeFunctional(ArrayList<FunctionalGroupNode> root, ContributionGroupsManager aGC) {
        boolean answer = true;
        typeFunctional = 0;
        // if the code Row of the group is grater than 1 the group is functional, so the counter increase in one.
        for (int j = 0; j < root.size(); j++) {
            FunctionalGroupNode n = root.get(j);
            aGC.getCodeOfRowBNameOrRefCode(n.getRootNode());
            if (aGC.getCodeOfRow() > 1) {
                typeFunctional = typeFunctional + 1;
            }
            count(n, aGC);
        }
        if (typeFunctional >= 3) {
            answer = false;
        }
        return answer;
    }

    public static void count(FunctionalGroupNode aFunctionalGroupNode, ContributionGroupsManager aGC) {

        for (int i = 0; i < aFunctionalGroupNode.getSubGroups().size(); i++) {
            FunctionalGroupNode n = aFunctionalGroupNode.getGroupAt(i);
            aGC.getCodeOfRowBNameOrRefCode(n.getRootNode());
            if (aGC.getCodeOfRow() > 1) {
                typeFunctional = typeFunctional + 1;
            }
            if (n.getGroupsCount() > 0) {
                count(n, aGC);
            }
        }
    }

    /**
     * this method change the groups root or leaf , for solve the problem funtional-funtional or correct the OH(s,p o t)
     *
     * @param aG1
     * @param newGr
     * @param canBeChangeNewGr
     */

    public static void mayBeFuncFuncOrOH(FunctionalGroupNode aG1, FunctionalGroupNode newGr, boolean canBeChangeNewGr, ContributionGroupsManager aGC) {
        FunctionalGroupNode gr;
        FunctionalGroupNode gr2 = aG1;
        int aValence = 0;
        gr = newGr;

        int code = newGr.getRootNode();
        switch (code) {
            case 2: {

                if (aGC.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHp");
                    aG1 = new FunctionalGroupNode("OHP", aGC);
                }
            }
            case 3: {
                if (aGC.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHs");
                    aG1 = new FunctionalGroupNode("OHS", aGC);
                }
            }
            case 4: {
                //System.out.println(aGroups.getGroupAt(i).getName());
                if (aGC.getPrincipalGroupCode(aG1.getRootNode()) == 5) {
                    //System.out.println("cambio por OHt");
                    aG1 = new FunctionalGroupNode("OHT", aGC);
                }
            }
            default: {

                aValence = aGC.getValence(newGr.getRootNode());
                //System.out.println("avalence"+aValence);

                aGC.getCodeOfRowBNameOrRefCode(aG1.getRootNode());
                int a = aGC.getCodeOfRow();
                aGC.getCodeOfRowBNameOrRefCode(newGr.getRootNode());
                int b = aGC.getCodeOfRow();

                while (a > 1 && b > 1) {
                    //System.out.println("entra a while_restric");
                    if (canBeChangeNewGr == true) {
                        newGr = new FunctionalGroupNode(MoleculeOperations.findNewRefCode(aValence, aGC, false));
                        if (gr.getGroupsCount() > 0) {
                            for (int i = 0; i < gr.getGroupsCount(); i++) {
                                //aValence=gr.getTemporalValence();
                                //	gr.getGroupAt(i).setValence(aValence+1);//incrementar valencia por qutar grupo
                                newGr.addGroup(gr.getGroupAt(i));
                            }
                        }
                    } else {
                        aG1 = new FunctionalGroupNode(MoleculeOperations.findNewRefCode(1, aGC, false));
                    }
                    aGC.getCodeOfRowBNameOrRefCode(aG1.getRootNode());
                    a = aGC.getCodeOfRow();
                    aGC.getCodeOfRowBNameOrRefCode(newGr.getRootNode());
                    b = aGC.getCodeOfRow();
                }
            }
            break;
        }
        newGr.addGroup(aG1);
    }

    /**public void typeOfOH(Groups aGroups){
     for(int i=0;i<aGroups.getGroupsCount();i++){
     if(aGroups.getRefCode()==2){
     System.out.println("cambio?");
     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh1");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else if(aGroups.getRefCode()==3){

     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh2");
     aGroups.setGroupAt(i, new Groups("OH1",operator));
     }


     } else  if(aGroups.getRefCode()==4){
     //System.out.println(aGroups.getGroupAt(i).getName());
     if(aGroups.getGroupAt(i).getPrincipalGroupCode()==5){
     System.out.println("cambio por oh3");
     aGroups.setGroupAt(i, new Groups("OH3",operator));
     }

     }
     }
     }
     */

}


