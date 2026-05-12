import lombok.Alias;

public class AliasGenericClass {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    static class SectorList extends java.util.ArrayList<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> {
    }

    interface SectorSupplier extends java.util.function.Supplier<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> {
    }
}
