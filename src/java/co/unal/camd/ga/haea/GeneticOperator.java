package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.FunctionalGroupNode;
import co.unal.camd.view.CamdRunner;


public class GeneticOperator {

    public static final void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, FunctionalGroupNode aGrToReplace, boolean replaceAll) {
        FunctionalGroupNode aGroup = new FunctionalGroupNode(0);
        //System.out.println("CCoperator");
        //	System.out.println("c:"+genotype.countSubgroups());
        //	System.out.println("code:"+codeToCut);

        if (codeToCut == 0) {
            if (!replaceAll) {
                for (int j = 0; j < genotype.countSubgroups(); j++) {
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
            while (codeToCut >= genotype.getGroupAt(i).countTotalGroups()) {
                codeToCut -= genotype.getGroupAt(i).countTotalGroups();
                i++;
            }
            searchAndReplace(genotype.getGroupAt(i), codeToCut, replaceAll);
        }
        /**
         if(genotype.countSubgroups()>0){
         //System.out.println("aaa");
         for(int i=0;i<genotype.countSubgroups();i++){
         codeToCut=codeToCut-1;
         if(codeToCut==0){
         aGroup=genotype.getGroupAt(i);
         if(replaceAll==false){
         for(int j=0;j<genotype.getGroupAt(i).countSubgroups();j++){
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

    public static void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, boolean replaceAll) {
        if (codeToCut == 0) {
            int valence = CamdRunner.CONTRIBUTION_GROUPS.findGroupValence((genotype.getRootNode()));
            CamdRunner.CONTRIBUTION_GROUPS.resolveValence(genotype.getRootNode());
            int new_code = MoleculeOperations.findNewRefCode(valence, CamdRunner.CONTRIBUTION_GROUPS.getCodeOfRow() > 1);
            genotype.setRootNode(new_code);

        } else {
            codeToCut--;
            int i = 0;
            while (codeToCut >= genotype.getGroupAt(i).countTotalGroups()) {
                codeToCut -= genotype.getGroupAt(i).countTotalGroups();
                i++;
            }
            searchAndReplace(genotype.getGroupAt(i), codeToCut, replaceAll);
            //				for(int i=0; i<genotype.countSubgroups();i++){
            //				code=code+1;
            //			if(code==codeToCut){
            //					}
            //searchAndReplace(genotype.getGroupAt(i),codeToCut, replaceAll, contributionGroups);
            //				}
        }
    }

}