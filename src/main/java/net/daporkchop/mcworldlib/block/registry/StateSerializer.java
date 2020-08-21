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

package net.daporkchop.mcworldlib.block.registry;

import lombok.NonNull;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.trait.Trait;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Map;

/**
 * Serializes a global block state (block type and trait values) to a version-specific state.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface StateSerializer {
    /**
     * Serializes the given block state.
     *
     * @param typeIn   the {@link BlockType} of the state to serialize
     * @param traitsIn the {@link Trait} values of the state to serialize
     * @param out      a {@link CompoundTag} where the output traits should be written
     * @return the {@link Identifier} of the output block, or {@code null} to ignore this state
     */
    Identifier serialize(@NonNull BlockType typeIn, @NonNull Map<Trait<?>, ?> traitsIn, @NonNull Output out);

    /**
     * Helper callback to set the output values for a {@link StateSerializer}.
     *
     * @author DaPorkchop_
     */
    @FunctionalInterface
    interface Output {
        /**
         * Adds a given {@link Trait} with the given value.
         *
         * @param trait the {@link Trait} to add
         * @param value the trait value
         * @param <T>   the trait type
         */
        <T> void trait(@NonNull Trait<T> trait, @NonNull T value);
    }
}
