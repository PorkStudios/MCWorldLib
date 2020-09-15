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

package net.daporkchop.mcworldlib.format.common.storage.flattened;

import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.format.common.storage.AbstractBlockStorage;
import net.daporkchop.mcworldlib.world.storage.BlockStorage;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;
import net.daporkchop.mcworldlib.world.storage.UniversalBlockStorage;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractFlattenedBlockStorage extends AbstractBlockStorage implements FlattenedBlockStorage {
    protected static int index(int x, int y, int z) {
        BlockStorage.checkCoords(x, y, z);
        return (y << 8) | (z << 4) | x;
    }

    @Override
    public UniversalBlockStorage toUniversal(boolean preferView) {
        throw new UnsupportedOperationException(); //TODO: implement this
    }

    @Override
    public FlattenedBlockStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    public abstract FlattenedBlockStorage clone();
}
