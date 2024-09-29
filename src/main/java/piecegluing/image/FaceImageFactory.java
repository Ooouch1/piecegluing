package piecegluing.image;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import piecegluing.data.coord.Face;

public class FaceImageFactory {

	private final int width;
	private final int height;

	static class Label {
		String value;
		Point2D position;

		public Label(final String value, final Point2D position) {
			this.value = value;
			this.position = position;
		}

		public void transform(final AffineTransform t) {
			t.transform(position, position);
		}

		public String getValue() {
			return value;
		}

	}

	public FaceImageFactory(final int width, final int height) {
		this.width = width;
		this.height = height;
	}

	public BufferedImage create(final List<Face> faces) {
		if (faces.stream()
			.map(Face::getLabel)
			.anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("label of face is null!");
		}

		var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		var wholeShape = createWholeShapePath2D(faces);
		var labels = createLabels(faces);

		fitSize(wholeShape, labels);
		moveToCenter(wholeShape, labels);

		Graphics2D g2d = (Graphics2D) image.getGraphics();

		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, width, height);

		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g2d.draw(wholeShape);
		labels.forEach(
			label -> g2d.drawString(label.value, (int) label.position.getX() - 5, (int) label.position.getY() + 5));

		return image;
	}

	private void fitSize(final Path2D path, final List<Label> labels) {
		var bound = path.getBounds2D();

		var wholeScale = 0.9 * Math.min(width / bound.getWidth(), height / bound.getHeight());

		var affine = new AffineTransform();
		affine.scale(wholeScale, wholeScale);

		path.transform(affine);

		labels.forEach(label -> label.transform(affine));
	}

	private void moveToCenter(final Path2D path, final List<Label> labels) {
		var bound = path.getBounds2D();
		var affine = new AffineTransform();
		affine.translate(width / 2 - bound.getCenterX(), height / 2 - bound.getCenterY());

		path.transform(affine);

		labels.forEach(label -> label.transform(affine));
	}

	private Path2D createWholeShapePath2D(final List<Face> faces) {

		var wholeShape = new Path2D.Double();

		faces.forEach(face -> wholeShape.append(createPath2D(face), false));

		return wholeShape;
	}

	private List<Label> createLabels(final List<Face> faces) {
		return faces.stream()
			.map(face -> {
				var centroid = face.centroid();
				return new Label(face.getLabel(), new Point2D.Double(centroid.getX(), centroid.getY()));
			})
			.toList();
	}

	private Path2D createPath2D(final Face pathObj) {
		var path = new Path2D.Double();

		var vertex = pathObj.getVertex(0);

		path.moveTo(vertex.getX(), vertex.getY());

		for (int i = 1; i < pathObj.getVertexCount(); i++) {
			vertex = pathObj.getVertex(i);
			path.lineTo(vertex.getX(), vertex.getY());
		}

		path.closePath();

		return path;
	}
}
