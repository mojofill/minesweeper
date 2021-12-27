import java.util.Objects;

public class Coordinate {
    public int x;
    public int y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (! (obj instanceof Coordinate)) { // not a coordinate - auto false
            return false;
        }

        Coordinate cast = (Coordinate) obj;

        return cast.x == this.x && cast.y == this.y; // only check the x and y, nothing else
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y); // unique for each coordinate
    }
}
