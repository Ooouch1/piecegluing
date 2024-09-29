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
 * algorithm in Appendix. Unique gluing patterns of triangles allowing
 * duplication of outer shapes.
 */
public class TriangulationEnumerator extends AbstractTriangulationEnumerator {

	private List<Triangulation> answers;

	public TriangulationEnumerator(final List<Polygon> canonicalPieces, final int piUnit) {
		super(canonicalPieces, new ConvexityTest(piUnit));
	}

	@Override
	public List<Triangulation> apply(final Integer angleCount) {
		enumerate(angleCount - 2);

		return answers;
	}

	/**
	 * straightforward implementation.
	 * 
	 * @param requiredPieceCount
	 */
	private void enumerateAll(final int requiredPieceCount) {
		var answerMapList = new ArrayList<Map<Map<List<Integer>, List<BigDecimal>>, List<Triangulation>>>();

		answerMapList.add(new HashMap<>());

		var answerMap = answerMapList.get(0);

		// initialize
		for (var piece : getPieces()) {
			var pieceTriangulation = new Triangulation(new Polygon(piece));
			var list = new ArrayList<Triangulation>();
			list.add(pieceTriangulation);
			answerMap.put(createKey(pieceTriangulation), list);
		}

		for (int i = 1; i < requiredPieceCount; i++) {
			var prevAnswerMap = answerMapList.get(i - 1);
			answerMap = new HashMap<>();

			for (var prevTriangulations : prevAnswerMap.values()) {
				for (var prevTriangulation : prevTriangulations) {
					for (int o = 0; o < prevTriangulation.getOuterEdgeCount(); o++) {
						for (var piece : getMovedPieces()) {
							var triangulation = prevTriangulation.copy();

							triangulation.glue(piece.copy(), 0, o);

							if (!getAnswerTest().apply(triangulation)) {
								continue;
							}

							var key = createKey(triangulation);
							answerMap.putIfAbsent(key, new ArrayList<>());

							answerMap.get(key)
								.add(triangulation);
						}
					}
				}
			}

			answerMapList.add(answerMap);
		}

		answers = answerMapList.get(requiredPieceCount - 1)
			.values()
			.stream()
			.flatMap(List::stream)
			.toList();
	}

	private Map<List<Integer>, List<BigDecimal>> createKey(final Triangulation triangulation) {

		var lengthPattern = new EdgeLengthEncoder().apply(triangulation);
		var anglePattern = CircularAlgorithm.createCanonicalOnSymmetry(triangulation.getMergedAngles(),
			Integer::compare);

		return Map.of(anglePattern, lengthPattern);
	}

	/**
	 * proposed method.
	 * 
	 * @param requiredPieceCount
	 */
	private void enumerate(final int requiredPieceCount) {

		var answerMapList = new ArrayList<Map<List<Integer>, Triangulation>>();

		answerMapList.add(new HashMap<>());

		var answerMap = answerMapList.get(0);

		var encoder = new AnglePatternEncoder();

		// initialize
		for (var piece : getPieces()) {
			var pieceTriangulation = new Triangulation(new Polygon(piece));
			answerMap.put(encoder.apply(pieceTriangulation), pieceTriangulation);
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

						answerMap.put(encoder.apply(triangulation), triangulation);
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
}
