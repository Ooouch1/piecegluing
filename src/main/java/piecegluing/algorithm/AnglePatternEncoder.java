package piecegluing.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import piecegluing.circular.CircularAlgorithm;
import piecegluing.data.noncoord.Triangulation;

public class AnglePatternEncoder implements Function<Triangulation, List<Integer>> {

	@Override
	public List<Integer> apply(final Triangulation t) {
		var pattern = t.createAnglePattern();
		var encoded = new ArrayList<Integer>();

		for (var angles : pattern) {
			encoded.addAll(angles);
			encoded.add(0);
		}

		return CircularAlgorithm.createCanonicalOnSymmetry(encoded, Integer::compare);
	}
}
