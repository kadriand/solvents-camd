package co.unal.camd.availability;

public enum CompoundSource {
    TEST(""),
    ZINC_WAITOK("http://zinc15.docking.org/substances/"),
    ZINC_BOUTIQUE("http://zinc15.docking.org/substances/"),
    ZINC_ANNOTATED("http://zinc15.docking.org/substances/"),
    EPA("https://comptox.epa.gov/dashboard/dsstoxdb/results?search=");

    private final String urlPrefix;

    CompoundSource(String weight) {
        this.urlPrefix = weight;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }
}
