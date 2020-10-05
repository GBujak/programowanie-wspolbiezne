package instrukcja11.zadanie1;

class Rect {
    final private int x, y, width, height, rotation;

    public Rect(int x, int y, int width, int height, int rotation) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRotation() {
        return rotation;
    }

    @Override
    public String toString() {
        return "Rect{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", rotation=" + rotation +
                '}';
    }

    private int newRotation(int givenRotation) {
        if (givenRotation != 90 && givenRotation != -90)
            throw new IllegalStateException("Can only rotate by 90deg");
        return (rotation + givenRotation + 360) % 360;
    }

    public Rect rotatedRight() {
        return new Rect(x, y, width, height, newRotation(90));
    }

    public Rect rotatedLeft() {
        return new Rect(x, y, width, height, newRotation(-90));
    }

    public Rect moved(int x, int y) {
        return new Rect(this.x + x, this.y + y, width, height, rotation);
    }

    public Rect resized(int width, int height) {
        return new Rect(x, y, width, height, rotation);
    }
}

public class Zadanie1 {
    public static void main(String[] args) {
        final Rect rect = new Rect(10, 20, 10, 20, 0);
        System.out.println("initial: " + rect);
        System.out.println("rotated: " + rect.rotatedRight());
        System.out.println("moved:   " + rect.rotatedRight().moved(10, 10));
        System.out.println("resized: " + rect.rotatedRight().moved(10, 10).resized(100, 100));
    }
}
