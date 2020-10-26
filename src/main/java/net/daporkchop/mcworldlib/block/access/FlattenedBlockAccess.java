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

import net.daporkchop.mcworldlib.block.BlockState;

/**
 * Provides access to flattened block data at given coordinates.
 *
 * @author DaPorkchop_
 */
public interface FlattenedBlockAccess {
    //
    //
    // getters
    //
    //

    /**
     * Gets the block state at the given coordinates in the default layer.
     *
     * @param x the X coordinate of the block to get
     * @param y the Y coordinate of the block to get
     * @param z the Z coordinate of the block to get
     * @return the block state
     */
    BlockState getBlockState(int x, int y, int z);

    //
    //
    // setters
    //
    //

    /**
     * Sets the block state at the given coordinates in the default layer.
     *
     * @param x     the X coordinate of the block to set
     * @param y     the Y coordinate of the block to set
     * @param z     the Z coordinate of the block to set
     * @param state the new block state
     */
    void setBlockState(int x, int y, int z, BlockState state);
}
