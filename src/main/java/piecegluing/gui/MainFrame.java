package piecegluing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import piecegluing.algorithm.TriangulationToFaces;
import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private final Map<Polygon, List<Triangulation>> answers;

	public MainFrame(final Map<Polygon, List<Triangulation>> answers) {
		this.answers = answers;

		setSize(800, 800);

		addComponents();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private void addComponents() {

		var layout = new WrapLayout();

		var panel = new JPanel();
		panel.setLayout(layout);

		var scroll = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scroll, BorderLayout.CENTER);

		int answerSize = 100;

		int id = 0;

		var triangulationToFaces = new TriangulationToFaces();
		for (var entry : answers.entrySet()) {
			var shape = entry.getKey();
			for (var answer : entry.getValue()) {

				var faces = triangulationToFaces.apply(answer);

				var drawPanel = new AnswerDrawPanel(faces, id);
				drawPanel.setPreferredSize(new Dimension(answerSize, answerSize));

				var answerPanel = new JPanel();
				answerPanel.setLayout(new BoxLayout(answerPanel, BoxLayout.Y_AXIS));
				answerPanel.add(createText(shape.toAngleString(), answerSize));
				answerPanel.add(createText(id + ":" + answer.createAnglePattern()
					.toString(), answerSize));
				answerPanel.add(drawPanel);

				panel.add(answerPanel);

				id++;
			}
		}
	}

	private JTextArea createText(final String text, final int size) {
		var textArea = new JTextArea(text);

		textArea.setMaximumSize(new Dimension(size, size));
		textArea.setLineWrap(true);
		textArea.setEditable(false);

		return textArea;
	}

}
