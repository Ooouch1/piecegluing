package piecegluing.data.coord;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class Vertex {
	private final double x;
	private final double y;

	public Vertex(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public Vertex add(final Vertex v) {
		return new Vertex(x + v.x, y + v.y);
	}

	public Vertex multiply(final double a) {
		return new Vertex(a * x, a * y);
	}

	public Vertex transform(final AffineTransform affine) {
		var before = new Point2D.Double(x, y);
		var after = new Point2D.Double();
		affine.transform(before, after);

		return new Vertex(after.getX(), after.getY());
	}

	public double distance(final Vertex v) {
		return Math.sqrt((x - v.x) * (x - v.x) + (y - v.y) * (y - v.y));
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
