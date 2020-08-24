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

package net.daporkchop.mcworldlib.format.common.section;

import lombok.NonNull;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.storage.BlockStorage;
import net.daporkchop.mcworldlib.world.Section;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of a 2-layer {@link Section} which does not initially have a second layer, and instead creates it lazily.
 *
 * @author DaPorkchop_
 */
public class LazyLayer1Section extends DefaultSection {
    protected BlockStorage layer1;

    public LazyLayer1Section(int x, int y, int z, @NonNull BlockStorage layer0, @NonNull NibbleArray blockLight, NibbleArray skyLight) {
        super(x, y, z, layer0, blockLight, skyLight);
    }

    @Override
    public int layers() {
        return 2;
    }

    @Override
    public BlockStorage blockStorage(int layer) {
        checkIndex((layer & ~1) == 0, "invalid layer: %d (must be in range [0,1])", layer);
        if (layer == 0) {
            return this.blocks;
        }

        BlockStorage layer1 = this.layer1;
        if (layer1 == null) {
            this.layer1 = layer1 = this.blocks.localRegistry().createStorage();
        }
        return layer1;
    }

    @Override
    protected void doRelease() {
        super.doRelease();

        if (this.layer1 != null) {
            this.layer1.release();
        }
    }
}
