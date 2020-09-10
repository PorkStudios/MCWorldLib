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
import net.daporkchop.lib.binary.bit.packed.PackedBitArray;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.world.storage.BlockStorage;
import net.daporkchop.mcworldlib.format.common.storage.legacy.AbstractLegacyBlockStorage;
import net.daporkchop.mcworldlib.util.palette.ArrayPalette;
import net.daporkchop.mcworldlib.util.palette.IdentityPalette;
import net.daporkchop.mcworldlib.util.palette.Palette;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;

import java.util.Arrays;
import java.util.function.IntBinaryOperator;

import static java.lang.Math.*;
import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implementation of {@link BlockStorage} which uses a palette.
 *
 * @author DaPorkchop_
 */
public class HeapPackedFlattenedBlockStorage extends AbstractFlattenedBlockStorage implements IntBinaryOperator {
    protected final ArrayAllocator<long[]> alloc;
    protected BitArray array;
    protected Palette palette;
    protected int bits;

    public HeapPackedFlattenedBlockStorage() {
        this(null);
    }

    public HeapPackedFlattenedBlockStorage(ArrayAllocator<long[]> alloc) {
        this.alloc = alloc;
        this.setBits(4);
    }

    public HeapPackedFlattenedBlockStorage(@NonNull BitArray array, @NonNull Palette palette) {
        this(null, array, palette);
    }

    public HeapPackedFlattenedBlockStorage(ArrayAllocator<long[]> alloc, @NonNull BitArray array, @NonNull Palette palette) {
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
            this.palette = new ArrayPalette(this, this.bits = 4);
        } else if (bits < 9) {
            this.palette = new ArrayPalette(this, this.bits = bits); //vanilla uses a hashmap for this, i doubt it's much faster though...
        } else {
            //this.bits = BinMath.getNumBitsNeededFor(this.localRegistry.maxRuntimeId() + 1);
            this.bits = 12; //TODO: get the number of required bits from somewhere
            this.palette = IdentityPalette.INSTANCE;
        }

        this.palette.get(0);
        this.array = this.alloc != null
                     ? new PackedBitArray(this.bits, 4096, this.alloc.atLeast(4096))
                     : new PackedBitArray(this.bits, 4096);
    }

    @Override
    public int getBlockRuntimeId(int x, int y, int z) {
        return max(this.palette.getReverse(this.array.get(index(x, y, z))), 0);
    }

    @Override
    public void setBlockRuntimeId(int x, int y, int z, int runtimeId) {
        int paletteId = this.palette.get(runtimeId);
        this.array.set(index(x, y, z), paletteId); //this prevents this.array from being loaded first, in case it gets modified
    }

    /**
     * resizes this block storage
     * <p>
     * internal API, do not touch!
     */
    @Override
    @Deprecated
    public int applyAsInt(int nextBits, int nextValue) {
        BitArray array = this.array;
        Palette palette = this.palette;
        this.setBits(nextBits);

        //copy values
        for (int i = 0; i < 4096; i++) {
            int paletteId = this.palette.get(max(palette.get(array.get(i)), 0));
            this.array.set(i, paletteId); //this prevents this.array from being loaded first, in case it gets modified
        }
        array.release();

        return this.palette.get(nextValue);
    }

    @Override
    public FlattenedBlockStorage clone() {
        return null; //TODO
    }

    @Override
    protected void doRelease() {
        if (this.array != null) {
            this.array.release();
        }
    }
}
