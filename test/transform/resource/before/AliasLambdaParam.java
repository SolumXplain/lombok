import lombok.Alias;
import java.util.function.Function;

public class AliasLambdaParam {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    // Lambda with explicit param type as variable initializer
    public void varInit() {
        Function<Sector, Integer> f = (Sector x) -> x.length();
    }

    // Lambda as method argument
    public void asArg() {
        process((Sector x) -> x.length());
    }

    private void process(Function<Sector, Integer> fn) {}
}
