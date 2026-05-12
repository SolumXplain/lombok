import lombok.Alias;

public class AliasGenericClass {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    static class SectorList extends java.util.ArrayList<Sector> {
    }

    interface SectorSupplier extends java.util.function.Supplier<Sector> {
    }
}
