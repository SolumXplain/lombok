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
}
