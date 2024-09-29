package piecegluing.algorithm;

import java.util.List;

import piecegluing.data.noncoord.Polygon;

public interface PieceFactory {

	List<Polygon> createAll();

}