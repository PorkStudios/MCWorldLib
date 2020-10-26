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
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.block.access.BlockAccess;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.version.MinecraftVersion;
import net.daporkchop.mcworldlib.world.section.Section;
import net.daporkchop.mcworldlib.world.storage.BlockStorage;

import java.util.Collection;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default implementation of {@link Section}, as a combination of {@link BlockAccess} and multiple {@link NibbleArray}s for block and sky light.
 *
 * @author DaPorkchop_
 */
@Getter
public abstract class AbstractSection extends AbstractRefCounted implements Section {
    @Getter(AccessLevel.NONE)
    protected final NibbleArray blockLight;
    @Getter(AccessLevel.NONE)
    protected final NibbleArray skyLight;

    protected final IntObjMap<CompoundTag> tileEntities = new IntObjOpenHashMap<>();

    protected final MinecraftVersion version;
    protected final int x;
    protected final int y;
    protected final int z;

    public AbstractSection(@NonNull MinecraftVersion version, int x, int y, int z, @NonNull NibbleArray blockLight, NibbleArray skyLight) {
        this.blockLight = blockLight;
        this.skyLight = skyLight;

        this.version = version;
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
        this.blockLight.release();
        if (this.skyLight != null) {
            this.skyLight.release();
        }
    }

    @Override
    public CompoundTag getTileEntity(int x, int y, int z) {
        BlockStorage.checkCoords(x, y, z);
        return uncheckedCast(this.tileEntities.get((x << 8) | (y << 4) | z));
    }

    @Override
    public void setTileEntity(int x, int y, int z, CompoundTag tileEntity) {
        BlockStorage.checkCoords(x, y, z);
        if (tileEntity == null) {
            this.tileEntities.remove((x << 8) | (y << 4) | z);
        } else {
            this.tileEntities.put((x << 8) | (y << 4) | z, tileEntity);
        }
    }

    @Override
    public Collection<CompoundTag> tileEntities() {
        return this.tileEntities.values();
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
}
