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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Bi-directional lookup for converting block runtime IDs between a given block registry and the global block registry.
 * <p>
 * Note that the conversion operation may be lossy. Generally, a lookup for an unknown ID or an ID with no decent equivalent in the other version
 * will return {@code 0} (air).
 *
 * @author DaPorkchop_
 */
public interface RegistryConverter {
    /**
     * Converts the given ID to the global block registry.
     *
     * @param id the ID to convert
     * @return the converted ID
     */
    int toGlobal(int id);

    /**
     * Converts the given ID from the global block registry.
     *
     * @param id the ID to convert
     * @return the converted ID
     */
    int fromGlobal(int id);
}
