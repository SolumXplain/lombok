import lombok.Alias;
import java.util.function.Function;

public class AliasLambdaParam {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    // Lambda with explicit param type as variable initializer
    public void varInit() {
        Function<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String, Integer> f = (@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String x) -> x.length();
    }

    // Lambda as method argument
    public void asArg() {
        process((@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String x) -> x.length());
    }

    private void process(Function<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String, Integer> fn) {
    }
}
