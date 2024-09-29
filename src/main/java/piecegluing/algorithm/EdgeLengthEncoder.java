package piecegluing.algorithm;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import piecegluing.circular.CircularAlgorithm;
import piecegluing.data.noncoord.Triangulation;

public class EdgeLengthEncoder implements Function<Triangulation, List<BigDecimal>> {

	@Override
	public List<BigDecimal> apply(final Triangulation triangulation) {

		var lengths = triangulation.getNormalizedOuterEdgeLengths(6);

		return CircularAlgorithm.createCanonicalOnSymmetry(lengths, (a, b) -> a.compareTo(b));
	}
}
