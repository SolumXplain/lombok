import lombok.Alias;
import java.util.ArrayList;
import java.util.List;

public class AliasGenericConstructor {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    public void test() {
        List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = new ArrayList<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>();
        list.add("NYSE");
    }
}
