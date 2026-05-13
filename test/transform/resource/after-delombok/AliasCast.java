import lombok.Alias;
import java.util.Map;

public class AliasCast {

    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {
    }

    private Map<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String, Integer> map;

    @org.jspecify.annotations.Nullable
    @lombok.Typed(Sector.class)
    public String fromObject(Object obj) {
        return (@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String) obj;
    }

    public void test(Object obj) {
        @org.jspecify.annotations.Nullable
        @lombok.Typed(Sector.class)
        String s = (@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String) obj;
        System.out.println(s);
    }

    public void testGeneric(Object obj) {
        java.util.List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String> list = (java.util.List<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String>) obj;
        System.out.println(list);
    }

    // Cast on the RHS of a field assignment
    public void testAssignCast(Object obj) {
        this.map = (Map<@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String, Integer>) obj;
    }

    // Cast passed directly as a method argument
    public void testArgCast(Object obj) {
        consume((@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String) obj);
    }

    private void consume(@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String s) {
    }
}
