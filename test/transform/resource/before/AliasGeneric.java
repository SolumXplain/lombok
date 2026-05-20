import lombok.Alias;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AliasGeneric {
    @Alias(of = String.class)
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
        List<List<Sector>> chunked = Collections.<List<Sector>>emptyList();
        Map<Sector, Integer> counts = new java.util.HashMap<>();
    }

    public void typeWitnessNested() {
        Map<String, Optional<Sector>> m = Collections.<String, Optional<Sector>>emptyMap();
    }

    public void typeWitnessNestedInLambda() {
        Function<String, Map<String, Optional<Sector>>> lambda = it -> Collections.<String, Optional<Sector>>emptyMap();
    }

    public void typeAsLambdaParam() {
        var lambda = (Function<String, Optional<Sector>>) (it) -> Optional.empty();
    }

    public void typeOnLambdaArg() {
        Function<List<Sector>, Integer> lambda = (List<Sector> sectors) -> sectors.size();
    }

    public void typeOnLambdaInMethodArg() {
        Collections.<List<Sector>>emptyList().forEach((List<Sector> c) -> {
            System.out.println(c);
        });
    }
}
