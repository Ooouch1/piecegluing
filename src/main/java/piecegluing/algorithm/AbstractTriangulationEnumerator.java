package piecegluing.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

public abstract class AbstractTriangulationEnumerator implements Function<Integer, List<Triangulation>> {
	private final List<Polygon> pieces;
	private final Set<Polygon> movedPieces;

	private final Function<Triangulation, Boolean> answerTest;

	public AbstractTriangulationEnumerator(final List<Polygon> canonicalPieces,
			final Function<Triangulation, Boolean> answerTest) {
		this.pieces = canonicalPieces;
		this.answerTest = answerTest;

		movedPieces = createMovedPieces();

		System.out.println("moved pieces:");
		System.out.println(movedPieces.stream()
			.map(p -> p.toAngleString())
			.toList());

	}

	private Set<Polygon> createMovedPieces() {
		var movedPieces = new HashSet<Polygon>();

		for (var piece : pieces) {

			var reversed = piece.reverse();
			for (int firstIndex = 0; firstIndex < piece.getAngleCount(); firstIndex++) {
				var rotated = piece.rotate(firstIndex);

				var reversedRotated = reversed.rotate(firstIndex);

				movedPieces.add(rotated);
				movedPieces.add(reversedRotated);
			}

		}

		return movedPieces;
	}

	protected List<Polygon> getPieces() {
		return pieces;
	}

	protected Set<Polygon> getMovedPieces() {
		return movedPieces;
	}

	protected Function<Triangulation, Boolean> getAnswerTest() {
		return answerTest;
	}
}
