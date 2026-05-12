import lombok.Alias;

public class AliasCast {
    @Alias(of = String.class, annotated = org.jspecify.annotations.Nullable.class)
    interface Sector {}

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
}
