package piecegluing.algorithm;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

public class TriangulationListComparer {

	public Map<Polygon, List<Triangulation>> extractDifferences(final List<Triangulation> triangulations0,
			final List<Triangulation> triangulations1) {

		var shapeGrouping = new ShapeGrouping();

		var grouped0 = shapeGrouping.apply(triangulations0);

		var grouped1 = shapeGrouping.apply(triangulations1);

		var diffs = new HashMap<Polygon, List<Triangulation>>();

		var sharedKeys = new HashSet<>(grouped0.keySet());
		sharedKeys.retainAll(grouped1.keySet());

		var differentKeys0 = new HashSet<>(grouped0.keySet());
		var differentKeys1 = new HashSet<>(grouped1.keySet());
		differentKeys0.removeAll(sharedKeys);
		differentKeys1.removeAll(sharedKeys);

		System.out.println("#differentKeys0 = " + differentKeys0.size());
		for (var key0 : differentKeys0) {
			diffs.put(key0, grouped0.get(key0));
		}

		System.out.println("#differentKeys1 = " + differentKeys1.size());
		for (var key1 : differentKeys1) {
			diffs.put(key1, grouped1.get(key1));
		}

		for (var key : sharedKeys) {
			var valueMap0 = new HashMap<List<BigDecimal>, Triangulation>();
			var valueMap1 = new HashMap<List<BigDecimal>, Triangulation>();

			grouped0.get(key)
				.forEach(v -> {
					var lengthPattern = new EdgeLengthEncoder().apply(v);
					valueMap0.put(lengthPattern, v);
				});

			grouped1.get(key)
				.forEach(v -> {
					var lengthPattern = new EdgeLengthEncoder().apply(v);
					valueMap1.put(lengthPattern, v);
				});

			var differentLengths0 = new HashSet<>(valueMap0.keySet());
			differentLengths0.removeAll(valueMap1.keySet());
			var differentLengths1 = new HashSet<>(valueMap1.keySet());
			differentLengths1.removeAll(valueMap0.keySet());

			var differentLengths = new HashSet<>(differentLengths0);
			differentLengths.addAll(differentLengths1);

			var differentValues = new ArrayList<Triangulation>();

			for (var lengthPattern : differentLengths) {
				var v0 = valueMap0.get(lengthPattern);
				if (v0 != null) {
					differentValues.add(v0);
				}
				var v1 = valueMap1.get(lengthPattern);
				if (v1 != null) {
					differentValues.add(v1);
				}
			}

			diffs.put(key, differentValues);

		}

		return diffs;
	}
}
