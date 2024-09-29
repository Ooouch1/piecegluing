package piecegluing.circular;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class CircularAlgorithm {

	/**
	 * find the first index of the least circular string of given list in range of
	 * index = 0 ... lengh-1. this algorithm is from "LEXICOGRAPHlCALLY LEAST
	 * CIRCULAR SUBSTRINGS" by Kellogg S, BOOTH, pp240--241 Volume 10, number 4,5
	 * INFORMATION PROCESSING LETTERS 5 July 1980.
	 **/
	public static <V> int findFirstIndexOfLeastCircular(final List<V> values, final Comparator<V> comparator) {
		final int NIL_INDEX = -1;

		final int length = values.size();

		BiFunction<Integer, Integer, Integer> compareV = (i0, i1) -> comparator.compare(get(values, i0),
			get(values, i1));

		final int doubledLength = 2 * length;

		List<Integer> table = new ArrayList<>(IntStream.range(0, doubledLength)
			.map(i -> NIL_INDEX)
			.boxed()
			.toList());

		table.set(0, NIL_INDEX);

		int k = 0;
		for (int j = 1; j < doubledLength; j++) {
			// causes WRONG answer ALTHOUGH this "if" statement is in the paper...
			// A killer case is "0001011010001011"
			// see the python code @
			// https://en.wikipedia.org/wiki/Lexicographically_minimal_string_rotation
			// if (j - k >= length) {
			// if (debug) cout << "return " << k << endl;
			// return k;
			// }

			int i = table.get(j - k - 1);

			while (compareV.apply(j, k + i + 1) != 0 && i != NIL_INDEX) {
				if (compareV.apply(j, k + i + 1) < 0) {
					k = j - i - 1;
				}
				i = table.get(i);
			}
			if (i == NIL_INDEX && compareV.apply(j, k + i + 1) != 0) {
				if (compareV.apply(j, k + i + 1) < 0) {
					k = j;
				}
				table.set(j - k, NIL_INDEX);
			} else {
				table.set(j - k, i + 1);
			}

		}

		return k;
	}

	public static <T> T get(final List<T> values, final int index) {
		return values.get((index + values.size()) % values.size());
	}

	public static <V> List<V> createCanonicalOnSymmetry(final List<V> values, final Comparator<V> comparator) {

		var length = values.size();

		var firstIndex = findFirstIndexOfLeastCircular(values, comparator);

		var reversed = new ArrayList<>(values);
		Collections.reverse(reversed);
		var rev_firstIndex = findFirstIndexOfLeastCircular(reversed, comparator);

		// select the least one from {least_reversed, least}

		if (lessThan(reversed, values, length, rev_firstIndex, firstIndex, comparator)) {
			return createShiftedValues(reversed, rev_firstIndex);
		}

		return createShiftedValues(values, firstIndex);
	}

	/**
	 * new i-th value will be the given (firstIndex+i)-th value.
	 * 
	 * @param base
	 * @param firstIndex
	 * @param length
	 * @return
	 */
	public static <V> List<V> createShiftedValues(final List<V> base, final int firstIndex) {
		int length = base.size();
		List<V> shifted = new ArrayList<V>(IntStream.range(0, length)
			.mapToObj(i -> (V) null)
			.toList());
		for (int i = 0; i < length; i++) {
			shifted.set(i, base.get((i + firstIndex) % length));
		}
		return shifted;
	}

	private static <V> boolean lessThan(final List<V> left, final List<V> right, final int length,
			final int leftStartIndex, final int rightStartIndex, final Comparator<V> comparator) {

		BiFunction<Integer, Integer, Integer> modIndex = (firstIndex, offset) -> (firstIndex + offset) % length;
		int i;
		for (i = 0; i < length; i++) {
			if (comparator.compare(right.get(modIndex.apply(rightStartIndex, i)),
				left.get(modIndex.apply(leftStartIndex, i))) == 0)
				continue;
			else if (comparator.compare(right.get(modIndex.apply(rightStartIndex, i)),
				left.get(modIndex.apply(leftStartIndex, i))) < 0)
				return false;
			else
				break;
		}

		return i < length;
	}
}
