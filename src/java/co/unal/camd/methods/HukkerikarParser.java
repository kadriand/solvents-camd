package co.unal.camd.methods;

import co.unal.camd.methods.hukkerikar.HukkerikarFirstOrderContribution;
import co.unal.camd.methods.hukkerikar.HukkerikarSecondOrderContribution;
import co.unal.camd.model.molecule.UnifacGroupNode;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static co.unal.camd.methods.ParametersParserUtils.validateNumericCell;

public class HukkerikarParser {

    static Map<Integer, Integer> hukkerikarGroupsEquivalences;
    static Map<Integer, HukkerikarFirstOrderContribution> hukkerikarFirstOrderContributions = new HashMap<>();

    static void readFirstOrderHukkerikarThermoPhysicalContribution(HukkerikarFirstOrderContribution firstOrderContribution, XSSFRow currentRow) {
        XSSFCell rowCell;
        if (firstOrderContribution.getGroupName() == null) {
            rowCell = currentRow.getCell(1);
            if (rowCell != null)
                firstOrderContribution.setGroupName(rowCell.getStringCellValue());
        }

        HukkerikarFirstOrderContribution.ThermoPhysical thermoPhysicalContribution = firstOrderContribution.getThermoPhysical();

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setGibbsFreeEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(20);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setLiquidMolarVolume(rowCell.getNumericCellValue());
    }

    static void readFirstOrderHukkerikarEnvironmentalContribution(HukkerikarFirstOrderContribution firstOrderContribution, XSSFRow currentRow) {
        XSSFCell rowCell;
        if (firstOrderContribution.getGroupName() == null) {
            rowCell = currentRow.getCell(1);
            if (rowCell != null)
                firstOrderContribution.setGroupName(rowCell.getStringCellValue());
        }

        HukkerikarFirstOrderContribution.Environmental environmentalContribution = firstOrderContribution.getEnvironmental();

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50FM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50DM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            environmentalContribution.setOralLD50(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLogWS(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterBFC(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(13);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAnc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(14);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(15);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAnc(rowCell.getNumericCellValue());
    }

    static HukkerikarSecondOrderContribution readHukkerikarSecondOrderConfigurations(XSSFRow currentRow) {
        Integer groupId = (int) currentRow.getCell(0).getNumericCellValue();
        HukkerikarSecondOrderContribution secondOrderContribution = new HukkerikarSecondOrderContribution(groupId);

        XSSFCell rowCell = currentRow.getCell(1);
        if (rowCell != null)
            secondOrderContribution.setGroupDescription(rowCell.getStringCellValue());

        rowCell = currentRow.getCell(3);
        if (rowCell == null)
            return secondOrderContribution;

        String rawGroupCases = rowCell.getCellTypeEnum() == CellType.NUMERIC ? NumberToTextConverter.toText(rowCell.getNumericCellValue()) : rowCell.getStringCellValue().trim();
        String[] differentAlternatives = rawGroupCases.split("/");
        for (String differentAlternative : differentAlternatives) {
            List<String> singleConfigurations = ParametersParserUtils.breakConfigurationAlternatives(differentAlternative);
            singleConfigurations.forEach(singleConfiguration -> {
                UnifacGroupNode secondGroupConfiguration = ParametersParserUtils.parseNodeGroupsConfiguration(singleConfiguration);
                secondOrderContribution.getGroupConfigurations().add(secondGroupConfiguration);
                appendToEnvironmentalFirstOrderContributions(singleConfiguration, secondGroupConfiguration, secondOrderContribution);
            });
        }
        return secondOrderContribution;
    }

    private static void appendToEnvironmentalFirstOrderContributions(String singleConfiguration, UnifacGroupNode secondGroupConfiguration, HukkerikarSecondOrderContribution secondOrderContribution) {
        String[] rawGroups = singleConfiguration.split("[|().]+");
        int[] groups = Stream.of(rawGroups).mapToInt(Integer::parseInt).toArray();
        int biggerGroup = Arrays.stream(groups).max().getAsInt();
        Integer hukkerikarEquivalence = hukkerikarGroupsEquivalences.get(biggerGroup);
        if (hukkerikarFirstOrderContributions.containsKey(hukkerikarEquivalence))
            hukkerikarFirstOrderContributions.get(hukkerikarEquivalence).getSecondOrderContributions().put(secondGroupConfiguration, secondOrderContribution);
    }

    static void readSecondOrderEnvironmentalContribution(HukkerikarSecondOrderContribution secondOrderContribution, XSSFRow currentRow) {
        XSSFCell rowCell;

        if (secondOrderContribution.getGroupDescription() == null) {
            rowCell = currentRow.getCell(1);
            if (rowCell != null)
                secondOrderContribution.setGroupDescription(rowCell.getStringCellValue());
        }

        HukkerikarSecondOrderContribution.Environmental environmentalContribution = secondOrderContribution.getEnvironmental();

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50FM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(3);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLC50DM(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(4);
        if (validateNumericCell(rowCell))
            environmentalContribution.setOralLD50(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(5);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterLogWS(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            environmentalContribution.setWaterBFC(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(9);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(10);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirEUAnc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(11);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAc(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(12);
        if (validateNumericCell(rowCell))
            environmentalContribution.setAirERAnc(rowCell.getNumericCellValue());
    }

    static void readSecondOrderThermoPhysicalContribution(HukkerikarSecondOrderContribution secondOrderContribution, XSSFRow currentRow) {
        XSSFCell rowCell;

        if (secondOrderContribution.getGroupDescription() == null) {
            rowCell = currentRow.getCell(1);
            if (rowCell != null)
                secondOrderContribution.setGroupDescription(rowCell.getStringCellValue());
        }

        HukkerikarSecondOrderContribution.ThermoPhysical thermoPhysicalContribution = secondOrderContribution.getThermoPhysical();

        rowCell = currentRow.getCell(2);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setBoilingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(6);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setMeltingPoint(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(7);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setGibbsFreeEnergy(rowCell.getNumericCellValue());

        rowCell = currentRow.getCell(20);
        if (validateNumericCell(rowCell))
            thermoPhysicalContribution.setLiquidMolarVolume(rowCell.getNumericCellValue());
    }
}
