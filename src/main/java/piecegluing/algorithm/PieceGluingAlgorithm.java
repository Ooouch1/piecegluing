package piecegluing.algorithm;

import java.util.List;
import java.util.function.Function;

import piecegluing.data.noncoord.Triangulation;

public class PieceGluingAlgorithm implements Function<Integer, List<Triangulation>> {

	private final Function<Integer, List<Triangulation>> enumerator;

	public PieceGluingAlgorithm(final Function<Integer, List<Triangulation>> enumerator) {
		this.enumerator = enumerator;
	}

	@Override
	public List<Triangulation> apply(final Integer shapeAngleCount) {

		var time0 = System.currentTimeMillis();

		var triangulations = enumerator.apply(shapeAngleCount);

		var time1 = System.currentTimeMillis();

		System.out.println((time1 - time0) + "[ms]");

		return triangulations;
	}
}
