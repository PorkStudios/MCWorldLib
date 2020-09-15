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

package net.daporkchop.mcworldlib.format.common.section.legacy;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.section.AbstractSection;
import net.daporkchop.mcworldlib.world.section.LegacySection;
import net.daporkchop.mcworldlib.world.section.Section;
import net.daporkchop.mcworldlib.world.storage.LegacyBlockStorage;

/**
 * @author DaPorkchop_
 */
public class DefaultLegacySection extends AbstractSection implements LegacySection {
    protected final LegacyBlockStorage blocks;

    public DefaultLegacySection(int x, int y, int z, @NonNull LegacyBlockStorage blocks, @NonNull NibbleArray blockLight, NibbleArray skyLight) {
        super(x, y, z, blockLight, skyLight);

        this.blocks = blocks;
    }

    @Override
    public LegacySection retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        super.doRelease();

        this.blocks.release();
    }

    @Override
    public LegacyBlockStorage blockStorage() {
        return this.blocks;
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.blocks.getBlockLegacyId(x, y, z);
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.blocks.getBlockMeta(x, y, z);
    }

    @Override
    public int getCombinedIdMeta(int x, int y, int z) {
        return this.blocks.getCombinedIdMeta(x, y, z);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.blocks.setBlockState(x, y, z, legacyId, meta);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.blocks.setBlockLegacyId(x, y, z, legacyId);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.blocks.setBlockMeta(x, y, z, meta);
    }
}
