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
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.RegistryConverter;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.version.MinecraftVersion;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/**
 * A {@link BlockRegistry} for a specific version of the game.
 *
 * @author DaPorkchop_
 */
public interface VersionBlockRegistry extends BlockRegistry {
    /**
     * @return the version of the game that this registry is for
     */
    MinecraftVersion version();

    /**
     * Registers a new block type.
     *
     * @param serializer the {@link StateSerializer} to use for serializing
     * @param globalType the {@link BlockType} registered in the global block registry
     * @return the block type as newly added to this registry
     */
    BlockType register(@NonNull StateSerializer serializer, @NonNull BlockType globalType);

    /**
     * @return a {@link RegistryConverter} for converting this registry's runtime IDs to the global block registry
     */
    RegistryConverter toGlobal();

    /**
     * @return the number of registered blocks
     */
    int blocks();

    /**
     * Checks whether or not the given block ID is registered.
     *
     * @param blockId the block ID to check for
     * @return whether or not the given block ID is registered
     */
    boolean containsBlockId(@NonNull Identifier blockId);

    /**
     * Checks whether or not the given legacy ID is registered.
     *
     * @param legacyId the legacy ID to check for
     * @return whether or not the given legacy ID is registered
     */
    boolean containsLegacyId(int legacyId);

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param blockId the block ID of the block state to check for
     * @param meta    the metadata of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(@NonNull Identifier blockId, int meta);

    /**
     * Checks whether or not the given block state is registered.
     *
     * @param legacyId the legacy ID of the block state to check for
     * @param meta     the metadata of the block state to check for
     * @return whether or not the given block state is registered
     */
    boolean containsState(int legacyId, int meta);

    /**
     * Checks if the given block ID is registered and has an associated legacy ID_CHEST.
     * <p>
     * As some blocks do not have a legacy ID, this will not always return {@code true}, even if the block ID_CHEST is valid.
     *
     * @param blockId the block ID of the block
     * @return whether or not the given block ID is registered and has an associated legacy ID_CHEST
     */
    boolean hasLegacyId(@NonNull Identifier blockId);

    /**
     * Gets the legacy ID belonging to the given block.
     *
     * @param blockId the block ID of the block
     * @return the block's legacy ID
     */
    int getLegacyId(@NonNull Identifier blockId);

    /**
     * Gets the block ID belonging to the given block.
     *
     * @param legacyId the legacy ID of the block
     * @return the block's ID
     * @throws IllegalArgumentException if the given legacy ID is not registered
     */
    Identifier getBlockId(int legacyId);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param blockId the block ID of the block state to get
     * @param meta    the metadata of the block state to get
     * @return the {@link BlockState} for the given block state
     * @throws IllegalArgumentException if the given block state is not registered
     */
    BlockState getState(@NonNull Identifier blockId, int meta);

    /**
     * Gets the {@link BlockState} for to the given block state.
     *
     * @param legacyId the legacy ID of the block state to get
     * @param meta     the metadata of the block state to get
     * @return the {@link BlockState} for the given block state
     * @throws IllegalArgumentException if the given block state is not registered
     */
    BlockState getState(int legacyId, int meta);

    /**
     * Gets the default {@link BlockState} for to the given block ID.
     *
     * @param blockId the block ID of the block state to get
     * @return the default {@link BlockState} for the given block ID
     * @throws IllegalArgumentException if the given block ID is not registered
     */
    BlockState getDefaultState(@NonNull Identifier blockId);

    /**
     * Gets the default {@link BlockState} for to the given legacy block ID.
     *
     * @param legacyId the legacy ID of the block state to get
     * @return the default {@link BlockState} for the given legacy block ID
     * @throws IllegalArgumentException if the given legacy ID is not registered
     */
    BlockState getDefaultState(int legacyId);

    /**
     * Gets the runtime ID for the given block state.
     *
     * @param blockId the block ID of the block state to get
     * @param meta    the metadata of the block state to get
     * @return the runtime ID for the given block state
     * @throws IllegalArgumentException if the given block state is not registered
     */
    int getRuntimeId(@NonNull Identifier blockId, int meta);

    /**
     * Gets the runtime ID for the given block state.
     *
     * @param legacyId the legacy ID of the block state to get
     * @param meta     the metadata of the block state to get
     * @return the runtime ID for the given block state
     * @throws IllegalArgumentException if the given block state is not registered
     */
    int getRuntimeId(int legacyId, int meta);

    void forEachBlockId(@NonNull Consumer<? super Identifier> action);

    void forEachLegacyId(@NonNull IntConsumer action);

    /**
     * Performs the given action on each block in this registry.
     * <p>
     * The second parameter is the block's legacy ID. If the block does not have a legacy ID_CHEST, it will always be {@code -1}.
     *
     * @param action the action to perform
     */
    void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action);
}
