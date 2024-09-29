package piecegluing.algorithm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

/**
 * Straightforward algorithm
 * 
 * @author OUCHI Koji
 *
 */
public class ReducedTriangulationEnumerator2 extends AbstractTriangulationEnumerator {
	private final int piUnit;

	public ReducedTriangulationEnumerator2(final List<Polygon> canonicalPieces, final int piUnit) {
		super(canonicalPieces, new ConvexityTest(piUnit));
		this.piUnit = piUnit;
	}

	@Override
	public List<Triangulation> apply(final Integer angleCount) {
		var allTriangulations = new TriangulationEnumerator(getPieces(), piUnit).apply(angleCount);
		var grouped = new ShapeGrouping().apply(allTriangulations);

		var reduced = new HashMap<Polygon, List<Triangulation>>();

		for (var polygon : grouped.keySet()) {
			var uniqueTriangulations = new HashMap<List<BigDecimal>, Triangulation>();
			for (var triangulation : grouped.get(polygon)) {
				var key = new EdgeLengthEncoder().apply(triangulation);

				uniqueTriangulations.putIfAbsent(key, triangulation);
			}
			reduced.put(polygon, uniqueTriangulations.values()
				.stream()
				.toList());
		}

		return reduced.values()
			.stream()
			.flatMap(List::stream)
			.toList();
	}
}