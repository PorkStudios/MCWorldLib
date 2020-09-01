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

package net.daporkchop.mcworldlib.world.common;

import net.daporkchop.lib.common.misc.Cloneable;
import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.block.access.BlockAccess;
import net.daporkchop.mcworldlib.block.BlockRegistry;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A 16³ array of block states.
 * <p>
 * All IDs used by all methods use the local block registry.
 *
 * @author DaPorkchop_
 * @see BlockRegistry
 */
public interface IBlockStorage<I extends IBlockStorage> extends Cloneable<I>, RefCounted {
    /**
     * The number of blocks in a single block storage.
     */
    int NUM_BLOCKS = 16 * 16 * 16;

    static void checkCoords(int x, int y, int z) {
        checkIndex(x >= 0 && x < 16, "x");
        checkIndex(y >= 0 && y < 16, "y");
        checkIndex(z >= 0 && z < 16, "z");
    }

    /**
     * @return the {@link BlockRegistry} that this {@link IBlockStorage} uses
     */
    BlockRegistry localRegistry();

    /**
     * Gets this {@link IBlockStorage} using the global block registry.
     * <p>
     * WARNING! After calling this method, this {@link IBlockStorage} is implicitly released. Do not use this for shared instances!
     *
     * @param preferView hints to the implementation that a view would be preferred over a copy
     * @return this {@link IBlockStorage} in the global block registry
     */
    //TODO: this
    //IBlockStorage toGlobal(boolean preferView);

    @Override
    int refCnt();

    @Override
    I retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}