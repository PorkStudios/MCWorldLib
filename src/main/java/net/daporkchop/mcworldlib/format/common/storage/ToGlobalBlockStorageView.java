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
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.RegistryConverter;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.world.common.IBlockStorage;

/**
 * @author DaPorkchop_
 */
@Getter
public class ToGlobalBlockStorageView implements IBlockStorage {
    protected final RegistryConverter converter;
    protected final IBlockStorage delegate;

    public ToGlobalBlockStorageView(@NonNull IBlockStorage delegate) {
        this.delegate = delegate;
        this.converter = delegate.localRegistry().toGlobal();
    }

    @Override
    public BlockRegistry localRegistry() {
        return BlockRegistry.global();
    }

    @Override
    public IBlockStorage toGlobal(boolean preferView) {
        return this;
    }

    @Override
    public int refCnt() {
        return this.delegate.refCnt();
    }

    @Override
    public IBlockStorage retain() throws AlreadyReleasedException {
        this.delegate.retain();
        return this;
    }

    @Override
    public boolean release() throws AlreadyReleasedException {
        return this.delegate.release();
    }

    @Override
    public IBlockStorage clone() {
        return new ToGlobalBlockStorageView(this.delegate.clone());
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return BlockRegistry.global().getState(this.getBlockRuntimeId(x, y, z));
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return BlockRegistry.global().getState(this.getBlockRuntimeId(x, y, z)).id();
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return BlockRegistry.global().getState(this.getBlockRuntimeId(x, y, z)).legacyId();
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return BlockRegistry.global().getState(this.getBlockRuntimeId(x, y, z)).meta();
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return this.converter.toGlobal(this.delegate.getBlockRuntimeId(x, y, z));
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        this.setBlockRuntimeId(x, y, z, state.runtimeId());
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.setBlockRuntimeId(x, y, z, BlockRegistry.global().getRuntimeId(id, meta));
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.setBlockRuntimeId(x, y, z, BlockRegistry.global().getRuntimeId(legacyId, meta));
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        this.setBlockRuntimeId(x, y, z, BlockRegistry.global().getDefaultState(id).runtimeId());
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.setBlockRuntimeId(x, y, z, BlockRegistry.global().getDefaultState(legacyId).runtimeId());
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.setBlockRuntimeId(x, y, z, this.getBlockState(x, y, z).withMeta(meta).runtimeId());
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        this.delegate.setBlockRuntimeId(x, y, z, this.converter.fromGlobal(runtimeId));
    }
}
