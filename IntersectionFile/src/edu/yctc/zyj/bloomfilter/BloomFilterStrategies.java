/*
 * Copyright (C) 2011 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package edu.yctc.zyj.bloomfilter;

import static edu.yctc.zyj.common.Preconditions.checkArgument;
import static edu.yctc.zyj.common.Preconditions.checkNotNull;
import static java.lang.Math.abs;
import static java.math.RoundingMode.HALF_EVEN;
import static java.math.RoundingMode.HALF_UP;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLongArray;

import edu.yctc.zyj.hash.Murmur3_128HashFunction;

/**
 * Collections of strategies of generating the k * log(M) bits required for an element to be mapped to a BloomFilter of
 * M bits and k hash functions. These strategies are part of the serialized form of the Bloom filters that use them,
 * thus they must be preserved as is (no updates allowed, only introduction of new versions).
 * <p>
 * Important: the order of the constants cannot change, and they cannot be deleted - we depend on their ordinal for
 * BloomFilter serialization.
 *
 * @author Dimitris Andreou
 * @author Kurt Alfred Kluever
 */
public enum BloomFilterStrategies implements BloomFilter.Strategy {
                                                                   /**
                                                                    * This strategy uses all 128 bits of
                                                                    * {@link Hashing#murmur3_128} when hashing. It looks
                                                                    * different than the implementation in
                                                                    * MURMUR128_MITZ_32 because we're avoiding the
                                                                    * multiplication in the loop and doing a (much
                                                                    * simpler) += hash2. We're also changing the index
                                                                    * to a positive number by AND'ing with
                                                                    * Long.MAX_VALUE instead of flipping the bits.
                                                                    */
                                                                   MURMUR128_MITZ_64() {

                                                                       @Override
                                                                       public <T> boolean put(T object,
                                                                                              Funnel<? super T> funnel,
                                                                                              int numHashFunctions,
                                                                                              LockFreeBitArray bits) {
                                                                           long bitSize = bits.bitSize();
                                                                           byte[] bytes = Murmur3_128HashFunction.MURMUR3_128.hashObject(object,
                                                                                                                                         funnel).getBytesInternal();
                                                                           long hash1 = lowerEight(bytes);
                                                                           long hash2 = upperEight(bytes);

                                                                           boolean bitsChanged = false;
                                                                           long combinedHash = hash1;
                                                                           for (int i = 0; i < numHashFunctions; i++) {
                                                                               // Make the combined hash positive and
                                                                               // indexable
                                                                               bitsChanged |= bits.set((combinedHash
                                                                                                        & Long.MAX_VALUE)
                                                                                                       % bitSize);
                                                                               combinedHash += hash2;
                                                                           }
                                                                           return bitsChanged;
                                                                       }

                                                                       @Override
                                                                       public <T> boolean mightContain(T object,
                                                                                                       Funnel<? super T> funnel,
                                                                                                       int numHashFunctions,
                                                                                                       LockFreeBitArray bits) {
                                                                           long bitSize = bits.bitSize();
                                                                           byte[] bytes = Murmur3_128HashFunction.MURMUR3_128.hashObject(object,
                                                                                                                                         funnel).getBytesInternal();
                                                                           long hash1 = lowerEight(bytes);
                                                                           long hash2 = upperEight(bytes);

                                                                           long combinedHash = hash1;
                                                                           for (int i = 0; i < numHashFunctions; i++) {
                                                                               // Make the combined hash positive and
                                                                               // indexable
                                                                               if (!bits.get((combinedHash
                                                                                              & Long.MAX_VALUE)
                                                                                             % bitSize)) {
                                                                                   return false;
                                                                               }
                                                                               combinedHash += hash2;
                                                                           }
                                                                           return true;
                                                                       }

                                                                       private /* static */ long lowerEight(byte[] bytes) {
                                                                           return fromBytes(bytes[7], bytes[6],
                                                                                            bytes[5], bytes[4],
                                                                                            bytes[3], bytes[2],
                                                                                            bytes[1], bytes[0]);
                                                                       }

                                                                       private /* static */ long upperEight(byte[] bytes) {
                                                                           return fromBytes(bytes[15], bytes[14],
                                                                                            bytes[13], bytes[12],
                                                                                            bytes[11], bytes[10],
                                                                                            bytes[9], bytes[8]);
                                                                       }

                                                                       /**
                                                                        * Returns the {@code long} value whose byte
                                                                        * representation is the given 8 bytes, in
                                                                        * big-endian order; equivalent to
                                                                        * {@code Longs.fromByteArray(new byte[] {b1, b2,
                                                                        * b3, b4, b5, b6, b7, b8})}.
                                                                        *
                                                                        * @since 7.0
                                                                        */
                                                                       private long fromBytes(byte b1, byte b2, byte b3,
                                                                                              byte b4, byte b5, byte b6,
                                                                                              byte b7, byte b8) {
                                                                           return (b1 & 0xFFL) << 56
                                                                                  | (b2 & 0xFFL) << 48
                                                                                  | (b3 & 0xFFL) << 40
                                                                                  | (b4 & 0xFFL) << 32
                                                                                  | (b5 & 0xFFL) << 24
                                                                                  | (b6 & 0xFFL) << 16
                                                                                  | (b7 & 0xFFL) << 8 | (b8 & 0xFFL);
                                                                       }
                                                                   };

