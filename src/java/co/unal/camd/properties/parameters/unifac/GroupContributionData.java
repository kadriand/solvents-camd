package co.unal.camd.properties.parameters.unifac;

import lombok.Data;

@Data
public class GroupContributionData {
    private Integer groupId;
    private Integer mainGroup;
    private String groupName;
    private Double rParam;
    private Double qParam;

    private Double bji;
    private Double cji;

    public GroupContributionData(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return String.format("Interactions [%d][%d] : rParam:%f , qParam:%f , groupName:%f , mainGroup:%f , bji:%f , cji:%f", groupId, rParam, qParam, groupName, mainGroup, bji, cji);
    }
}
