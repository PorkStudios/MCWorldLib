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

package net.daporkchop.mcworldlib.format.java.decoder.section;

import lombok.NonNull;
import net.daporkchop.lib.binary.bit.padded.PaddedBitArray;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.LongArrayTag;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.format.common.storage.flattened.HeapPackedFlattenedBlockStorage;
import net.daporkchop.mcworldlib.util.palette.Palette;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;

/**
 * @author DaPorkchop_
 */
public class PaddedFlattenedSectionDecoder extends PackedFlattenedSectionDecoder {
    public static final JavaVersion VERSION = JavaVersion.latest();

    @Override
    protected FlattenedBlockStorage parseBlockStorage(@NonNull CompoundTag tag, @NonNull BlockRegistry registry) {
        ListTag<CompoundTag> paletteTag = tag.getList("Palette", CompoundTag.class);
        LongArrayTag blockStatesTag = tag.getTag("BlockStates");

        int bits = Math.max(BinMath.getNumBitsNeededFor(paletteTag.size()), 4);
        Palette palette = this.parseBlockPalette(bits, paletteTag, registry);

        if (blockStatesTag.handle() != null) {
            return new HeapPackedFlattenedBlockStorage(new PaddedBitArray(bits, 4096, blockStatesTag.handle().retain()), palette);
        } else {
            return new HeapPackedFlattenedBlockStorage(new PaddedBitArray(bits, 4096, blockStatesTag.value()), palette);
        }
    }
}
