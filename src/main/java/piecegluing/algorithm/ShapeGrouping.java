package piecegluing.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

public class ShapeGrouping implements Function<List<Triangulation>, Map<Polygon, List<Triangulation>>> {

	@Override
	public Map<Polygon, List<Triangulation>> apply(final List<Triangulation> triangulations) {

		var map = new HashMap<Polygon, List<Triangulation>>();

		for (var triangulation : triangulations) {
			var shape = triangulation.createShapePolygon();
			map.putIfAbsent(shape, new ArrayList<>());

			map.get(shape)
				.add(triangulation);
		}

		return map;
	}
}
