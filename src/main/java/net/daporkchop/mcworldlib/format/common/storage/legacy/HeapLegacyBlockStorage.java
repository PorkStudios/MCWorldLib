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

package net.daporkchop.mcworldlib.format.common.storage.legacy;

import lombok.NonNull;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.world.storage.LegacyBlockStorage;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Heap-based {@link AbstractLegacyBlockStorage} implementation.
 * <p>
 * This also stores block meta itself (without wrapping it into a {@link NibbleArray}) for performance reasons.
 *
 * @author DaPorkchop_
 */
public class HeapLegacyBlockStorage extends AbstractLegacyBlockStorage {
    protected final byte[] blocks;
    protected final byte[] meta;

    protected final ArrayAllocator<byte[]> blocksAlloc;
    protected final ArrayAllocator<byte[]> metaAlloc;

    public HeapLegacyBlockStorage() {
        this(new byte[NUM_BLOCKS], new byte[NibbleArray.PACKED_SIZE], null, null);
    }

    public HeapLegacyBlockStorage(@NonNull byte[] blocks, @NonNull byte[] meta) {
        this(blocks, meta, null, null);
    }

    public HeapLegacyBlockStorage(@NonNull byte[] blocks, @NonNull byte[] meta, ArrayAllocator<byte[]> blocksAlloc, ArrayAllocator<byte[]> metaAlloc) {
        checkRange(blocks.length, 0, NUM_BLOCKS);
        checkRange(meta.length, 0, NibbleArray.PACKED_SIZE);

        this.blocks = blocks;
        this.meta = meta;

        this.blocksAlloc = blocksAlloc;
        this.metaAlloc = metaAlloc;
    }

    @Override
    public int getBlockLegacyId(int x, int y, int z) {
        return this.blocks[index(x, y, z)] & 0xFF;
    }

    @Override
    public int getBlockMeta(int x, int y, int z) {
        int index = index(x, y, z);
        return NibbleArray.extractNibble(index, this.meta[index >> 1]);
    }

    @Override
    public int getCombinedIdMeta(int x, int y, int z) {
        int index = index(x, y, z);
        return ((this.blocks[index] & 0xFF) << 4)
               | NibbleArray.extractNibble(index, this.meta[index >> 1]);
    }

    @Override
    public void setBlockState(int x, int y, int z, int legacyId, int meta) {
        checkArg((legacyId & 0xFF) == legacyId, "legacy ID must be in range [0-256)");
        checkArg((meta & 0xF) == meta, "nibble value must be in range [0-16)");
        int index = index(x, y, z);
        this.blocks[index] = (byte) legacyId;
        this.meta[index >> 1] = (byte) NibbleArray.insertNibble(index, this.meta[index >> 1], meta);
    }

    @Override
    public void setBlockLegacyId(int x, int y, int z, int legacyId) {
        checkArg((legacyId & 0xFF) == legacyId, "legacy ID must be in range [0-256)");
        this.blocks[index(x, y, z)] = (byte) legacyId;
    }

    @Override
    public void setBlockMeta(int x, int y, int z, int meta) {
        checkArg((meta & 0xF) == meta, "nibble value must be in range [0-16)");
        int index = index(x, y, z);
        this.meta[index >> 1] = (byte) NibbleArray.insertNibble(index, this.meta[index >> 1], meta);
    }

    @Override
    public LegacyBlockStorage clone() {
        byte[] blocksClone;
        if (this.blocksAlloc != null) {
            System.arraycopy(this.blocks, 0, blocksClone = this.blocksAlloc.atLeast(NUM_BLOCKS), 0, NUM_BLOCKS);
        } else {
            blocksClone = this.blocks.clone();
        }

        byte[] metaClone;
        if (this.metaAlloc != null) {
            System.arraycopy(this.meta, 0, metaClone = this.metaAlloc.atLeast(NibbleArray.PACKED_SIZE), 0, NibbleArray.PACKED_SIZE);
        } else {
            metaClone = this.meta.clone();
        }

        return new HeapLegacyBlockStorage(blocksClone, metaClone, this.blocksAlloc, this.metaAlloc);
    }

