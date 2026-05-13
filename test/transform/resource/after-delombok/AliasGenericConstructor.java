import lombok.Alias;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AliasGenericConstructor {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    public void test() {
        List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = new ArrayList<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>();
        list.add("NYSE");
    }

    // new ArrayList<Alias>() passed directly as a method argument
    public void passAsArg() {
        consume(new ArrayList<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>());
    }

    // Mapstruct-generated pattern: local is replaced but the new ArrayList<Alias>(local)
    // in the next statement is a method-call argument, not a variable initializer
    public void mapstructLike() {
        List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = getSectors();
        if (list != null) {
            setSectors(new ArrayList<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>(list));
        }
    }

    // Explicit type witness on a method call: Collections.<Sector>emptyList()
    public void typeWitnessAsArg() {
        consume(Collections.<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>emptyList());
    }

    // Explicit type witness on a variable initializer
    public void typeWitnessInit() {
        List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = Collections.<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>emptyList();
    }

    private List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> getSectors() {
        return null;
    }

    private void setSectors(List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> sectors) {
    }

    private void consume(List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> sectors) {
    }
}
