package co.unal.camd.ga.haea;

import co.unal.camd.properties.model.ContributionGroupNode;
import co.unal.camd.properties.parameters.unifac.ThermodynamicFirstOrderContribution;
import co.unal.camd.view.CamdRunner;


public class GeneticOperator {

    public static final void searchAndReplace(ContributionGroupNode genotype, int codeToCut, ContributionGroupNode aGrToReplace, boolean replaceAll) {
        ContributionGroupNode aGroup = new ContributionGroupNode(0);
        //System.out.println("CCoperator");
        //	System.out.println("c:"+rootContributionGroup.countSubgroups());
        //	System.out.println("code:"+codeToCut);

        if (codeToCut == 0) {
            if (!replaceAll) {
                for (int j = 0; j < genotype.getSubGroups().size(); j++) {
                    ContributionGroupNode subG = genotype.getGroupAt(j);
                    aGrToReplace.getSubGroups().add(subG);
                }
            }
            //System.out.println("CCoperator");
            genotype = aGrToReplace;
            //System.out.println("nnnn"+rootContributionGroup.getGroupAt(i).toString());

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
         if(rootContributionGroup.countSubgroups()>0){
         //System.out.println("aaa");
         for(int i=0;i<rootContributionGroup.countSubgroups();i++){
         codeToCut=codeToCut-1;
         if(codeToCut==0){
         aGroup=rootContributionGroup.getGroupAt(i);
         if(replaceAll==false){
         for(int j=0;j<rootContributionGroup.getGroupAt(i).countSubgroups();j++){
         FunctionalGroupNode subG=rootContributionGroup.getGroupAt(i).getGroupAt(j);
         aGrToReplace.getSubGroups().add(subG);
         }
         }
         //System.out.println("CCoperator");
         rootContributionGroup.setGroupAt(i, aGrToReplace);
         //System.out.println("nnnn"+rootContributionGroup.getGroupAt(i).toString());
         }
         searchAndReplace(rootContributionGroup.getGroupAt(i),codeToCut,aGrToReplace,replaceAll,contributionGroups);
         }
         }
         */
    }

    public static void searchAndReplace(ContributionGroupNode genotype, int codeToCut, boolean replaceAll) {
        if (codeToCut == 0) {
            ThermodynamicFirstOrderContribution groupContribution = CamdRunner.CONTRIBUTION_GROUPS.getThermodynamicFirstOrderContributionsGroups().get(genotype.getGroupCode());
            int new_code = MoleculeOperations.getNewGroupCode(groupContribution.getValence(), groupContribution.getMainGroup().getCode() > 1);
            genotype.setGroupCode(new_code);
        } else {
            codeToCut--;
            int i = 0;
            while (codeToCut >= genotype.getGroupAt(i).countTotalGroups()) {
                codeToCut -= genotype.getGroupAt(i).countTotalGroups();
                i++;
            }
            searchAndReplace(genotype.getGroupAt(i), codeToCut, replaceAll);
            //				for(int i=0; i<rootContributionGroup.countSubgroups();i++){
            //				code=code+1;
            //			if(code==codeToCut){
            //					}
            //searchAndReplace(rootContributionGroup.getGroupAt(i),codeToCut, replaceAll, contributionGroups);
            //				}
        }
    }

}
