import lombok.Alias;
import java.util.Map;

public class AliasCast {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

    private Map<Sector, Integer> map;

    public Sector fromObject(Object obj) {
        return (Sector) obj;
    }

    public void test(Object obj) {
        Sector s = (Sector) obj;
        System.out.println(s);
    }

    public void testGeneric(Object obj) {
        java.util.List<Sector> list = (java.util.List<Sector>) obj;
        System.out.println(list);
    }

    // Cast on the RHS of a field assignment
    public void testAssignCast(Object obj) {
        this.map = (Map<Sector, Integer>) obj;
    }

    // Cast passed directly as a method argument
    public void testArgCast(Object obj) {
        consume((Sector) obj);
    }

    private void consume(Sector s) {}

    // Cast inside an intermediate call of a method chain on a return statement
    public String testReturnMethodChainCast(Object obj, Object other) {
        return wrap((Sector) obj).concat(other.toString());
    }

    private String wrap(Sector s) { return ""; }
}
