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

package net.daporkchop.mcworldlib.block.access;

import lombok.NonNull;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.util.Identifier;

/**
 * Provides access to block data at given coordinates.
 *
 * @author DaPorkchop_
 */
public interface BlockAccess {
    //
    //
    // getters
    //
    //

    /**
     * Gets the {@link BlockState} at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the {@link BlockState}
     */
    BlockState getBlockState(int x, int y, int z);

    /**
     * Gets the {@link Identifier} of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the {@link Identifier} of the block
     */
    Identifier getBlockId(int x, int y, int z);

    /**
     * Gets the legacy ID of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the legacy ID of the block
     */
    int getBlockLegacyId(int x, int y, int z);

    /**
     * Gets the metadata of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the metadata of the block
     */
    int getBlockMeta(int x, int y, int z);

    /**
     * Gets the runtime ID of the block at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the runtime ID of the block
     */
    int getBlockRuntimeId(int x, int y, int z);

    //
    //
    // setters
    //
    //

    /**
     * Sets the {@link BlockState} at the given coordinates in the default layer.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param state the new {@link BlockState}
     */
    void setBlockState(int x, int y, int z, @NonNull BlockState state);

    /**
     * Sets the block state at the given coordinates in the default layer.
     *
     * @param x    the X coordinate of the block to set
     * @param y    the Y coordinate of the block to set
     * @param z    the Z coordinate of the block to set
     * @param id   the new {@link Identifier}
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given block {@link Identifier}
     */
    void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta);

    /**
     * Sets the block state at the given coordinates in the default layer.
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param legacyId the new legacy ID
     * @param meta     the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given legacy block ID
     */
    void setBlockState(int x, int y, int z, int legacyId, int meta);

    /**
     * Sets the {@link Identifier} of the block at the given coordinates in the default layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x  the X coordinate of the block to set
     * @param y  the Y coordinate of the block to set
     * @param z  the Z coordinate of the block to set
     * @param id the new {@link Identifier}
     */
    void setBlockId(int x, int y, int z, @NonNull Identifier id);

    /**
     * Sets the legacy ID of the block at the given coordinates in the default layer.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x        the X coordinate of the block to set
     * @param y        the Y coordinate of the block to set
     * @param z        the Z coordinate of the block to set
     * @param legacyId the new legacy ID
     */
    void setBlockLegacyId(int x, int y, int z, int legacyId);

    /**
     * Sets the metadata of the block at the given coordinates in the default layer.
     * <p>
     * The block ID will remain unaffected by this change.
     *
     * @param x    the X coordinate of the block to set
     * @param y    the Y coordinate of the block to set
     * @param z    the Z coordinate of the block to set
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the block at the given coordinates
     */
    void setBlockMeta(int x, int y, int z, int meta);

    /**
     * Sets the runtime ID of the block at the given coordinates in the default layer.
     *
     * @param x         the X coordinate of the block to set
     * @param y         the Y coordinate of the block to set
     * @param z         the Z coordinate of the block to set
     * @param runtimeId the new runtime ID
     */
    void setBlockRuntimeId(int x, int y, int z, int runtimeId);
}
