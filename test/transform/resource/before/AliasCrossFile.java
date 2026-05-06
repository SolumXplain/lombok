import com.example.Sector;

public class AliasCrossFile {
    private Sector field;

    public Sector getField() {
        return field;
    }

    public void setField(Sector s) {
        this.field = s;
    }

    public void process() {
        Sector local = field;
        System.out.println(local);
    }
}
