package piecegluing.main;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.SwingUtilities;

import piecegluing.algorithm.PieceGluingAlgorithm;
import piecegluing.algorithm.ReducedTriangulationEnumerator;
import piecegluing.algorithm.ReducedTriangulationEnumerator2;
import piecegluing.algorithm.ShapeGrouping;
import piecegluing.algorithm.TriangulationEnumerator;
import piecegluing.algorithm.TriangulationListComparer;
import piecegluing.algorithm.TriangulationToFaces;
import piecegluing.algorithm.UnitAnglePieceFactory;
import piecegluing.data.noncoord.Polygon;
import piecegluing.data.noncoord.Triangulation;
import piecegluing.gui.MainFrame;
import piecegluing.image.FaceImageFactory;
import piecegluing.persistence.PngFileWriter;

public class Main {
	// typical command: java -jar xxx.jar 8 4 unique [optional : path for images]
	public static void main(final String[] args) {
		var piUnit = 8;
		var angleCount = 4;
		var mode = "";
		String dirPath = null;

		if (args.length > 0) {
			piUnit = Integer.parseInt(args[0]);
		}

		if (args.length > 1) {
			angleCount = Integer.parseInt(args[1]);
		}

		if (args.length > 2) {
			mode = args[2];
		} else {
			mode = "";
		}

		if (args.length > 3) {
			dirPath = args[3];
		}

		try {

			var pieceFactory = new UnitAnglePieceFactory(piUnit);
			var pieces = pieceFactory.createAll();

			System.out.println("pieces:");

			for (var piece : pieces) {
				System.out.println(piece.toString());
			}
			System.out.println(pieces.size() + " pieces");

			if (mode.equals("diff")) {
				showDiffs(pieces, piUnit, angleCount);
				return;
			}

			var enumerator = switch (mode) {
			case "all" -> new TriangulationEnumerator(pieces, piUnit);
			case "unique" -> new ReducedTriangulationEnumerator(pieces, piUnit);
			case "unique2" -> new ReducedTriangulationEnumerator2(pieces, piUnit);
			default -> throw new IllegalArgumentException("wrong mode specification");
			};

			var answers = new PieceGluingAlgorithm(enumerator).apply(angleCount);

			System.out.println("------------------------");

			// debug
//			for (var i = 0; i < answers.size(); i++) {
//				System.out.println(i + ":" + answers.get(i));
//			}

			System.out.println(answers.size() + " answers.");

			if (answers.size() > 2000) {
				System.out.println("too many answers to output.");
				return;
			}

			if (dirPath == null) {
				SwingUtilities.invokeLater(() -> new MainFrame(new ShapeGrouping().apply(answers)).setVisible(true));
			} else {
				writePieceFiles(pieces, 200, 200, dirPath);
				writeTriangulationFiles(answers, 200, 200, dirPath);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void showDiffs(final List<Polygon> pieces, final int piUnit, final int angleCount) {
		var answers0 = new PieceGluingAlgorithm(new ReducedTriangulationEnumerator(pieces, piUnit)).apply(angleCount);
		var answers1 = new PieceGluingAlgorithm(new ReducedTriangulationEnumerator2(pieces, piUnit)).apply(angleCount);

		var diffs = new TriangulationListComparer().extractDifferences(answers0, answers1);

		SwingUtilities.invokeLater(() -> new MainFrame(diffs).setVisible(true));

	}

	private static void writePieceFiles(final List<Polygon> pieces, final int width, final int height,
			final String dirPath) throws IOException {
		writeFiles(pieces.stream()
			.map(Triangulation::new)
			.toList(), width, height, dirPath, "p");
	}

	private static void writeTriangulationFiles(final List<Triangulation> answers, final int width, final int height,
			final String dirPath) throws IOException {
		writeFiles(answers, width, height, dirPath, "");
	}

	private static void writeFiles(final List<Triangulation> answers, final int width, final int height,
			final String dirPath, final String prefix) throws IOException {
		var triangulationToFaces = new TriangulationToFaces();

		var digitCount = Integer.toString(answers.size())
			.length();

		var formatString = "%0" + digitCount + "d";

		var imageFactory = new FaceImageFactory(width, height);

		int fileNumber = 0;
		for (var answer : answers) {
			var faces = triangulationToFaces.apply(answer);

			var filePath = Paths.get(dirPath, prefix + String.format(formatString, fileNumber) + ".png");

			var writer = new PngFileWriter();

			writer.setFilePath(filePath.toString())
				.write(imageFactory.create(faces));

			fileNumber++;
		}
	}
}
