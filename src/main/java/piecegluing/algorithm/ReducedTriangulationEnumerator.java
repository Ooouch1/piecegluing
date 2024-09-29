package piecegluing.algorithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import piecegluing.circular.CircularAlgorithm;
import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

/**
 * Proposed algorithm based on the algorithm by Horiyama et al.
 */
public class ReducedTriangulationEnumerator extends AbstractTriangulationEnumerator {
	private List<Triangulation> answers;

	public ReducedTriangulationEnumerator(final List<Polygon> canonicalPieces, final int piUnit) {
		super(canonicalPieces, new ConvexityTest(piUnit));
	}

	@Override
	public List<Triangulation> apply(final Integer angleCount) {
		enumerate(angleCount - 2);

		return answers;
	}

	private void enumerate(final int requiredPieceCount) {

		var answerMapList = new ArrayList<Map<Map<List<Integer>, List<BigDecimal>>, Triangulation>>();

		answerMapList.add(new HashMap<>());

		var answerMap = answerMapList.get(0);

		// initialize
		for (var piece : getPieces()) {
			var pieceTriangulation = new Triangulation(new Polygon(piece));
			answerMap.put(createKey(pieceTriangulation), pieceTriangulation);
		}

		for (int i = 1; i < requiredPieceCount; i++) {
			var prevAnswerMap = answerMapList.get(i - 1);
			answerMap = new HashMap<>();

			for (var prevTriangulation : prevAnswerMap.values()) {
				for (int o = 0; o < prevTriangulation.getOuterEdgeCount(); o++) {
					for (var piece : getMovedPieces()) {
						var triangulation = prevTriangulation.copy();

						triangulation.glue(piece.copy(), 0, o);

						if (!getAnswerTest().apply(triangulation)) {
							continue;
						}

						answerMap.put(createKey(triangulation), triangulation);
					}
				}
			}

			answerMapList.add(answerMap);
		}

		answers = answerMapList.get(requiredPieceCount - 1)
			.values()
			.stream()
			.toList();
	}

	private Map<List<Integer>, List<BigDecimal>> createKey(final Triangulation triangulation) {

		var lengthPattern = new EdgeLengthEncoder().apply(triangulation);
		var anglePattern = CircularAlgorithm.createCanonicalOnSymmetry(triangulation.getMergedAngles(),
			Integer::compare);

		return Map.of(anglePattern, lengthPattern);
	}
}
