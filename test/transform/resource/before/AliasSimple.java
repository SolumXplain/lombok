import lombok.Alias;

public class AliasSimple {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    private Sector defaultSector;

    public void test() {
        Sector code = "NYSE";
        System.out.println(code);
    }

    public void process(Sector code) {
        System.out.println(code);
    }

    public Sector getDefaultSector() {
        return defaultSector;
    }
}
