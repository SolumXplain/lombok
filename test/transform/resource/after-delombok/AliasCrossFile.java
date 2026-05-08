import com.example.Sector;

public class AliasCrossFile {
    private java.lang.@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String field;

    public java.lang.@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String getField() {
        return field;
    }

    public void setField(java.lang.@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String s) {
        this.field = s;
    }

    public void process() {
        java.lang.@org.jspecify.annotations.Nullable @lombok.Typed(Sector.class) String local = field;
        System.out.println(local);
    }
}
