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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.block.BlockAccess;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.storage.BlockStorage;
import net.daporkchop.mcworldlib.tileentity.TileEntity;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.world.Section;

import java.util.Collection;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default implementation of {@link Section}, as a combination of {@link BlockAccess} and multiple {@link NibbleArray}s for block and sky light.
 *
 * @author DaPorkchop_
 */
@Getter
public abstract class DefaultSection extends AbstractRefCounted implements Section {
    @Getter(AccessLevel.NONE)
    protected final BlockStorage blocks;
    @Getter(AccessLevel.NONE)
    protected final NibbleArray blockLight;
    @Getter(AccessLevel.NONE)
    protected final NibbleArray skyLight;

    protected final IntObjMap<TileEntity> tileEntities = new IntObjOpenHashMap<>();

    protected final int x;
    protected final int y;
    protected final int z;

    public DefaultSection(int x, int y, int z, @NonNull BlockStorage blocks, @NonNull NibbleArray blockLight, NibbleArray skyLight) {
        this.blocks = blocks;
        this.blockLight = blockLight;
        this.skyLight = skyLight;

        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public Section retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        this.blocks.release();
        this.blockLight.release();
        if (this.skyLight != null) {
            this.skyLight.release();
        }
    }

    @Override
    public <T extends TileEntity> T getTileEntity(int x, int y, int z) {
        BlockStorage.checkCoords(x, y, z);
        return uncheckedCast(this.tileEntities.get((x << 8) | (y << 4) | z));
    }

    @Override
    public void setTileEntity(int x, int y, int z, TileEntity tileEntity) {
        BlockStorage.checkCoords(x, y, z);
        if (tileEntity == null) {
            this.tileEntities.remove((x << 8) | (y << 4) | z);
        } else {
            this.tileEntities.put((x << 8) | (y << 4) | z, tileEntity);
        }
    }

    @Override
    public Collection<TileEntity> tileEntities() {
        return this.tileEntities.values();
    }

    @Override
    public BlockStorage blockStorage() {
        return this.blocks;
    }

    @Override
    public NibbleArray blockLightStorage() {
        return this.blockLight;
    }

    //
    //
    // blockaccess methods
    //
    //

