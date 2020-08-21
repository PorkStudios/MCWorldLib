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

package net.daporkchop.mcworldlib.block.trait;

import lombok.NonNull;

import java.util.List;

/**
 * A key used to define an attribute of a block state.
 *
 * @author DaPorkchop_
 */
public interface Trait<V> {
    /**
     * @return this property's name
     */
    String name();

    /**
     * @return a list of all valid values for this property
     */
    List<V> values();

    /**
     * @return this trait's default value
     */
    V defaultValue();

    /**
     * Checks whether or not the given value is valid.
     *
     * @param value the value to check
     * @return whether or not the given value is valid
     */
    boolean isValid(@NonNull V value);

    /**
     * Gets the index of a value.
     *
     * @param value the value to get the index for
     * @return the value's index
     */
    int valueIndex(@NonNull V value);

    /**
     * Gets a value from its index.
     *
     * @param index the index to get the value for
     * @return the value at the given index
     */
    V valueFromIndex(int index);

    /**
     * Encodes a value to its {@link String} representation.
     *
     * @param value the value to encode
     * @return the encoded value
     */
    String encodeValue(@NonNull V value);

    /**
     * Decodes a value from its {@link String} representation.
     *
     * @param encoded the encoded value
     * @return the decoded value
     */
    V decodeValue(@NonNull String encoded);
}
