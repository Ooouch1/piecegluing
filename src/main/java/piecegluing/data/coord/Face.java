package piecegluing.data.coord;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import piecegluing.data.noncoord.Polygon;

public class Face {
	private final List<Vertex> vertices = new ArrayList<>();

	private final List<DirectedEdge> edges = new ArrayList<>();

	private String label;

	public Face(final List<Vertex> vertices) {
		this.vertices.addAll(vertices);
		buildEdges();
	}

	/**
	 * 
	 * @param polygon  triangle only.
	 * @param sizeRate
	 */
	public Face(final Polygon polygon, final double sizeRate) {
		this(polygon, polygon.createTriangleLengthRatios()
			.stream()
			.map(r -> r * sizeRate)
			.toList());
	}

	public Face(final Polygon polygon, final List<Double> lengths) {
		double x = 0;
		double y = 0;
		double angle = 0;

		vertices.add(new Vertex(x, y));

		for (int i = 1; i < polygon.getAngleCount(); i++) {
			x = x + lengths.get(i - 1) * Math.cos(angle);
			y = y + lengths.get(i - 1) * Math.sin(angle);
			vertices.add(new Vertex(x, y));

			angle = angle + Math.PI - polygon.getDoubleAngle(i);
		}

		buildEdges();

		label = polygon.getLabel();
	}

	private void buildEdges() {
		for (int i = 0; i < vertices.size(); i++) {
			edges.add(new DirectedEdge(vertices.get(i), vertices.get((i + 1) % vertices.size())));
		}
	}

	public Vertex center() {
		return new Vertex((minX() + maxX()) / 2, (minY() + maxY()) / 2);
	}

	public double minX() {
		return vertices.stream()
			.mapToDouble(Vertex::getX)
			.min()
			.getAsDouble();
	}

	public double maxX() {
		return vertices.stream()
			.mapToDouble(Vertex::getX)
			.max()
			.getAsDouble();
	}

	public double minY() {
		return vertices.stream()
			.mapToDouble(Vertex::getY)
			.min()
			.getAsDouble();
	}

	public double maxY() {
		return vertices.stream()
			.mapToDouble(Vertex::getY)
			.max()
			.getAsDouble();
	}

	public Vertex centroid() {
		return vertices.stream()
			.reduce(Vertex::add)
			.get()
			.multiply(1.0 / vertices.size());
	}

	public Vertex getVertex(final int index) {
		return vertices.get((index + getVertexCount()) % getVertexCount());
	}

	public int getVertexCount() {
		return vertices.size();
	}

	public DirectedEdge getEdge(final int index) {
		return edges.get(index);
	}

	public String getLabel() {
		return label;
	}

	public Face transform(final AffineTransform affine) {
		var face = new Face(vertices.stream()
			.map(v -> v.transform(affine))
			.toList());

		face.label = label;

		return face;
	}

	public Iterable<Vertex> verticesIterable() {
		return vertices;
	}

	@Override
	public String toString() {
		return vertices.toString();
	}
}
