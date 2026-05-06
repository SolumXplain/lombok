import com.example.Sector;

public class AliasCrossFile {
    @org.jspecify.annotations.Nullable
    private String field;

    @org.jspecify.annotations.Nullable
    public String getField() {
        return field;
    }

    public void setField(@org.jspecify.annotations.Nullable String s) {
        this.field = s;
    }

    public void process() {
        @org.jspecify.annotations.Nullable
        String local = field;
        System.out.println(local);
    }
}
