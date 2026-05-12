import lombok.Alias;
import java.util.ArrayList;
import java.util.List;

public class AliasGenericConstructor {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    public void test() {
        List<Sector> list = new ArrayList<Sector>();
        list.add("NYSE");
    }

    // new ArrayList<Alias>() passed directly as a method argument
    public void passAsArg() {
        consume(new ArrayList<Sector>());
    }

    // Mapstruct-generated pattern: local is replaced but the new ArrayList<Alias>(local)
    // in the next statement is a method-call argument, not a variable initializer
    public void mapstructLike() {
        List<Sector> list = getSectors();
        if (list != null) {
            setSectors(new ArrayList<Sector>(list));
        }
    }

    private List<Sector> getSectors() { return null; }
    private void setSectors(List<Sector> sectors) {}
    private void consume(List<Sector> sectors) {}
}
