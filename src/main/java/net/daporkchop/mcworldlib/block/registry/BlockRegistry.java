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
import net.daporkchop.lib.primitive.lambda.ObjIntConsumer;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.RegistryConverter;
import net.daporkchop.mcworldlib.registry.Registry;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.stream.Stream;

/**
 * Extension of {@link Registry} for blocks.
 * <p>
 * Note that all methods inherited from {@link Registry} (except {@link Registry#id()}) consider only mappings between block {@link Identifier}s and
 * legacy block IDs.
 * <p>
 * A block registry consists of a number of different mappings to and from different types, making it a fair bit more complicated than most other
 * registries. A brief description of each type follows:
 * <p>
 * - "block IDs" or "block {@link Identifier}s" are user-friendly {@link Identifier}s given to each block, for example {@code minecraft:stone} (Stone)
 * or {@code minecraft:diamond_ore} (Diamond Ore)
 * <p>
 * - "legacy IDs" are {@code int} IDs given to blocks, for example 1 (Stone) or 56 (Diamond Ore). However, as they are no longer used in modern
 * versions of the game, newer blocks will not have one.
 * <p>
 * - "metadata" are {@code int} values, generally in range 0-15, which describe the a block state. For example, {@code minecraft:stone} uses metadata
 * to differentiate between the different stone varieties (Andesite, etc.), or {@code minecraft:redstone} uses 16 metadata values to store its current
 * power level
 * <p>
 * - "block states" (normally referred to as {@link BlockState}) are simply a combination of a block (defined either by legacy or block ID) and a
 * metadata value
 * <p>
 * - "runtime IDs" are {@code int} IDs given to each block state, and serve no purpose beyond performance
 *
 * @author DaPorkchop_
 */
//TODO: a system for registry "contexts": allowing for modded block IDs to be registered
public interface BlockRegistry {
    Identifier ID = Identifier.fromString("minecraft:block");

    /**
     * @return the number of registered block states
     */
    int states();

    /**
     * @return the highest runtime ID registered
     */
    int maxRuntimeId();

    /**
     * @return the {@link BlockState} used to represent air
     */
    BlockState air();

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param runtimeId the runtime ID of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(int runtimeId);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param runtimeId the runtime ID of the block state to get
     * @return the {@link BlockState} for the given block state
     * @throws IllegalArgumentException if the given runtime ID is not registered
     */
    BlockState getState(int runtimeId);

    void forEachState(@NonNull Consumer<? super BlockState> action);

    void forEachRuntimeId(@NonNull IntConsumer action);
}
