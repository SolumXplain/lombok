import lombok.Alias;

public class AliasSimple {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    @org.jspecify.annotations.Nullable
    private String defaultSector;

    public void test() {
        @org.jspecify.annotations.Nullable
        String code = "NYSE";
        System.out.println(code);
    }

    public void process(@org.jspecify.annotations.Nullable String code) {
        System.out.println(code);
    }


    @org.jspecify.annotations.Nullable
    public String getDefaultSector() {
        return defaultSector;
    }
}