    @Override
    protected void doRelease() {
        if (this.blocksAlloc != null) {
            this.blocksAlloc.release(this.blocks);
        }
        if (this.metaAlloc != null) {
            this.metaAlloc.release(this.meta);
        }
    }

    /**
     * Extension of {@link HeapLegacyBlockStorage} with support for Anvil's extended block IDs.
     *
     * @author DaPorkchop_
     */
    public static class Add extends HeapLegacyBlockStorage {
        protected final byte[] add;

        protected final ArrayAllocator<byte[]> addAlloc;

        public Add() {
            this(new byte[NUM_BLOCKS], new byte[NibbleArray.PACKED_SIZE], new byte[NibbleArray.PACKED_SIZE], null, null, null);
        }

        public Add(@NonNull byte[] blocks, @NonNull byte[] meta, @NonNull byte[] add) {
            this(blocks, meta, add, null, null, null);
        }

        public Add(@NonNull byte[] blocks, @NonNull byte[] meta, @NonNull byte[] add, ArrayAllocator<byte[]> blocksAlloc, ArrayAllocator<byte[]> metaAlloc, ArrayAllocator<byte[]> addAlloc) {
            super(blocks, meta, blocksAlloc, metaAlloc);

            checkRange(meta.length, 0, NibbleArray.PACKED_SIZE);
            this.add = add;
            this.addAlloc = addAlloc;
        }

        @Override
        public int getBlockLegacyId(int x, int y, int z) {
            int index = index(x, y, z);
            return (this.blocks[index] & 0xFF)
                   | (NibbleArray.extractNibble(index, this.add[index >> 1]) << 8);
        }

        @Override
        public int getCombinedIdMeta(int x, int y, int z) {
            int index = index(x, y, z);
            return ((this.blocks[index] & 0xFF) << 4)
                   | (NibbleArray.extractNibble(index, this.add[index >> 1]) << 12)
                   | NibbleArray.extractNibble(index, this.meta[index >> 1]);
        }

        @Override
        public void setBlockState(int x, int y, int z, int legacyId, int meta) {
            checkArg((legacyId & 0xFFF) == legacyId, "legacy ID must be in range [0-4096)");
            checkArg((meta & 0xF) == meta, "nibble value must be in range [0-16)");
            int index = index(x, y, z);
            this.blocks[index] = (byte) legacyId;
            this.add[index >> 1] = (byte) NibbleArray.insertNibble(index, this.add[index >> 1], (legacyId >> 8) & 0xF);
            this.meta[index >> 1] = (byte) NibbleArray.insertNibble(index, this.meta[index >> 1], meta);
        }

        @Override
        public void setBlockLegacyId(int x, int y, int z, int legacyId) {
            checkArg((legacyId & 0xFFF) == legacyId, "legacy ID must be in range [0-4096)");
            int index = index(x, y, z);
            this.blocks[index] = (byte) legacyId;
            this.add[index >> 1] = (byte) NibbleArray.insertNibble(index, this.add[index >> 1], (legacyId >> 8) & 0xF);
        }

        @Override
        public LegacyBlockStorage clone() {
            byte[] blocksClone;
            if (this.blocksAlloc != null) {
                System.arraycopy(this.blocks, 0, blocksClone = this.blocksAlloc.atLeast(NUM_BLOCKS), 0, NUM_BLOCKS);
            } else {
                blocksClone = this.blocks.clone();
            }

            byte[] metaClone;
            if (this.metaAlloc != null) {
                System.arraycopy(this.meta, 0, metaClone = this.metaAlloc.atLeast(NibbleArray.PACKED_SIZE), 0, NibbleArray.PACKED_SIZE);
            } else {
                metaClone = this.meta.clone();
            }

            byte[] addClone;
            if (this.addAlloc != null) {
                System.arraycopy(this.add, 0, addClone = this.addAlloc.atLeast(NibbleArray.PACKED_SIZE), 0, NibbleArray.PACKED_SIZE);
            } else {
                addClone = this.add.clone();
            }

            return new Add(blocksClone, metaClone, addClone, this.blocksAlloc, this.metaAlloc, this.addAlloc);
        }

        @Override
        protected void doRelease() {
            super.doRelease();
            if (this.addAlloc != null) {
                this.addAlloc.release(this.add);
            }
        }
    }
}
