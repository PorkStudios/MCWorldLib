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

import lombok.NonNull;
import net.daporkchop.lib.binary.bit.BitArray;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.format.common.storage.AbstractBlockStorage;
import net.daporkchop.mcworldlib.util.palette.state.ArrayStatePalette;
import net.daporkchop.mcworldlib.util.palette.state.StatePalette;
import net.daporkchop.mcworldlib.world.storage.BlockStorage;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractHeapFlattenedBlockStorage extends AbstractBlockStorage implements FlattenedBlockStorage, FlattenedBlockStorage.Internal {
    protected static int index(int x, int y, int z) {
        BlockStorage.checkCoords(x, y, z);
        return (y << 8) | (z << 4) | x;
    }

    protected final ArrayAllocator<long[]> alloc;
    protected BitArray array;
    protected StatePalette palette;
    protected int bits;

    public AbstractHeapFlattenedBlockStorage() {
        this(null);
    }

    public AbstractHeapFlattenedBlockStorage(ArrayAllocator<long[]> alloc) {
        this.alloc = alloc;
        this.setBits(4);
    }

    public AbstractHeapFlattenedBlockStorage(@NonNull BitArray array, @NonNull StatePalette palette) {
        this(null, array, palette);
    }

    public AbstractHeapFlattenedBlockStorage(ArrayAllocator<long[]> alloc, @NonNull BitArray array, @NonNull StatePalette palette) {
        this.alloc = alloc;
        checkArg(array.size() >= 4096, "array (%d) must be at least 4096 entries!", array.size());
        this.array = array;
        this.palette = palette;
    }

    protected void setBits(int bits) {
        if (bits == this.bits) {
            return;
        }

        if (bits <= 4) {
            this.palette = new ArrayStatePalette(1 << (this.bits = 4));
        } else if (bits < 9) {
            this.palette = new ArrayStatePalette(1 << (this.bits = bits)); //vanilla uses a hashmap for this, i doubt it's much faster though...
        } else {
            //this.bits = BinMath.getNumBitsNeededFor(this.localRegistry.maxRuntimeId() + 1);
            this.bits = 12; //TODO: get the number of required bits from somewhere
            //this.palette = IdentityPalette.INSTANCE;
            throw new UnsupportedOperationException("identity palette");
        }

        this.array = this.createArray();
    }

    @Override
    public BlockState getBlockState(int x, int y, int z) {
        return this.palette.idToState(this.array.get(index(x, y, z)));
    }

    @Override
    public void setBlockState(int x, int y, int z, BlockState state) {
        int i = index(x, y, z);

        int paletteId = this.palette.stateToId(state);
        if (paletteId < 0) {
            this.growPalette();
            paletteId = this.palette.stateToId(state);
        }

        this.array.set(i, paletteId);
    }

    private void growPalette() {
        StatePalette oldPalette = this.palette;
        BitArray oldArray = this.array;

        this.setBits(this.bits + 1);

        for (int i = 0; i < 4096; i++) {
            this.array.set(i, this.palette.stateToId(oldPalette.idToState(oldArray.get(i))));
        }

        oldArray.release();
    }

    protected abstract BitArray createArray();

    @Override
    public abstract FlattenedBlockStorage clone();

    @Override
    public FlattenedBlockStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        if (this.array != null) {
            this.array.release();
        }
    }

    @Override
    public Internal internal() {
        return this;
    }

    @Override
    public StatePalette palette() {
        return this.palette;
    }

    @Override
    public BitArray data() {
        return this.array;
    }
}
