package piecegluing.algorithm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import piecegluing.data.noncoord.Polygon;

public class UnitAnglePieceFactory implements PieceFactory {
	private final int angleCount;
	private final int piUnit;

	private Set<Polygon> pieces;

	public UnitAnglePieceFactory(final int piUnit) {
		this.angleCount = 3;
		this.piUnit = piUnit;
	}

	@Override
	public List<Polygon> createAll() {

		pieces = new HashSet<Polygon>();

		var angleSum = piUnit * (angleCount - 2);

		setAngle(new Polygon(angleCount), 0, angleSum);

		var list = pieces.stream()
			.toList();

		Integer i = 0;
		for (var polygon : list) {
			polygon.setLabel((++i).toString());
		}

		return list;
	}

	private void setAngle(final Polygon piece, final int angleIndex, final int restSum) {
		// System.out.println(piece);

		if (angleIndex == angleCount) {
			if (restSum == 0) {
				pieces.add(piece.toCanonical());
			}
			return;
		}

		if (restSum <= 0) {
			return;
		}

		for (int angle = 1; angle <= restSum; angle++) {
			piece.setAngle(angleIndex, angle);

			setAngle(piece, angleIndex + 1, restSum - angle);
		}

	}

}
