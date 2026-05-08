import lombok.Alias;

public class AliasSimple {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    private @org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String defaultSector;

    public void test() {
        @org.jspecify.annotations.Nullable
        @lombok.Typed(Sector.class)
        String code = "NYSE";
        System.out.println(code);
    }

    public void process(@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String code) {
        System.out.println(code);
    }


    @org.jspecify.annotations.Nullable
    @lombok.Typed(Sector.class)
    public String getDefaultSector() {
        return defaultSector;
    }
}
