package piecegluing.data.coord;

public class DirectedEdge {
	private final Vertex start;
	private final Vertex end;

	public DirectedEdge(final Vertex start, final Vertex end) {
		this.start = start;
		this.end = end;
	}

	public Vertex getStart() {
		return start;
	}

	public Vertex getEnd() {
		return end;
	}

	public DirectedEdge reverse() {
		return new DirectedEdge(end, start);
	}

	public double length() {
		return start.distance(end);
	}

	public double angle() {
		return Math.PI + Math.atan2(end.getY() - start.getY(), end.getX() - start.getX());
	}
}
