package piecegluing.algorithm;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import piecegluing.data.coord.Face;
import piecegluing.data.noncoord.Triangulation;

public class TriangulationToFaces implements Function<Triangulation, List<Face>> {

	@Override
	public List<Face> apply(final Triangulation triangulation) {

		var faces = new ArrayList<Face>(triangulation.trianglesStream()
			.map(t -> new Face(t, 1))
			.toList());

		if (faces.stream()
			.map(Face::getLabel)
			.anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("label of face is null!");
		}

		glueFaces(faces, triangulation);

		return faces;
	}

	private void glueFaces(final List<Face> faces, final Triangulation triangulation) {
		var done = new Boolean[faces.size()];

		Arrays.fill(done, false);
		done[0] = true;

		for (int i = 0; i < faces.size(); i++) {
			for (int j = i + 1; j < faces.size(); j++) {

				if (done[i] && done[j]) {
					// System.out.println("both done:" + id + "(" + i + "," + j + ")");
					continue;
				}
				if (!done[i] && !done[j]) {
					// System.out.println("both not done:" + id + "(" + i + "," + j + ")");
					continue;
				}

				if (!triangulation.areGlued(i, j)) {
					// System.out.println("not glued:" + id + " " + answer + " (" + i + "," + j +
					// ")");
					continue;
				}

				// System.out.println("move:" + id + "(" + i + "," + j + ")");

				var edgeIndices = triangulation.getGluedEdgeIndices(i, j);

				var triangleIndices = List.of(i, j);
				var is0Base = done[triangleIndices.get(0)];

				var index0 = triangleIndices.get(is0Base ? 0 : 1);
				var index1 = triangleIndices.get(is0Base ? 1 : 0);
				var edgeIndex0 = edgeIndices.get(is0Base ? 0 : 1);
				var edgeIndex1 = edgeIndices.get(is0Base ? 1 : 0);

				var face0 = faces.get(index0);
				var face1 = faces.get(index1);

				var edge0 = face0.getEdge(edgeIndex0);
				var edge1 = face1.getEdge(edgeIndex1);

				var scale = edge0.length() / edge1.length();

				var toOrigin = new AffineTransform();

				var end1 = edge1.getEnd();
				toOrigin.translate(-end1.getX(), -end1.getY());

				var gluing = new AffineTransform();

				var start0 = edge0.getStart();
				gluing.translate(start0.getX(), start0.getY());
				gluing.rotate(edge0.angle() - edge1.reverse()
					.angle());
				gluing.scale(scale, scale);

				faces.set(index1, face1.transform(toOrigin)
					.transform(gluing));

				done[i] = true;
				done[j] = true;

			}
		}

	}

}