    @Override
    public abstract int layers();

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.blocks.getBlockState(x, y, z);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        BlockStorage storage = this.blockStorage(layer);
        return storage != null ? storage.getBlockState(x, y, z) : this.blocks.localRegistry().air();
    }

    @Override
    public Identifier getBlockId(int x, int y, int z) {
        return this.blocks.getBlockId(x, y, z);
    }

    @Override
    public Identifier getBlockId(int x, int y, int z, int layer) {
        BlockStorage storage = this.blockStorage(layer);
        return storage != null ? storage.getBlockId(x, y, z) : this.blocks.localRegistry().air().id();
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.blocks.getBlockLegacyId(x, y, z);
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z, int layer) {
        BlockStorage storage = this.blockStorage(layer);
        return storage != null ? storage.getBlockLegacyId(x, y, z) : this.blocks.localRegistry().air().legacyId();
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        return this.blocks.getBlockMeta(x, y, z);
    }

    @Override
    public int getBlockMeta(int x, int y, int z, int layer) {
        BlockStorage storage = this.blockStorage(layer);
        return storage != null ? storage.getBlockMeta(x, y, z) : this.blocks.localRegistry().air().meta();
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return this.blocks.getBlockRuntimeId(x, y, z);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z, int layer) {
        BlockStorage storage = this.blockStorage(layer);
        return storage != null ? storage.getBlockRuntimeId(x, y, z) : 0;
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull BlockState state) {
        this.blocks.setBlockState(x, y, z, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull BlockState state) {
        this.blockStorage(layer).setBlockState(x, y, z, state);
    }

    @Override
    public void setBlockState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.blocks.setBlockState(x, y, z, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, @NonNull Identifier id, int meta) {
        this.blockStorage(layer).setBlockState(x, y, z, id, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        this.blocks.setBlockState(x, y, z, legacyId, meta);
    }

    @Override
    public void setBlockState(int x, int y, int z, int layer, int legacyId, int meta) {
        this.blockStorage(layer).setBlockState(x, y, z, legacyId, meta);
    }

    @Override
    public void setBlockId(int x, int y, int z, @NonNull Identifier id) {
        this.blocks.setBlockId(x, y, z, id);
    }

    @Override
    public void setBlockId(int x, int y, int z, int layer, @NonNull Identifier id) {
        this.blockStorage(layer).setBlockId(x, y, z, id);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        this.blocks.setBlockLegacyId(x, y, z, legacyId);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int layer, int legacyId) {
        this.blockStorage(layer).setBlockLegacyId(x, y, z, legacyId);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        this.blocks.setBlockMeta(x, y, z, meta);
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int layer, int meta) {
        this.blockStorage(layer).setBlockMeta(x, y, z, meta);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        this.blocks.setBlockRuntimeId(x, y, z, runtimeId);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int layer, int runtimeId) {
        this.blockStorage(layer).setBlockRuntimeId(x, y, z, runtimeId);
    }

    @Override
    public int getBlockLight(int x, int y, int z) {
        return this.blockLight.get(x, y, z);
    }

    @Override
    public void setBlockLight(int x, int y, int z, int level) {
        this.blockLight.set(x, y, z, level);
    }

    @Override
    public boolean hasSkyLight() {
        return this.skyLight != null;
    }

    @Override
    public NibbleArray skyLightStorage() {
        if (this.skyLight != null) {
            return this.skyLight;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getSkyLight(int x, int y, int z) {
        return this.skyLight != null ? this.skyLight.get(x, y, z) : 0;
    }

    @Override
    public void setSkyLight(int x, int y, int z, int level) {
        if (this.skyLight != null) {
            this.skyLight.set(x, y, z, level);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    //
    //
    // fluidaccess methods
    //
    //

    @Override
    public BlockState getFluidState(int x, int y, int z) {
        return this.getBlockState(x, y, z, 1);
    }

    @Override
    public Identifier getFluidId(int x, int y, int z) {
        return this.getBlockId(x, y, z, 1);
    }

    @Override
    public int getFluidLegacyId(int x, int y, int z) {
        return this.getBlockLegacyId(x, y, z, 1);
    }

    @Override
    public int getFluidMeta(int x, int y, int z) {
        return this.getBlockMeta(x, y, z, 1);
    }

    @Override
    public int getFluidRuntimeId(int x, int y, int z) {
        return this.getBlockRuntimeId(x, y, z, 1);
    }

    @Override
    public void setFluidEmpty(int x, int y, int z) {
        this.setBlockRuntimeId(x, y, z, 1, 0);
    }

    @Override
    public void setFluidState(int x, int y, int z, @NonNull BlockState state) {
        this.setBlockState(x, y, z, 1, state);
    }

    @Override
    public void setFluidState(int x, int y, int z, @NonNull Identifier id, int meta) {
        this.setBlockState(x, y, z, 1, id, meta);
    }

    @Override
    public void setFluidState(int x, int y, int z, int legacyId, int meta) {
        this.setBlockState(x, y, z, 1, legacyId, meta);
    }

    @Override
    public void setFluidId(int x, int y, int z, @NonNull Identifier id) {
        this.setBlockId(x, y, z, 1, id);
    }

    @Override
    public void setFluidLegacyId(int x, int y, int z, int legacyId) {
        this.setBlockLegacyId(x, y, z, 1, legacyId);
    }

    @Override
    public void setFluidMeta(int x, int y, int z, int meta) {
        this.setBlockMeta(x, y, z, 1, meta);
    }

    @Override
    public void setFluidRuntimeId(int x, int y, int z, int runtimeId) {
        this.setBlockRuntimeId(x, y, z, 1, runtimeId);
    }
}
