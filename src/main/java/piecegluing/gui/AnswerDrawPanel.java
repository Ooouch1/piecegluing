package piecegluing.gui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Objects;

import javax.swing.JPanel;

import piecegluing.data.coord.Face;
import piecegluing.image.FaceImageFactory;

public class AnswerDrawPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final List<Face> faces;

	private BufferedImage image;

	private final int id; // for debug

	public AnswerDrawPanel(final List<Face> faces, final int id) {
		this.id = id;
		this.faces = faces;

		if (faces.stream()
			.map(Face::getLabel)
			.anyMatch(Objects::isNull)) {
			throw new IllegalArgumentException("label of face is null!");
		}

	}

	@Override
	protected void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (image == null) {
			var factory = new FaceImageFactory(getWidth(), getHeight());
			image = factory.create(faces);
		}

		g.drawImage(image, 0, 0, this);
	}
}
