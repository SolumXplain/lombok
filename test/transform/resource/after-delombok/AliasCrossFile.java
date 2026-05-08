import com.example.Sector;

public class AliasCrossFile {
    private @org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String field;

    @org.jspecify.annotations.Nullable
    @lombok.Typed(Sector.class)
    public String getField() {
        return field;
    }

    public void setField(@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String s) {
        this.field = s;
    }

    public void process() {
        @org.jspecify.annotations.Nullable
        @lombok.Typed(Sector.class)
        String local = field;
        System.out.println(local);
    }
}
