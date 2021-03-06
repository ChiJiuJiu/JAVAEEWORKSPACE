/*
 * Copyright (C) 2017 The Guava Authors
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

package edu.yctc.zyj.hash;

import static edu.yctc.zyj.common.Preconditions.checkArgument;
import static edu.yctc.zyj.common.Preconditions.checkPositionIndexes;

import edu.yctc.zyj.bloomfilter.Funnel;

/**
 * Skeleton implementation of {@link HashFunction} in terms of {@link #newHasher()}.
 * <p>
 * TODO(lowasser): make public
 */
abstract class AbstractHashFunction implements HashFunction {

    @Override
    public <T> HashCode hashObject(T instance, Funnel<? super T> funnel) {
        return newHasher().putObject(instance, funnel).hash();
    }

    @Override
    public HashCode hashBytes(byte[] input, int off, int len) {
        checkPositionIndexes(off, off + len, input.length);
        return newHasher(len).putBytes(input, off, len).hash();
    }

    @Override
    public Hasher newHasher(int expectedInputSize) {
        checkArgument(expectedInputSize >= 0, "expectedInputSize must be >= 0 but was %s", expectedInputSize);
        return newHasher();
    }
}
