/*
 * Adapted from The MIT License (MIT)
 *
 * Copyright (c) 2020-2020 DaPorkchop_
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy,
 * modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * Any persons and/or organizations using this software must include the above copyright notice and this permission notice,
 * provide sufficient credit to the original authors of the project (IE: DaPorkchop_), as well as provide a link to the original project.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package net.daporkchop.mcworldlib.block;

import lombok.NonNull;

/**
 * A mapping of {@link Trait} values to {@link BlockState}s.
 *
 * @author DaPorkchop_
 */
public interface PropertyMap<V> {
    /**
     * Gets the {@link BlockState} mapped to the given value.
     *
     * @param value the value
     * @return the {@link BlockState} mapped to the given value
     * @throws IllegalArgumentException if the given {@link Trait} cannot store the given value
     */
    BlockState getState(@NonNull V value);

    /**
     * Extension of {@link PropertyMap} for {@code int} values.
     *
     * @author DaPorkchop_
     */
    interface Int extends PropertyMap<Integer> {
        @Override
        @Deprecated
        default BlockState getState(@NonNull Integer value) {
            return this.getState(value.intValue());
        }

        /**
         * Gets the {@link BlockState} mapped to the given value.
         *
         * @param value the value
         * @return the {@link BlockState} mapped to the given value
         * @throws IllegalArgumentException if the given {@link Trait} cannot store the given value
         */
        BlockState getState(int value);
    }

    /**
     * Extension of {@link PropertyMap} for {@code boolean} values.
     *
     * @author DaPorkchop_
     */
    interface Boolean extends PropertyMap<java.lang.Boolean> {
        @Override
        @Deprecated
        default BlockState getState(@NonNull java.lang.Boolean value) {
            return this.getState(value.booleanValue());
        }

        /**
         * Gets the {@link BlockState} mapped to the given value.
         *
         * @param value the value
         * @return the {@link BlockState} mapped to the given value
         * @throws IllegalArgumentException if the given {@link Trait} cannot store the given value
         */
        BlockState getState(boolean value);
    }
}
