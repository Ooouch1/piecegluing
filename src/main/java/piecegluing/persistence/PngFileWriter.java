package piecegluing.persistence;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PngFileWriter implements FileWriter<BufferedImage> {

	private String filePath;

	@Override
	public FileWriter<BufferedImage> setFilePath(final String filePath) {
		this.filePath = filePath;
		return this;
	}

	@Override
	public void write(final BufferedImage image) throws IOException {
		var file = new File(filePath).getCanonicalFile();

		System.out.println("write " + file);

		var rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
		rgbImage.getGraphics()
			.drawImage(image, 0, 0, null);
		if (!ImageIO.write(rgbImage, "png", file)) {
			throw new RuntimeException("ImageIO could not find writer");
		}
	}
}
