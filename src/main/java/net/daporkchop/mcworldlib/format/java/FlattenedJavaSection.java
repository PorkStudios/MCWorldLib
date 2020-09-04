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

package net.daporkchop.mcworldlib.format.java;

import lombok.NonNull;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.section.DefaultSection;
import net.daporkchop.mcworldlib.world.storage.BlockStorage;
import net.daporkchop.mcworldlib.world.section.Section;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of a 2-layer {@link Section} which has a fixed a second layer.
 *
 * @author DaPorkchop_
 */
public class FlattenedJavaSection extends DefaultSection {
    protected final BlockStorage layer1;

    public FlattenedJavaSection(int x, int y, int z, @NonNull BlockStorage layer0, @NonNull BlockStorage layer1, @NonNull NibbleArray blockLight, NibbleArray skyLight) {
        super(x, y, z, layer0, blockLight, skyLight);

        this.layer1 = layer1;
    }

    @Override
    public int layers() {
        return 2;
    }

    @Override
    public BlockStorage blockStorage(int layer) {
        checkIndex((layer & ~1) == 0, "invalid layer: %d (must be in range [0,1])", layer);
        return layer == 0 ? this.blocks : this.layer1;
    }

    @Override
    protected void doRelease() {
        super.doRelease();

        this.layer1.release();
    }
}
