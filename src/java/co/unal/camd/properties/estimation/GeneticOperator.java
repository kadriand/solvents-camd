package co.unal.camd.properties.estimation;

import co.unal.camd.control.parameters.ContributionGroupsManager;
import co.unal.camd.ga.haea.MoleculeOperations;


public class GeneticOperator {

    public static void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, FunctionalGroupNode aGrToReplace, boolean replaceAll, ContributionGroupsManager aGC) {
        FunctionalGroupNode aGroup = new FunctionalGroupNode(0);
        //System.out.println("CCoperator");
        //	System.out.println("c:"+genotype.getGroupsCount());
        //	System.out.println("code:"+codeToCut);

        if (codeToCut == 0) {
            if (!replaceAll) {
                for (int j = 0; j < genotype.getGroupsCount(); j++) {
                    FunctionalGroupNode subG = genotype.getGroupAt(j);
                    aGrToReplace.addGroup(subG);
                }
            }
            //System.out.println("CCoperator");
            genotype = aGrToReplace;
            //System.out.println("nnnn"+genotype.getGroupAt(i).toString());

        } else {
            codeToCut--;
            int i = 0;
            while (codeToCut >= genotype.getGroupAt(i).getTotalGroupsCount()) {
                codeToCut -= genotype.getGroupAt(i).getTotalGroupsCount();
                i++;
            }
            searchAndReplace(genotype.getGroupAt(i), codeToCut, replaceAll, aGC);
        }
        /**
         if(genotype.getGroupsCount()>0){
         //System.out.println("aaa");
         for(int i=0;i<genotype.getGroupsCount();i++){
         codeToCut=codeToCut-1;
         if(codeToCut==0){
         aGroup=genotype.getGroupAt(i);
         if(replaceAll==false){
         for(int j=0;j<genotype.getGroupAt(i).getGroupsCount();j++){
         FunctionalGroupNode subG=genotype.getGroupAt(i).getGroupAt(j);
         aGrToReplace.addGroup(subG);
         }
         }
         //System.out.println("CCoperator");
         genotype.setGroupAt(i, aGrToReplace);
         //System.out.println("nnnn"+genotype.getGroupAt(i).toString());
         }
         searchAndReplace(genotype.getGroupAt(i),codeToCut,aGrToReplace,replaceAll,contributionGroups);
         }
         }
         */
    }

    public static void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, boolean replaceAll, ContributionGroupsManager aGC) {
        if (codeToCut == 0) {
            int valence = aGC.getGroupValence((genotype.getRootNode()));
            aGC.getCodeOfRowBNameOrRefCode(genotype.getRootNode());
            int new_code = MoleculeOperations.findNewRefCode(valence, aGC, (aGC.getCodeOfRow() > 1));
            genotype.setRootNode(new_code);

        } else {
            codeToCut--;
            int i = 0;
            while (codeToCut >= genotype.getGroupAt(i).getTotalGroupsCount()) {
                codeToCut -= genotype.getGroupAt(i).getTotalGroupsCount();
                i++;
            }
            searchAndReplace(genotype.getGroupAt(i), codeToCut, replaceAll, aGC);
//				for(int i=0; i<genotype.getGroupsCount();i++){
            //				code=code+1;
            //			if(code==codeToCut){
//					}
            //searchAndReplace(genotype.getGroupAt(i),codeToCut, replaceAll, contributionGroups);
//				}
        }
    }

}
