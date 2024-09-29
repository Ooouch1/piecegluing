package piecegluing.persistence;

import java.io.IOException;

public interface FileWriter<Data> {
	public FileWriter<Data> setFilePath(String filePath);

	public void write(Data data) throws IOException;
}