    /**
     * Models a lock-free array of bits.
     * <p>
     * We use this instead of java.util.BitSet because we need access to the array of longs and we need
     * compare-and-swap.
     */
    static final class LockFreeBitArray {

        private static final int  LONG_ADDRESSABLE_BITS = 6;
        final AtomicLongArray     data;
        private final LongAddable bitCount;

        LockFreeBitArray(long bits){
            this(new long[checkedCast(divide(bits, 64, RoundingMode.CEILING))]);
        }

        // Used by serialization
        LockFreeBitArray(long[] data){
            checkArgument(data.length > 0, "data length is zero!");
            this.data = new AtomicLongArray(data);
            this.bitCount = LongAddables.create();
            long bitCount = 0;
            for (long value : data) {
                bitCount += Long.bitCount(value);
            }
            this.bitCount.add(bitCount);
        }

        /** Returns true if the bit changed value. */
        boolean set(long bitIndex) {
            if (get(bitIndex)) {
                return false;
            }

            int longIndex = (int) (bitIndex >>> LONG_ADDRESSABLE_BITS);
            long mask = 1L << bitIndex; // only cares about low 6 bits of bitIndex

            long oldValue;
            long newValue;
            do {
                oldValue = data.get(longIndex);
                newValue = oldValue | mask;
                if (oldValue == newValue) {
                    return false;
                }
            } while (!data.compareAndSet(longIndex, oldValue, newValue));

            // We turned the bit on, so increment bitCount.
            bitCount.increment();
            return true;
        }

        boolean get(long bitIndex) {
            return (data.get((int) (bitIndex >>> 6)) & (1L << bitIndex)) != 0;
        }

        /**
         * Careful here: if threads are mutating the atomicLongArray while this method is executing, the final long[]
         * will be a "rolling snapshot" of the state of the bit array. This is usually good enough, but should be kept
         * in mind.
         */
        public static long[] toPlainArray(AtomicLongArray atomicLongArray) {
            long[] array = new long[atomicLongArray.length()];
            for (int i = 0; i < array.length; ++i) {
                array[i] = atomicLongArray.get(i);
            }
            return array;
        }

        /** Number of bits */
        long bitSize() {
            return (long) data.length() * Long.SIZE;
        }


        @Override
        public boolean equals(Object o) {
            if (o instanceof LockFreeBitArray) {
                LockFreeBitArray lockFreeBitArray = (LockFreeBitArray) o;
                // TODO(lowasser): avoid allocation here
                return Arrays.equals(toPlainArray(data), toPlainArray(lockFreeBitArray.data));
            }
            return false;
        }

        @Override
        public int hashCode() {
            // TODO(lowasser): avoid allocation here
            return Arrays.hashCode(toPlainArray(data));
        }

        /**
         * Returns the {@code int} value that is equal to {@code value}, if possible.
         *
         * @param value any value in the range of the {@code int} type
         * @return the {@code int} value that equals {@code value}
         * @throws IllegalArgumentException if {@code value} is greater than {@link Integer#MAX_VALUE} or less than
         * {@link Integer#MIN_VALUE}
         */
        public static int checkedCast(long value) {
            int result = (int) value;
            checkArgument(result == value, "Out of range: %s", value);
            return result;
        }

        /**
         * Returns the result of dividing {@code p} by {@code q}, rounding using the specified {@code
         * RoundingMode}.
         *
         * @throws ArithmeticException if {@code q == 0}, or if {@code mode == UNNECESSARY} and {@code a} is not an
         * integer multiple of {@code b}
         */
        @SuppressWarnings("fallthrough")
        private static long divide(long p, long q, RoundingMode mode) {
            checkNotNull(mode);
            long div = p / q; // throws if q == 0
            long rem = p - q * div; // equals p % q

            if (rem == 0) {
                return div;
            }

            /*
             * Normal Java division rounds towards 0, consistently with RoundingMode.DOWN. We just have to deal with the
             * cases where rounding towards 0 is wrong, which typically depends on the sign of p / q. signum is 1 if p
             * and q are both nonnegative or both negative, and -1 otherwise.
             */
            int signum = 1 | (int) ((p ^ q) >> (Long.SIZE - 1));
            boolean increment;
            switch (mode) {
                case UNNECESSARY:
                    checkRoundingUnnecessary(rem == 0);
                    // fall through
                case DOWN:
                    increment = false;
                    break;
                case UP:
                    increment = true;
                    break;
                case CEILING:
                    increment = signum > 0;
                    break;
                case FLOOR:
                    increment = signum < 0;
                    break;
                case HALF_EVEN:
                case HALF_DOWN:
                case HALF_UP:
                    long absRem = abs(rem);
                    long cmpRemToHalfDivisor = absRem - (abs(q) - absRem);
                    // subtracting two nonnegative longs can't overflow
                    // cmpRemToHalfDivisor has the same sign as compare(abs(rem), abs(q) / 2).
                    if (cmpRemToHalfDivisor == 0) { // exactly on the half mark
                        increment = (mode == HALF_UP | (mode == HALF_EVEN & (div & 1) != 0));
                    } else {
                        increment = cmpRemToHalfDivisor > 0; // closer to the UP value
                    }
                    break;
                default:
                    throw new AssertionError();
            }
            return increment ? div + signum : div;
        }

        private static void checkRoundingUnnecessary(boolean condition) {
            if (!condition) {
                throw new ArithmeticException("mode was UNNECESSARY, but rounding was necessary");
            }
        }
    }
}
