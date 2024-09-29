package piecegluing.data.noncoord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import piecegluing.circular.CircularAlgorithm;

public class Polygon {

	public class Edge {
		public final int neighborIndex;
		public final int neighborEdgeIndex;

		public Edge(final int neighborIndex, final int neighborEdgeIndex) {
			this.neighborIndex = neighborIndex;
			this.neighborEdgeIndex = neighborEdgeIndex;
		}

		public Polygon neighbor(final List<Polygon> polygons) {

			if (neighborIndex < 0 || neighborIndex > polygons.size()) {
				return null;
			}

			return polygons.get(neighborIndex);
		}

		@Override
		public String toString() {
			return "p:" + neighborIndex + " e:" + neighborEdgeIndex;
		}
	}

	private final List<Integer> angles = new ArrayList<>();
	private final List<Edge> edges = new ArrayList<>();
	private String label;

	public Polygon(final int angleCount) {

		angles.addAll(IntStream.range(0, angleCount)
			.map(v -> 0)
			.boxed()
			.toList());

		edges.addAll(IntStream.range(0, angleCount)
			.mapToObj(v -> new Edge(-1, -1))
			.toList());

	}

	public Polygon(final Polygon polygon) {
		this(polygon.angles);

		edges.clear();
		edges.addAll(polygon.edges);
		this.label = polygon.label;
	}

	protected Polygon(final List<Integer> angles) {
		this(angles.size());

		this.angles.clear();
		this.angles.addAll(angles);
	}

	public void setAngle(final int index, final int angle) {
		angles.set(index, angle);
	}

	public int getAngle(final int index) {
		return CircularAlgorithm.get(angles, index);
	}

	public int getAngleCount() {
		return angles.size();
	}

	public void setEdge(final int index, final int neighborIndex, final int neighborEdgeIndex) {
		edges.set(index, new Edge(neighborIndex, neighborEdgeIndex));
	}

	public Edge getEdge(final int index) {
		return CircularAlgorithm.get(edges, index);
	}

	public void setLabel(final String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public Polygon toCanonical() {
		var canonical = new Polygon(CircularAlgorithm.createCanonicalOnSymmetry(angles, Integer::compare));

		canonical.setLabel(label);
		return canonical;
	}

	public Polygon copy() {
		var p = new Polygon(this);

		return p;
	}

	public Polygon rotate(final int firstIndex) {

		var rotatedAngles = CircularAlgorithm.createShiftedValues(angles, firstIndex);

		var p = new Polygon(rotatedAngles);

		p.label = label;

		return p;
	}

	public Polygon reverse() {

		var reversedAngles = new ArrayList<Integer>(angles);
		Collections.reverse(reversedAngles);

		var p = new Polygon(reversedAngles);

		p.label = label;

		return p;

	}

	public List<Double> createTriangleLengthRatios() {
		if (getAngleCount() != 3) {
			throw new RuntimeException("For triangle only.");
		}

		var ratios = new ArrayList<Double>();
		var angleSum = angles.stream()
			.reduce((s, v) -> s + v)
			.get();

		ratios.add(Math.sin(Math.PI * angles.get(2) / angleSum));
		ratios.add(Math.sin(Math.PI * angles.get(0) / angleSum));
		ratios.add(Math.sin(Math.PI * angles.get(1) / angleSum));

		return ratios;
	}

	public int angleSum() {
		return angles.stream()
			.reduce((s, v) -> s + v)
			.get();

	}

	public double getDoubleAngle(final int index) {
		return Math.PI * (getAngleCount() - 2) * angles.get(index) / angleSum();
	}

	@Override
	public int hashCode() {
		return Objects.hash(angles);
	}

	@Override
	public boolean equals(final Object obj) {

		if (!(obj instanceof Polygon)) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		var p = (Polygon) obj;

		return angles.equals(p.angles);
	}

	public String toAngleString() {
		return angles.toString();
	}

	@Override
	public String toString() {
		var angleText = toAngleString();

		var edgeText = edges.toString();

		return angleText + " e=" + edgeText;
	}
}
