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

package net.daporkchop.mcworldlib.format.common.storage;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

/**
 * Base implementation of {@link BlockStorage} for the legacy block format used in Anvil chunk sections prior to The Flatting™️.
 *
 * @author DaPorkchop_
 */
public abstract class AbstractBlockStorage extends AbstractRefCounted implements BlockStorage {
    @Getter
    protected final BlockRegistry localRegistry;

    public AbstractBlockStorage(@NonNull BlockRegistry localRegistry) {
        this.localRegistry = localRegistry;
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.localRegistry.getState(this.getBlockRuntimeId(x, y, z));
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return this.localRegistry.getBlockId(this.getBlockLegacyId(x, y, z));
    }

    @Override
    public abstract int getBlockLegacyId(int x, int y, int z);

    @Override
    public abstract int getBlockMeta(int x, int y, int z);

    @Override
    public abstract int getBlockRuntimeId(int x, int y, int z);

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        if (this.localRegistry == state.registry()) {
            this.setBlockRuntimeId(x, y, z, state.runtimeId());
        } else {
            this.setBlockState(x, y, z, state.legacyId(), state.meta());
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.setBlockRuntimeId(x, y, z, this.localRegistry.getRuntimeId(id, meta));
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.setBlockRuntimeId(x, y, z, this.localRegistry.getRuntimeId(legacyId, meta));
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        this.setBlockRuntimeId(x, y, z, this.localRegistry.getRuntimeId(id, 0));
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.setBlockRuntimeId(x, y, z, this.localRegistry.getRuntimeId(legacyId, 0));
    }

    @Override
    public abstract void setBlockMeta(int x, int y, int z, int meta);

    @Override
    public abstract void setBlockRuntimeId(int x, int y, int z, int runtimeId);

    @Override
    public BlockStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    public abstract BlockStorage clone();

    @Override
    protected abstract void doRelease();
}
