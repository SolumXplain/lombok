import lombok.Alias;

public class AliasSimple {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    public void test() {
        Sector code = "NYSE";
        System.out.println(code);
    }
}
