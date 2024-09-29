package piecegluing.algorithm;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

public class SameShapeRemover implements Function<List<Triangulation>, List<Triangulation>> {

	@Override
	public List<Triangulation> apply(final List<Triangulation> answers) {
		var patternMap = new HashMap<Map<Polygon, List<BigDecimal>>, Triangulation>();

		for (var answer : answers) {
			var lengthPattern = new EdgeLengthEncoder().apply(answer);
			var anglePattern = answer.createShapePolygon();

			var duplication = patternMap.putIfAbsent(Map.of(anglePattern, lengthPattern), answer);
			if (duplication == null) {
				// debug
//				System.out.println("add: " + anglePattern.toAngleString() + " lengths = " + lengthPattern);
			} else {
				// debug
//				System.out.println("duplication: " + duplication.createAnglePattern() + " = "
//						+ answer.createAnglePattern() + ", lengths = " + lengthPattern);
			}
		}

		return patternMap.values()
			.stream()
			.toList();
	}

}
