package piecegluing.data.noncoord;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Triangulation {

	public class OuterEdge {
		public final int triangleIndex;
		public final int edgeIndex;

		public final double length;

		public OuterEdge(final int triangleIndex, final int edgeIndex, final double length) {
			this.triangleIndex = triangleIndex;
			this.edgeIndex = edgeIndex;
			this.length = length;
		}
	}

	private final List<OuterEdge> outerEdges = new ArrayList<>();
	private final List<Polygon> triangles = new ArrayList<>();

	protected Triangulation() {

	}

	public Triangulation(final Polygon triangle) {
		triangles.add(triangle);
		var lengthRatios = triangle.createTriangleLengthRatios();

		for (int i = 0; i < 3; i++) {
			outerEdges.add(new OuterEdge(0, i, lengthRatios.get(i)));
		}
	}

	/**
	 * 
	 * @param triangle       new triangle to be added
	 * @param edgeIndex      the index of the given triangle's edge to be glued. 0
	 *                       to 2.
	 * @param outerEdgeIndex the index of the outer edge to be glued.
	 */
	public void glue(final Polygon triangle, final int edgeIndex, final int outerEdgeIndex) {
		if (triangle.getLabel() == null) {
			throw new IllegalArgumentException("label of triangle is null!");
		}

		triangles.add(triangle);
		var triangleIndex = triangles.size() - 1;

		var lengthRatios = triangle.createTriangleLengthRatios();
		var sizeRate = outerEdges.get(outerEdgeIndex).length / lengthRatios.get(edgeIndex);

		var newEdgeIndex = (edgeIndex + 1) % 3;
		outerEdges.add(outerEdgeIndex,
			new OuterEdge(triangleIndex, newEdgeIndex, lengthRatios.get(newEdgeIndex) * sizeRate));

		newEdgeIndex = (edgeIndex + 2) % 3;
		outerEdges.add(outerEdgeIndex + 1,
			new OuterEdge(triangleIndex, newEdgeIndex, lengthRatios.get(newEdgeIndex) * sizeRate));

		var removedEdge = outerEdges.remove(outerEdgeIndex + 2);

		glue(removedEdge.triangleIndex, triangleIndex, removedEdge.edgeIndex, edgeIndex);
	}

	/**
	 * glue two triangles that are contained in this object.
	 * 
	 * @param triangle0
	 * @param triangle1
	 * @param edge0     0 to 2
	 * @param edge1     0 to 2
	 */
	protected void glue(final int triangle0, final int triangle1, final int edge0, final int edge1) {

		var p0 = triangles.get(triangle0);
		var p1 = triangles.get(triangle1);

		p0.setEdge(edge0, triangle1, edge1);
		p1.setEdge(edge1, triangle0, edge0);
	}

	public Polygon getTriangle(final int index) {
		return triangles.get(index);
	}

	/**
	 * The order of the parameters is considered: (i, j) is different from (j, i).
	 * 
	 * @param triangle0 first element of key
	 * @param triangle1 second element of key
	 * @return
	 */
	public boolean areGlued(final int triangle0, final int triangle1) {
		var p0 = triangles.get(triangle0);

		for (int i = 0; i < 3; i++) {

			var neighborIndex = p0.getEdge(i).neighborIndex;

			if (neighborIndex < 0 || neighborIndex >= triangles.size()) {
				continue;
			}

			if (neighborIndex == triangle1) {
				return true;
			}
		}
		return false;
	}

	public List<Integer> getGluedEdgeIndices(final int triangle0, final int triangle1) {
		var p0 = triangles.get(triangle0);
		var p1 = triangles.get(triangle1);

		int e0 = -1;
		int e1 = -1;

		for (int i = 0; i < 3; i++) {
			if (p0.getEdge(i).neighborIndex == triangle1) {
				e0 = i;
			}
		}

		for (int i = 0; i < 3; i++) {
			if (p1.getEdge(i).neighborIndex == triangle0) {
				e1 = i;
			}
		}

		return List.of(e0, e1);
	}

	public Stream<Polygon> trianglesStream() {
		return triangles.stream();
	}

	public int getOuterEdgeCount() {
		return outerEdges.size();
	}

	public Iterable<OuterEdge> outerEdgesIterable() {
		return outerEdges;
	}

	public List<BigDecimal> getNormalizedOuterEdgeLengths(final int scale) {
		var doubleLengths = new ArrayList<Double>();

		for (var outerEdge : outerEdgesIterable()) {
			doubleLengths.add(outerEdge.length);
		}

		var minLength = doubleLengths.stream()
			.min(Double::compare)
			.get();

		// normalize
		for (int i = 0; i < doubleLengths.size(); i++) {
			doubleLengths.set(i, doubleLengths.get(i) / minLength);
		}

		return doubleLengths.stream()
			.map(length -> BigDecimal.valueOf(length)
				.setScale(scale, RoundingMode.HALF_UP)
				.stripTrailingZeros())
			.toList();
	}

	public List<List<Integer>> createAnglePattern() {
		var pattern = new ArrayList<List<Integer>>();

		for (int i = 0; i < outerEdges.size(); i++) {
			var angles = new ArrayList<Integer>();

			var outerEdge = outerEdges.get(i);

			var triangle = triangles.get(outerEdge.triangleIndex);
			var e = outerEdge.edgeIndex;

			angles.add(triangle.getAngle(e + 1));

			var edge = triangle.getEdge(e + 1);
			while (edge.neighbor(triangles) != null) {
				triangle = edge.neighbor(triangles);
				e = edge.neighborEdgeIndex;

				angles.add(triangle.getAngle(e + 1));

				edge = triangle.getEdge(e + 1);
			}

			pattern.add(angles);
		}

		return pattern;
	}

	public Polygon createShapePolygon() {
		var mergedAngles = getMergedAngles();

		var shape = new Polygon(mergedAngles.size());

		for (int i = 0; i < mergedAngles.size(); i++) {
			shape.setAngle(i, mergedAngles.get(i));
		}

		return shape.toCanonical();
	}

	public List<Integer> getMergedAngles() {
		return createAnglePattern().stream()
			.map(a -> a.stream()
				.mapToInt(v -> v)
				.sum())
			.toList();

	}

	public Triangulation copy() {
		var t = new Triangulation();

		t.outerEdges.addAll(outerEdges);
		t.triangles.addAll(triangles.stream()
			.map(Polygon::copy)
			.toList());

		return t;
	}

	@Override
	public String toString() {
		return triangles.toString();
	}
}
