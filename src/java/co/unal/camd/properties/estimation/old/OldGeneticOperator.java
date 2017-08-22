package co.unal.camd.properties.estimation.old;

import co.unal.camd.control.parameters.ContributionParametersManager;
import co.unal.camd.ga.haea.MoleculeOperations;
import co.unal.camd.properties.estimation.FunctionalGroupNode;
import unalcol.agents.simulate.Environment;


public abstract class OldGeneticOperator extends Operator {

    public FunctionalGroupNode genotype1;
    public FunctionalGroupNode genotype2;

    public OldGeneticOperator(Environment _environment) {
        super(_environment);
    }


    public void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, FunctionalGroupNode aGrToReplace, boolean replaceAll, ContributionParametersManager aGC) {
        FunctionalGroupNode aGroup = new FunctionalGroupNode(0);
        //System.out.println("CCoperator");
        //	System.out.println("c:"+genotype.getGroupsCount());
        //	System.out.println("code:"+codeToCut);

        if (codeToCut == 0) {
            if (replaceAll == false) {
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
         searchAndReplace(genotype.getGroupAt(i),codeToCut,aGrToReplace,replaceAll,parametersManager);
         }
         }
         */
    }

    public void searchAndReplace(FunctionalGroupNode genotype, int codeToCut, boolean replaceAll, ContributionParametersManager aGC) {
        if (codeToCut == 0) {
            int valence = aGC.getValence((genotype.getRootNode()));
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
            //searchAndReplace(genotype.getGroupAt(i),codeToCut, replaceAll, parametersManager);
//				}
        }
    }

}
