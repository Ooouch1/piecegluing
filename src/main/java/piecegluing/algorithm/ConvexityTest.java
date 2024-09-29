package piecegluing.algorithm;

import java.util.function.Function;

import piecegluing.data.noncoord.Triangulation;

public class ConvexityTest implements Function<Triangulation, Boolean> {
	private final int piUnit;

	public ConvexityTest(final int piUnit) {
		this.piUnit = piUnit;
	}

	@Override
	public Boolean apply(final Triangulation triangulation) {
		var angles = triangulation.getMergedAngles();

		return angles.stream()
			.noneMatch(a -> a >= piUnit);
	}
}
