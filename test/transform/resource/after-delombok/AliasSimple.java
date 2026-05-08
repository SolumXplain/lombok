import lombok.Alias;

public class AliasSimple {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    private @org.jspecify.annotations.Nullable String defaultSector;

    public void test() {
        @org.jspecify.annotations.Nullable String code = "NYSE";
        System.out.println(code);
    }

    public void process(@org.jspecify.annotations.Nullable String code) {
        System.out.println(code);
    }


    public @org.jspecify.annotations.Nullable String getDefaultSector() {
        return defaultSector;
    }
}
