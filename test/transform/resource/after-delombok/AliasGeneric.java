import lombok.Alias;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AliasGeneric {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    private List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> sectors;

    public List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> getSectors() {
        return sectors;
    }

    public void process(List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> codes) {
        System.out.println(codes);
    }

    public void test() {
        List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = new java.util.ArrayList<>();
        Map<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String, Integer> counts = new java.util.HashMap<>();
    }

    public void typeWitnessNested() {
        Map<String, Optional<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>> m = Collections.<String, Optional<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>>emptyMap();
    }
}
