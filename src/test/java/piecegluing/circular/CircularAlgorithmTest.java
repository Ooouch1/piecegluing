package piecegluing.circular;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;

class CircularAlgorithmTest {

	@Test
	void testInteger() {

		var values = List.of(1, 2, 1, 4, 2);

		var least = CircularAlgorithm.createCanonicalOnSymmetry(values, Integer::compare);

		assertEquals(1, least.get(0));
		assertEquals(2, least.get(1));
		assertEquals(1, least.get(2));
		assertEquals(2, least.get(3));
		assertEquals(4, least.get(4));
	}

	@Test
	void testBigDecimal() {

		Function<Double, BigDecimal> factory = d -> BigDecimal.valueOf(d)
			.setScale(4, RoundingMode.HALF_UP);

		var values = List.of(factory.apply(1.00001), factory.apply(2d), factory.apply(1.00002), factory.apply(4d),
			factory.apply(2.00003));

		var least = CircularAlgorithm.createCanonicalOnSymmetry(values, (a, b) -> a.compareTo(b));

		assertBigDecimalEquals(BigDecimal.valueOf(1), least.get(0));
		assertBigDecimalEquals(BigDecimal.valueOf(2), least.get(1));
		assertBigDecimalEquals(BigDecimal.valueOf(1), least.get(2));
		assertBigDecimalEquals(BigDecimal.valueOf(2), least.get(3));
		assertBigDecimalEquals(BigDecimal.valueOf(4), least.get(4));
	}

	private void assertBigDecimalEquals(final BigDecimal expetcted, final BigDecimal actual) {
		assertTrue(expetcted.compareTo(actual) == 0);
	}

}
