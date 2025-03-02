package com.skylarkarms.stringutils;

import com.skylarkarms.compactcollections.CompactArrayBuilder;
import com.skylarkarms.compactcollections.CompactHashTable;
import com.skylarkarms.lambdas.Funs;
import com.skylarkarms.lambdas.ToStringFunction;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtils {
    public static String divisor =
            """

                     *** >>> *** <<< *** || *** >>> *** <<< *** || *** >>> *** <<< ***\s
                     *** >>> *** <<< *** || *** >>> *** <<< *** || *** >>> *** <<< ***\s
                    """;
    public static String join(String... strings) {
        return join(divisor, strings);
    }

    static int bufferCalc(int strl) {
        return strl * 6;
    }
    public static String join(String delimiter, String... strings) {
        int sl;
        if (strings == null || (sl = strings.length) == 0) return EMPTY.ref;
        int last = sl - 1;
        StringBuilder builder = new StringBuilder(bufferCalc(sl) + (last * delimiter.length()));
        builder.append(strings[0]);
        for (int i = 1; i < sl; i++) {
            builder.append(delimiter).append(strings[i]);
        }
        return builder.toString();
    }

    public static String join(String delimiter, Collection<String> strings) {
        if (strings == null) {
            return EMPTY.ref;
        }
        StringJoiner joiner = new StringJoiner(delimiter);
        for (String e:strings) joiner.add(e);

        return joiner.toString();
    }

    public static String[] split(String original, int index) {
        return new String[]{
                original.substring(0, index),
                original.substring(index)
        };
    }

    public static String[] split(String original, int ... points) {
        int length = points.length + 1;
        int lastPoint = 0;
        int lastPos = points.length;
        String[] result = new String[length];
        int lastSplittingPoint = points[lastPos - 1];
        if (!(lastSplittingPoint > original.length())) {
            for (int i = 0; i < length; i++) {
                if (i != lastPos) {
                    int currentPoint = points[i];
                    result[i] = original.substring(lastPoint, currentPoint);
                    lastPoint = currentPoint;
                } else {
                    result[i] = original.substring(lastPoint);
                }
            }
        } else {
            throw new IndexOutOfBoundsException("Last point: [" + lastSplittingPoint + "] is greater than the length of the original String: " + original +" [" + original.length() + "]. " );
        }
        return result;
    }

    public static String generateRandomHexToken(SecureRandom secureRandom, int byteLength) {
        byte[] token = new byte[byteLength];
        secureRandom.nextBytes(token);
        return new BigInteger(1, token).toString(16); // Hexadecimal encoding
    }

    public static String intToStringTransformer(int _int, int decimalPlaces, String prefix, String sufix) {
        String prevString = prefix == null ? EMPTY.ref : prefix;
        String precString = sufix == null ? EMPTY.ref : sufix;
        String wholeN = Integer.toString(_int);

        int stringLength = wholeN.length();

        int leftLength = stringLength - decimalPlaces;

        String leftString, rightString, commaString;

        leftString = leftLength < 1 ? "0" : wholeN.substring(0, leftLength);

        rightString = leftLength < 0 ? "0" + wholeN.charAt(0) : wholeN.substring(leftLength,stringLength);

        commaString = decimalPlaces == 0 ? EMPTY.ref : ",";

        return prevString + " " + leftString + commaString + rightString + " " + precString;
    }

    public static String intToStringTransformer(int _int, int decimalPlaces) {
        String wholeN = Integer.toString(_int);
        int stringLength = wholeN.length();
        int leftLength = stringLength - decimalPlaces;
        String leftString, rightString, commaString;
        leftString = leftLength < 1 ? "0" : wholeN.substring(0, leftLength);
        rightString = leftLength < 0 ? "0" + wholeN.charAt(0) : wholeN.substring(leftLength,stringLength);
        commaString = decimalPlaces == 0 ? EMPTY.ref : ",";
        return leftString + commaString + rightString;
    }

    public static String[] toStringArray(
            String fromString
    ) {
        if (fromString == null || fromString.length() < 2) {
            throw new IllegalStateException("Incompatible string: " + fromString);
        }
        return fromString.substring(1, fromString.length() - 1).split(", ");
    }

    /**Includes comma (",") parsing*/
    public static double parseDouble(String aDouble) {
        String changed = aDouble;
        if (aDouble.contains(",")) {
            changed = aDouble.replace(',','.');
        }
        return Double.parseDouble(changed);
    }

    public static String nonEmptyJoin(String... strings) {
        int sl;
        if (strings == null || (sl = strings.length) == 0) return EMPTY.ref;
        StringBuilder sb = new StringBuilder(bufferCalc(sl));
        String s = strings[0];
        if (!s.isEmpty()) {
            sb.append(s);
        }
        for (int i = 1; i < sl; i++) {
            s = strings[i];
            if (!s.isEmpty()) {
                sb.append(", ").append(s);
            }
        }
        return sb.toString();
    }

    /**
     * We can argue how stupid this is.
     * */
    public static String pluralize(int value, String word, String suffix) {
        if (value == 0) {
            return EMPTY.ref;
        }
        return Integer.toString(value).concat(" ".concat(word.concat(value > 1 ? suffix : EMPTY.ref)));
    }

    /**@param digitPlace = If negative it will be decimals, if positive it will round up to the nearest 10*/
    public static String parseDouble(double aDouble, int digitPlace) {
        if (aDouble == Double.POSITIVE_INFINITY || aDouble == Double.NEGATIVE_INFINITY) {
            return Character.toString('\u221E');
        }
        double toNearest = Math.pow(10, digitPlace);
        String res = Double.toString(Math.round(aDouble / toNearest) * toNearest);
        if (digitPlace < 0) {
            int indexOfDot = res.indexOf(".");
            int cropFrom = Math.min(indexOfDot + Math.abs(digitPlace) + 1, res.length());
            return res.substring(0, cropFrom);
        }
        return res;
    }

    private static final String space = "\\s+";
    public static String[] intoWords(String phrase) {
        return phrase.split(space);
    }
    public static String toPhrase(String... words) {
        return join(space, words);
    }
    public static String toPhrase(int start, int end, String ...words) {
        int wl;
        if (words == null || (wl = words.length) == 0) return EMPTY.ref;
        StringBuilder sb = new StringBuilder(bufferCalc(end - start));
        int last = end - 1;
        assert last < wl : "End index [" + end + "] greater than words length [" + wl + "]";
        sb.append(words[start]);
        for (int i = start + 1; i < last; i++) {
            sb.append(space).append(words[i]);
        }
        return sb.toString();
    }

    /**
     * from A == 0
     * */
    public static char from(int alphabetIndex, boolean upperCase) {
        assert alphabetIndex >= 0 && alphabetIndex <= 25 : "Alphabet index must be between 0 and 25, inclusive.";
        char c = (char) ('a' + alphabetIndex);
        if (upperCase) {
            c -= 32;
        }
        return c;
    }

    public static String hexavigesimal(int number, boolean upperCase) {
        assert number > 0 : "Only positive numbers, not [" + number + "]";
        StringBuilder result = new StringBuilder(number);
        char base = upperCase ? 'A' : 'a';
        while (number > 0) {
            number--;
            result.insert(0, (char)(base + (number % 26)));
            number /= 26;
        }
        return result.toString();
    }

    public static String toString(int[] digits) {
        int dl;
        if (digits == null || (dl = digits.length) == 0) return EMPTY.ref;
        StringBuilder builder = new StringBuilder(dl);
        for (int i = 0; i < dl; i++) {
            builder.append(digits[i]);
        }
        return builder.toString();
    }


    /////////////////////////////////////>>>>>>
    /////////////////////////////////////>>>>>>
    /////////////////////////////////////>>>>>>
    /////////////////////////////////////>>>>>>


    private static final String[] template = new String[0];

    public static final class Arrays {
        private Arrays() {}

        /**
         * Maps an array to a {@link String} array.
         * @param source the source array.
         * @param map the function to be applied to each element.
         * */
        public static <E> String[] toStringArray(
                E[] source, ToStringFunction<E> map
        ) {
            int length;
            if (source == null || (length = source.length) == 0) return template.clone();
            final String[] res = new String[length];
            for (int i = 0; i < length; i++) {
                res[i] = map.apply(source[i]);
            }
            return res;
        }

        /**
         * Maps an array to a {@link String[]} by applying {@link Object#toString()} on each element.
         * @param source the source array.
         * */
        public static <E> String[] toStringArray(
                E[] source
        ) throws NullPointerException {
            int length;
            if (source == null || (length = source.length) == 0) return template.clone();
            final String[] res = new String[length];
            for (int i = 0; i < length; i++) {
                res[i] = source[i].toString();
            }
            return res;
        }

        public static final String
                nullS = "null";

        static final String
                left = "["
                , left_2 = "=["
                , right = "]"
                , comma = ", "
                , empty = left.concat(right)
                ;

        /**
         * Formats a source array to a {@link String} following the convention specified at {@link java.util.Arrays#toString(Object[])}
         * @param source the array to be mapped.
         * @param from = the index to begin the formatting (inclusive)
         * @param to = the index to end (non-inclusive).
         * @apiNote Information about source's original length and previous indexes will be lost.
         * */
        public static<E> String toString(E[] source, int from, int to) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            int finalTo = Math.min(to, length);
            int finalFrom = Math.min(Math.max(from, 0), length - 1);
            StringBuilder sb = new StringBuilder(left);
            E next;
            sb.append(
                    (next = source[finalFrom++]) == null ? nullS : next.toString()
            );
            for (; finalFrom < finalTo; finalFrom++) {
                sb.append(comma)
                        .append((next = source[finalFrom]) == null ? nullS : next.toString());
            }
            return sb.append(right).toString();
        }

        /**
         * Formats a source array to a {@link String} by applying the specified function on each element and following the convention defined at {@link java.util.Arrays#toString(Object[])}
         * @param source the source array to be mapped.
         * @param map the {@link ToStringFunction} to be applied to each element {@link E}
         * */
        static<E> String toString(E[] source, ToStringFunction<E> map) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            StringBuilder sb = new StringBuilder(left);
            int i = 0;
            sb.append(
                    map.apply(source[i++])
            );
            for (; i < length; i++) {
                sb.append(comma)
                        .append(
                                map.apply(source[i])
                        );
            }
            return sb.append(right).toString();
        }
        /**
         * Parameter class that will define the sub-view of the E[] array to be transformed into a String.
         * <ul>
         *     <li>
         *         {@link #all}
         *     </li>
         *     <li>
         *         {@link #first}
         *     </li>
         *     <li>
         *         {@link #last}
         *     </li>
         *     <li>
         *         {@link #window(int, Mode)}
         *     </li>
         *     <li>
         *         {@link #single(int)}
         *     </li>
         *     <li>
         *         {@link #window(int, int)}
         *     </li>
         * </ul>
         * */
        public static final class ViewRange {
            final RangeResolve resolve;

            @FunctionalInterface
            interface RangeResolve {
                int start = 0, end = 1;
                int[] template = new int[]{0, 0};
                int[] apply(int length);
            }
            /**
             * Defines the 'side' by which the offset begins counting.
             * Defines the mode by which the value will be interpreted by the {@link ViewRange#window(int, Mode)} method.
             * */
            public enum Mode {
                /**
                 * Defines order first-to-{@code int value}
                 * */
                toFirst,
                /**
                 * Defines order last-to-(last - {@code int value})
                 * */
                toLast,
                /**
                 * Defines that the view window will encompass FROM the {@code value} defined (inclusive), TO LAST
                 * */
                from,
                /**
                 * Defines that the view window will encompass FROM FIRST TO the {@code value} defined (inclusive).
                 * */
                until
            }

            /**
             * The view will comprise the totality of the length of the source array.
             * */
            public static ViewRange all = new ViewRange(
                    l -> {
                        int[] copy = RangeResolve.template.clone();
                        copy[RangeResolve.start] = 0;
                        copy[RangeResolve.end] = l;
                        return copy;
                    }
            );
            /**
             * The view will return just the first element of the source array.
             * */
            public static ViewRange first = new ViewRange(
                    l -> {
                        int[] copy = RangeResolve.template.clone();
                        copy[RangeResolve.start] = 0;
                        copy[RangeResolve.end] = 1;
                        return copy;
                    }
            );
            /**
             * The view will return the last element of the source array.
             * */
            public static ViewRange last = new ViewRange(
                    l -> {
                        int[] copy = RangeResolve.template.clone();
                        copy[RangeResolve.start] = l - 1;
                        copy[RangeResolve.end] = l;
                        return copy;
                    }
            );

            private ViewRange(RangeResolve resolve) {
                this.resolve = resolve;
            }

            /**
             * Defines the {@link Mode} of the stack to print.
             * If the {@code value} surpasses the max length of the array, the length will be used instead,
             * returning the stack at index 0, or the last index depending on the direction chosen.
             * */
            public static ViewRange window(int value, Mode direction) {
                return switch (direction) {
                    case toLast -> new ViewRange(
                            l -> {
                                int[] copy = RangeResolve.template.clone();
                                copy[RangeResolve.start] = (l - 1) - value;
                                copy[RangeResolve.end] = l;
                                return copy;
                            }
                    );
                    case toFirst -> new ViewRange(
                            l -> {
                                int[] copy = RangeResolve.template.clone();
                                copy[RangeResolve.start] = 0;
                                copy[RangeResolve.end] = value + 1;
                                return copy;
                            }
                    );
                    case from -> new ViewRange(
                            length -> {
                                int[] copy = RangeResolve.template.clone();
                                copy[RangeResolve.start] = value;
                                copy[RangeResolve.end] = length;
                                return copy;
                            }
                    );
                    case until -> new ViewRange(
                            length -> {
                                int[] copy = RangeResolve.template.clone();
                                copy[RangeResolve.start] = 0;
                                copy[RangeResolve.end] = value;
                                return copy;
                            }
                    );
                };
            }

            /**
             * Will create a sub-view of the array that will span between the index
             * defined at 'start' and the one defined at 'end', both inclusive.
             * 'start' will truncate at 0, and 'end' at length - 1;
             * */
            public static ViewRange window(int start, int end) {
                return new ViewRange(
                        l -> {
                            int[] copy = RangeResolve.template.clone();
                            copy[RangeResolve.start] = start;
                            copy[RangeResolve.end] = end + 1;
                            return copy;
                        }
                );
            }

            /**
             * Will create view comprised of a single index cell, or:
             * index 0, if the index is too small.
             * last index if the value surpassed the length of the array.
             * */
            public static ViewRange single(int index) {
                return new ViewRange(
                        l -> {
                            int[] copy = RangeResolve.template.clone();
                            copy[RangeResolve.start] = index;
                            copy[RangeResolve.end] = index + 1;
                            return copy;
                        }
                );
            }

            interface StringResolve<E> {
                String toS(E[] es, int s, int e);
                StringResolve<?> defaultToString = (StringResolve<Object>) Arrays::toString;
                StringResolve<String> identity = Arrays::toString;
                @SuppressWarnings("unchecked")
                static<E> StringResolve<E> getDefault() {
                    return (StringResolve<E>) defaultToString;
                }
            }

            <E> String apply(E[] es, StringResolve<E> r) {
                int[] res = resolve.apply(es.length);
                return r.toS(es, res[RangeResolve.start], res[RangeResolve.end]);
            }
        }

        /**
         * Default implementation of {@link #toString(Object[], int, int)} where both parameters '{@code from}' and '{@code to}' have been replaced by {@link ViewRange}
         * @see ViewRange
         * */
        public static<E> String toString(E[] es, ViewRange range) {
            return range.apply(es, ViewRange.StringResolve.getDefault());
        }

        public static String toString(String[] strings, int from, int to) {
            return toString(
                    strings, Funs.Unaries.OfString.identity(),
                    from, to
            );
        }

        /**
         * Default implementation of {@link #toString(Object[], String, int, int)}
         * <p> where:
         * <ul>
         *     <li>
         *         {@code from} = 0
         *     </li>
         *     <li>
         *         {@code to} = length
         *     </li>
         * </ul>
         *
         * */
        public static String toString(String[] strings, String join) {
            return toString(
                    strings, join, 0, strings.length
            );
        }

        /**
         * Formats a source array to a {@link String} by applying a {@link ToStringFunction} on each element and
         * following the convention specified at {@link java.util.Arrays#toString(Object[])}
         * @param source the array to be mapped.
         * @param map the mapping function
         * @param from the index to begin the formatting (inclusive)
         * @param to the index to end (non-inclusive).
         * @apiNote Information about source's original length and previous indexes will be lost.
         * */
        public static<E> String toString(
                E[] source
                , ToStringFunction<E> map
                , int from
                , int to
        ) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            StringBuilder sb = new StringBuilder(left);
            int finalTo = Math.min(to, length);
            int finalFrom = Math.min(Math.max(from, 0), length - 1);
            sb.append(
                    map.apply(source[finalFrom++])
            );
            for (; finalFrom < finalTo; finalFrom++) {
                sb.append(comma)
                        .append(
                                map.apply(source[finalFrom])
                        );
            }
            return sb.append(right).toString();
        }
        /**
         * @param to = non-inclusive
         * @param prefix will be appended BEFORE the first {@link #left}
         * */
        public static<E> String toString(String prefix, E[] es, String join, ToStringFunction<E> map, int from, int to) {
            return toString(prefix.concat(left), es, join, right, map, from, to);
        }

        /**
         * Formats a source array to a {@link String} by applying a {@link ToStringFunction} on each element and
         * following the convention:
         * <p> 'prefix' + map.apply(firstElement) + 'join' + map.apply(secondElement) + 'join' + ... + map.apply(finalElement) + 'suffix';
         * @param source the array to be mapped.
         * @param map the mapping function
         * @param from the index to begin the formatting (inclusive)
         * @param to the index to end (non-inclusive).
         * @apiNote Information about source's original length and previous indexes will be lost.
         * */
        public static<E> String toString(String prefix, E[] source, String join, String suffix, ToStringFunction<E> map, int from, int to) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            int finalTo = Math.min(to, length);
            int finalFrom = Math.min(Math.max(from, 0), length - 1);
            StringBuilder sb = new StringBuilder(prefix);
            sb.append(map.apply(source[finalFrom++]));
            for (; finalFrom < finalTo; finalFrom++) {
                sb.append(join)
                        .append(map.apply(source[finalFrom]));
            }
            return sb.append(suffix).toString();
        }

        /**
         * Formats a source array to a {@link String} by applying an inlined {@link String#valueOf(Object)}
         * following the convention:
         * <p> 'prefix' + map.apply(firstElement) + 'join' + map.apply(secondElement) + 'join' + ... + map.apply(finalElement) + 'suffix';
         * @param source the array to be mapped.
         * @param from the index to begin the formatting (inclusive)
         * @param to the index to end (non-inclusive).
         * @apiNote Information about source's original length and previous indexes will be lost.
         * */
        public static<E> String toString(String prefix, E[] source, String join, String suffix, int from, int to) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            int finalTo = Math.min(to, length);
            int finalFrom = Math.min(Math.max(from, 0), length - 1);
            StringBuilder sb = new StringBuilder(prefix);
            E next;
            sb.append(
                    (next = source[finalFrom++]) == null ? nullS : next.toString()
            );
            for (; finalFrom < finalTo; finalFrom++) {
                sb.append(join)
                        .append((next = source[finalFrom]) == null ? nullS : next.toString());
            }
            return sb.append(suffix).toString();
        }


        /**
         * Formats a source array to a {@link String} by applying an inlined {@link String#valueOf(Object)}
         * following the convention:
         * <p> 'prefix' + map.apply(firstElement) + 'join' + map.apply(secondElement) + 'join' + ... + map.apply(finalElement) + 'suffix';
         * @param source the array to be mapped.
         * @param from the index to begin the formatting (inclusive)
         * @param to the index to end (non-inclusive).
         * @apiNote Information about source's original length and previous indexes will be lost.
         * */
        public static<E> String toString(E[] source, String join, int from, int to) {
            int length;
            if (source == null || (length = source.length) == 0) return empty;
            int finalTo = Math.min(to, length);
            int finalFrom = Math.min(Math.max(from, 0), length - 1);
            int tot = finalTo - finalFrom;
            StringBuilder sb = new StringBuilder(bufferCalc(tot) + (join.length() * tot));
            E next;
            sb.append(
                    (next = source[finalFrom++]) == null ? nullS : next.toString()
            );
            for (; finalFrom < finalTo; finalFrom++) {
                sb.append(join)
                        .append((next = source[finalFrom]) == null ? nullS : next.toString());
            }
            return sb.toString();
        }

        /**
         * Default implementation of {@link #toString(String, Object[], String, String, int, int)}
         * <p> where:
         * <ul>
         *     <li>
         *         {@code prefix} = {@link #left_2}
         *     </li>
         *     <li>
         *         {@code suffix} = {@link #right}
         *     </li>
         * </ul>
         * */
        static<E> String toString(String prefix, E[] source, String join, int from, int to) {
            return toString(
                    prefix.concat(left_2), source, join, right, from, to
            );
        }

        /**
         * Default implementation of {@link #toString(Object[], String, int, int)} that accepts a {@link ViewRange} as parameter,
         * replacing the parameters '{@code from}' and '{@code to}'.
         * @see ViewRange
         * */
        static<E> String toString(E[] source, String join, ViewRange range) {
            return range.apply(source,
                    (es1, s, e) -> toString(es1, join, s, e)
            );
        }

        /**
         * Will default to {@link #comma} as default join separator.
         * */
        static<E> String toString(String prefix, E[] es, int from, int to) {
            return toString(prefix.concat(left_2), es, comma, right, from, to);
        }
    }

    public static final class Collections { private Collections() {}

        public static <X> String[] toStringArray(
                Collection<X> source,
                ToStringFunction<X> map
        ) {
            int length;
            if (source == null || (length = source.size()) == 0) return template.clone();
            String[] res = new String[length];
            Iterator<X> xIterator = source.iterator();
            for (int i = 0; i < length; i++) {
                res[i] = map.apply(xIterator.next());
            }
            return res;
        }

        public static <X> String[] toStringArray(
                Collection<X> source
        ) throws NullPointerException {
            int length;
            if (source == null || (length = source.size()) == 0) return template.clone();
            String[] res = new String[length];
            Iterator<X> xIterator = source.iterator();
            for (int i = 0; i < length; i++) {
                res[i] = xIterator.next().toString();
            }
            return res;
        }
    }

    public static final class Maps {

        public static <K,V> Map<K,V> toMap(
                String input,
                Supplier<Map<K, V>> collector,
                Funs.From.String<K> keyFunction,
                Funs.From.String<V> valueFunction
        ) {
            Map<K, V> map = collector.get();
            String regex = ",\\s*(?![^\\[]*\\])";
            input = input.substring(1, input.length() - 1);
            String clone = new String(input);
            String[] pairs = clone.split(regex);
            try {
                for (String pair : pairs) {
                    // Split the pair by equal sign to get the key and value
                    String[] keyValue = pair.split("=");
                    // Apply the keyFunction and valueFunction to convert the strings to desired types
                    K key = keyFunction.apply(keyValue[0]);
                    V value = valueFunction.apply(keyValue[1]);
                    // Put the key and value into the map
                    map.put(key, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(
                        ">>> input = " + input
                                + "\n >>> clone = " + clone
                                + "\n >>> pairs = " + inspect(pairs)
                        , e);
            }
            return map;
        }

        public static <K, V> String toString(
                Map<K, V> map,
                ToStringFunction<K> mapKey,
                ToStringFunction<V> mapVal
        ) {
            // Create a StringBuilder to store the result
            StringBuilder sb = new StringBuilder(map.size() * 12);
            sb.append("{");
            for (Map.Entry<K, V> entry : map.entrySet()) {
                sb
                        .append(mapKey.apply(entry.getKey()))
                        .append("=")
                        .append(mapVal.apply(entry.getValue()))
                        .append(", \n");
            }
            // Remove the last comma and space if the map is not empty
            if (!map.isEmpty()) {
                sb.delete(sb.length() - 2, sb.length());
            }
            return sb.append("}").toString();
        }

        public static <K, V> String toStringDetailed(Map<K, V> map) {
            return toStringDetailed(map, ToStringFunction.valueOf(), ToStringFunction.valueOf());
        }

        public static <K, V> String toStringDetailed(
                Map<K, V> map,
                ToStringFunction<K> mapKey,
                ToStringFunction<V> mapVal
        ) {
            if (map == null) return "{NULL MAP}";
            if (map.size() == 0) return "{EMPTY MAP}";
            // Create a StringBuilder to store the result
            final StringBuilder sb;
            // Append the opening brace
            (sb = new StringBuilder())
                    .append("Map of size = ".concat(Integer.toString(map.size())))
                    .append(".\n {\n");
            // Iterate over the map entries
            for (Map.Entry<K, V> entry : map.entrySet()) {
                sb
                        .append(mapKey.apply(entry.getKey()))
                        .append("=")
                        .append(mapVal.apply(entry.getValue()))
                        .append(", \n");
            }
            sb.delete(sb.length() - 2, sb.length());
            // Append the closing brace
            return sb.append("\n }").toString();
        }
        public static <K, V> String toString(
                Map<K, V> map
        ) {
            return toString(map,
                    ToStringFunction.valueOf()
                    , ToStringFunction.valueOf()
            );
        }

        public static <K, V> String toStringVal(
                Map<K, V> map,
                ToStringFunction<V> fun
        ) {
            return toString(map,
                    ToStringFunction.valueOf()
                    , fun
            );
        }
        public static <K, V> String toStringValDetailed(
                Map<K, V> map,
                ToStringFunction<V> fun
        ) {
            return toStringDetailed(map,
                    ToStringFunction.valueOf()
                    , fun
            );
        }
    }

    public static String inspect(String[][] table) {
        int length;
        if (table == null || (length = table.length) == 0) {
            return table == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = new StringBuilder(table.length * 6 * 6);
        builder.append("\nTable:\n");

        if (table.length == 1) {
            String[] row = table[0];
            builder.append("[Row ").append("0").append("] ");
            for (String s : row) {
                String cell = s != null ? s : "null";
                builder.append(cell).append(" ");
            }
            return builder.toString();
        }

        // Calculate the maximum width of each column
        int[] columnWidths = null;
        try {
            for (String[] row : table) {
                if (columnWidths == null) {
                    columnWidths = new int[row.length];
                }
                for (int col = 0; col < row.length; col++) {
                    if (row[col] != null) {
                        columnWidths[col] =
                                Math.max(
                                        columnWidths[col],
                                        row[col].length()
                                );
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException(
                    "Jagged Arrays not supported... or maybe... if the 1st row is the lengthiest..."
            );
        }

        for (int i = 0; i < length; i++) {
            String[] row = table[i];
            builder.append("[Row ").append(i).append("] ");
            for (int col = 0; col < row.length; col++) {
                String cell = row[col] != null ? row[col] : "null";
                builder.append(String.format("%-" + columnWidths[col] + "s ", cell));
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    /**
     * Returns a {@link String} with specific details about the array's:
     * <ul>
     *     <li>
     *         length
     *     </li>
     *     <li>
     *         {@link Class}
     *     </li>
     *     <li>
     *         Element index
     *     </li>
     * </ul>
     * */
    public static<E> String inspect(E[] array) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return array == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(array.getClass(), length);
        E next;
        builder.append("\n");
        for (int i = 0; i < length; i++) {
            builder.append("   o->> [").append(i).append("] >> \n")
                    .append(
                            (next = array[i]) == null ? "      [null]" : next.toString().indent(6)
                    );
        }
        return builder.append("    }").toString();
    }
    /**
     * Similar behavior to {@link #inspect(Object[])} that applyies a {@link ToStringFunction} to each of the elements of the array.
     * @param array the array to be inspected.
     * @param map the function to be applied to each element.
     * */
    public static<E> String inspect(E[] array, ToStringFunction<E> map) {
        int length;
        if (array == null || (length = array.length) == 0) {
            return array == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(array.getClass(), length);
        for (int i = 0; i < length; i++) {
            builder.append("\n    o->> [").append(i).append("] >> ").append(map.apply(array[i]));
        }
        return builder.append("\n    }").toString();
    }
    public static<T> String inspect(double[] doubles) {
        return inspect(doubles, ToStringFunction.Double.valueOf());
    }
    public static<T> String inspect(float[] floats) {
        return inspect(floats, ToStringFunction.Float.valueOf());
    }
    public static<T> String inspect(double[] doubles, ToStringFunction.Double toString) {
        int length;
        if (doubles == null || (length = doubles.length) == 0) {
            return doubles == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(doubles.getClass(), length);
        for (int i = 0; i < length; i++) {
            builder.append("\n    o->> [").append(i).append("] >> ")
                    .append(toString.apply(doubles[i])
                    );
        }
        return builder.append("\n    }").toString();
    }
    public static<T> String inspect(float[] floats, ToStringFunction.Float toString) {
        int length;
        if (floats == null || (length = floats.length) == 0) {
            return floats == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(floats.getClass(), length);
        for (int i = 0; i < length; i++) {
            builder.append("\n o->> [").append(i).append("] >> ").append(toString.apply(floats[i]));
        }
        return builder.append("\n    }").toString();
    }
    public static<T> String inspect(long[] longs) {
        return inspect(longs, ToStringFunction.Long.valueOf());
    }
    public static<T> String inspect(int[] ints) {
        return inspect(ints, ToStringFunction.Int.valueOf());
    }
    public static<T> String inspect(long[] longs, ToStringFunction.Long toString) {
        int length;
        if (longs == null || (length = longs.length) == 0) {
            return longs == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(longs.getClass(), length);
        for (int i = 0; i < length; i++) {
            builder.append("\n o->> [").append(i).append("] >> ").append(toString.apply(longs[i]));
        }
        return builder.append("\n    }").toString();
    }

    private static StringBuilder getBuilder(Class<?> aClass, int length) {
        return getStringBuilder(aClass.getComponentType().toString(), length);
    }

    private static StringBuilder getStringBuilder(String componentType, int length) {
        StringBuilder sb = new StringBuilder((length * 6) + 185);
        sb.append("\n Reading Array..." + "\n >> Type: ")
                .append(componentType)
                .append("\n >> Length: ")
                .append(length)
                .append("\n >> Contents: {");
        return sb;
    }

    public static<T> String inspect(int[] ints, ToStringFunction.Int toString) {
        int length;
        if (ints == null || (length = ints.length) == 0) {
            return ints == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(ints.getClass(), length);
        for (int i = 0; i < length; i++) {
            builder.append("\n o->> [").append(i).append("] >> ").append(toString.apply(ints[i]));
        }
        return builder.append("\n    }").toString();
    }

    public static String inspect(double[][] ts) {
        int length;
        if (ts == null || (length = ts.length) == 0) {
            return ts == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getStringBuilder("double[][]", length);
        for (int i = 0; i < length; i++) {
            double[] aDs = ts[i];
            builder.append("\n <*> row: [").append(i).append("]");
            for (int j = 0; j < aDs.length; j++) {
                builder.append("\n    [").append(j).append("] >> ").append(aDs[j]);
            }
        }
        return builder.append("\n    }").toString();
    }

    public static<E> String inspect(E[][] ts) {
        int length;
        if (ts == null || (length = ts.length) == 0) {
            return ts == null ? "Array is null" : "Array is empty";
        }
        StringBuilder builder = getBuilder(ts.getClass(), length);
        for (int i = 0; i < length; i++) {
            E[] aDs = ts[i];
            builder.append("\n <*> row: [").append(i).append("]");
            for (int j = 0; j < aDs.length; j++) {
                builder.append("\n    [").append(j).append("] >> ").append(aDs[j]);
            }
        }
        return builder.append("\n    }").toString();
    }

    /**Used for SQL statements*/
    public static<T> String toQueryString(long[] longs) {
        assert longs != null;
        String query = "(";
        int length;
        if ((length = longs.length) == 0) throw new IllegalStateException("Empty array");
        int last = length - 1;
        for (int i = 0; i < last; i++) {
            query = query.concat(String.valueOf(longs[i]).concat(", "));
        }
        return query.concat(String.valueOf(longs[last]).concat(")"));
    }

    public static String stripEdges(String stringArray) {
        return stringArray.substring(1, stringArray.length() - 1);
    }

    public static String[][] fromColumns(long[]... columns) {
        if (columns == null) throw new IllegalArgumentException("Null arguments");
        int totalColumns = columns.length;
        if (totalColumns == 0) return new String[0][0];
        else {
            long[] column;
            int rowCount = 0;
            int columnCount = 0;
            int totalRows = (column = columns[columnCount]).length;
            final String[][] res = new String[totalRows][totalColumns];
            try {
                for (; rowCount < totalRows; rowCount++) {
                    String slong = Long.toString(column[rowCount]);
                    res[rowCount][columnCount] = slong;
                }
                columnCount++;
                rowCount = 0;
                for (; columnCount < totalColumns; columnCount++) {
                    column = columns[columnCount];
                    if (column.length > totalRows) throw new IllegalStateException("Uneven column row length at column = " + columnCount);
                    for (; rowCount < totalRows; rowCount++) {
                        res[rowCount][columnCount] = Long.toString(column[rowCount]);
                    }
                    rowCount = 0;
                }
            } catch (Exception | Error e) {
                throw new IllegalStateException(
                        "Error at:"
                                + "\n row = " + rowCount
                                + "\n column = " + columnCount
                                + "\n value = " + columns[columnCount][rowCount]
                        , e
                );
            }
            return res;
        }
    }
    record EMPTY() {
        static final String ref = "";
        static final Predicate<String> isNull = Objects::isNull;
        static Predicate<String> isNull() {
            return isNull;
        }
    }
    public static void mapToEmpty(String[] source){
        for (int i = 0; i < source.length; i++) {
            if (!((source[i] == EMPTY.ref) || (source[i] != null && ((Object) source[i]).equals(EMPTY.ref)))) source[i] = EMPTY.ref;
        }
    }

    static final CompactHashTable<String, CompactArrayBuilder<String>> EMPTY_MAP = new CompactHashTable<>(0);
    static final IntFunction<String[]> creator = String[]::new;
    /**
     * Extracts contents within specified XML-style tags from a given sequence.
     *
     * @param tags     Array of tag names (without angle brackets).
     * @param sequence Input string to search within.
     * @return Map where each key is a tag, and value is a list of contents found.
     */
    public static CompactHashTable<String, CompactArrayBuilder<String>> orderByTag(String sequence, String... tags) {
        int tl;
        if (tags == null || (tl = tags.length) == 0) {
            return EMPTY_MAP;
        }
        Set<String> unique = new HashSet<>();

        for (int i = 0; i < tl; i++) {
            unique.add(tags[i]);
        }

        // Build regex pattern: <(tag1|tag2|...)>(.*?)</\1>
        String tagPattern = String.join("|", unique);
        Pattern pattern = Pattern.compile("<(" + tagPattern + ")>(.*?)</\\1>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sequence);

        final CompactHashTable<String, CompactArrayBuilder<String>> result = new CompactHashTable<>(tl * 2);

        while (matcher.find()) {
            String matchedTag = matcher.group(1);
            String content = matcher.group(2);

            CompactHashTable<String, CompactArrayBuilder<String>>.Node n = result.getNode(matchedTag);

            if (n == null) {
                result.put(
                        matchedTag,
                        CompactArrayBuilder.ofSize(5, creator)
                );
            } else {
                n.getValue().add(
                        content
                );
            }
        }

        return result;
    }

    public static String[] unfold(String sequence, String... tags) {
        int tl;
        if (tags == null || (tl = tags.length) == 0) {
            return new String[0];
        }
        Set<String> unique = new HashSet<>();

        for (int i = 0; i < tl; i++) {
            unique.add(tags[i]);
        }

        // Build regex pattern: <(tag1|tag2|...)>(.*?)</\1>
        String tagPattern = join("|", unique);
        Pattern pattern = Pattern.compile("<(" + tagPattern + ")>(.*?)</\\1>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(sequence);

        final CompactArrayBuilder<String> result = CompactArrayBuilder.ofSize((int) (tl * 1.5), creator);
        while (matcher.find()) {
            String content = matcher.group(2);

            result.add(content.trim());

        }

        return result.publish();
    }

    final static String
            escape = "(?<!\\\\)\"",
            replacement = "\\\\\"";
    public static String jsonSanitize(String input){ return input.replaceAll(escape, replacement); }

    public static<E> String inspect(Iterable<E> iterable) {
        if (iterable == null) {
            return EMPTY.ref;
        }
        StringJoiner joiner = new StringJoiner("\n   o->> [",
                "\n Reading Array...\n >> Contents: {",
                "    }"
                );
        E next;
        int i = 0;
        for (E e:iterable
        ) {
            String toAdd = Integer.toString(i++).concat(
                    "] >> \n"
            ).concat(
                    (next = e) == null ? "      [null]" : next.toString().indent(6)
            );
            joiner.add(
                    toAdd
                    );
        }
        return joiner.toString();
    }
    public static char getUpperCaseLetter(int index) {
        if (index < 0 || index > 25) {
            throw new IllegalArgumentException("Index must be between 0 and 25");
        }
        return (char) ('A' + index);
    }

    public record tagged_group(String tag, String content) {}
    public static Supplier<String> indentedGroups(tagged_group... groups) {
        return indentedGroups(0, groups);
    }
    public static Supplier<String> indentedGroups(int indent, tagged_group... groups) {
        int gl = groups.length;
        if (gl < 1) throw new IllegalStateException("no groups found.");
        StringBuilder builder = new StringBuilder(gl * 3);
        if (indent != 0) {
            return () -> {
                tagged_group tg = groups[0];
                String tag = tg.tag;
                builder.append("<").append(tag).append(">\n").append(
                        tg.content.indent(indent)
                ).append("</").append(tag).append(">");
                for (int i = 1; i < gl; i++) {
                    tg = groups[i];
                    tag = tg.tag;
                    builder.append("\n<").append(tag).append(">\n")
                            .append(
                                    tg.content.indent(indent)
                            ).append("</").append(tag).append(">");
                }
                return builder.toString();
            };
        } else {
            return () -> {
                tagged_group tg = groups[0];
                String tag = tg.tag;

                builder.append("<").append(tag).append("> ").append(
                        tg.content
                ).append(" </").append(tag).append(">");
                for (int i = 1; i < gl; i++) {
                    tg = groups[i];
                    tag = tg.tag;
                    builder.append("\n<").append(tag).append("> ")
                            .append(
                                    tg.content
                            ).append(" </").append(tag).append(">");
                }
                return builder.toString();
            };
        }
    }

    public static String taggedGroup(String tag, String... items) {
        int il = items.length;
        if (il < 1) throw new IllegalStateException("no items found.");
        final StringBuilder builder = new StringBuilder(il * 3);
        String start = "<".concat(tag).concat("> "), end = " </".concat(tag).concat(">");
        builder.append(start).append(items[0]).append(end);
        for (int i = 1; i < il; i++) {
            builder.append("\n").append(start).append(items[i]).append(end);
        }
        return builder.toString();
    }

    public static String cropAfterFirst(String pattern, boolean leaveOutPattern, String input) {
        return input.replaceAll("(?s)^(.*?)" + pattern + (leaveOutPattern ? "" : "(.*?)") + ".*", "$1");
    }
}

