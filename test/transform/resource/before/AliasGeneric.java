import lombok.Alias;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AliasGeneric {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    private List<Sector> sectors;

    public List<Sector> getSectors() {
        return sectors;
    }

    public void process(List<Sector> codes) {
        System.out.println(codes);
    }

    public void test() {
        List<Sector> list = new java.util.ArrayList<>();
        Map<Sector, Integer> counts = new java.util.HashMap<>();
    }

    public void typeWitnessNested() {
        Map<String, Optional<Sector>> m = Collections.<String, Optional<Sector>>emptyMap();
    }

    public void typeWitnessNestedInLambda() {
        Function<String, Map<String, Optional<Sector>>> lambda = it -> Collections.<String, Optional<Sector>>emptyMap();
    }
}
