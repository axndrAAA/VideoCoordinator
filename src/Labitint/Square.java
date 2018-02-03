package Labitint;

public class Square {
    private int y;
    private int x;

    public Square(int x, int y) {
        this.y = y;
        this.x = x;
    }

    public Square clone() {
        return new Square(this.x, this.y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Square square = (Square) o;

        if (y != square.y) return false;
        if (x != square.x) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = y;
        result = 31 * result + x;
        return result;
    }
}