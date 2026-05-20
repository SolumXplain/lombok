import lombok.Alias;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

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

    public void typeWitnessNestedInLambda() {
        Function<String, Map<String, Optional<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>>> lambda = it -> Collections.<String, Optional<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>>emptyMap();
    }

    public void typeAsLambdaParam() {
        var lambda = (Function<String, Optional<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>>) it -> Optional.empty();
    }

    public void typeOnLambdaArg() {
        Function<List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>, Integer> lambda = (List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> sectors) -> sectors.size();
    }
}
