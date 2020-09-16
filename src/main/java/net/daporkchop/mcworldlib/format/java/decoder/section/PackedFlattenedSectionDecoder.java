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
import net.daporkchop.lib.binary.bit.packed.PackedBitArray;
import net.daporkchop.lib.binary.bit.padded.PaddedBitArray;
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.nbt.tag.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.LongArrayTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.format.common.nibble.HeapNibbleArray;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.section.flattened.SingleLayerFlattenedSection;
import net.daporkchop.mcworldlib.format.common.storage.flattened.HeapPackedFlattenedBlockStorage;
import net.daporkchop.mcworldlib.format.java.decoder.JavaSectionDecoder;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.util.palette.ArrayPalette;
import net.daporkchop.mcworldlib.util.palette.Palette;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.World;
import net.daporkchop.mcworldlib.world.section.Section;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;

import java.util.Map;

/**
 * @author DaPorkchop_
 */
public class PackedFlattenedSectionDecoder implements JavaSectionDecoder {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.15.2");

    @Override
    public Section decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world, int x, int z) {
        int y = tag.getByte("Y") & 0xFF;
        FlattenedBlockStorage blocks = this.parseBlockStorage(tag, world.parent().blockRegistryFor(version));

        NibbleArray blockLight = this.parseNibbleArray(tag, "BlockLight");
        NibbleArray skyLight = this.parseNibbleArray(tag, "SkyLight");
        return new SingleLayerFlattenedSection(x, y, z, blocks, blockLight, skyLight);
    }

    protected FlattenedBlockStorage parseBlockStorage(@NonNull CompoundTag tag, @NonNull BlockRegistry registry) {
        ListTag<CompoundTag> paletteTag = tag.getList("Palette", CompoundTag.class);
        LongArrayTag blockStatesTag = tag.getTag("BlockStates");

        int bits = Math.max(BinMath.getNumBitsNeededFor(paletteTag.size()), 4);
        Palette palette = this.parseBlockPalette(bits, paletteTag, registry);

        if (blockStatesTag.handle() != null) {
            return new HeapPackedFlattenedBlockStorage(new PackedBitArray(bits, 4096, blockStatesTag.handle().retain()), palette);
        } else {
            return new HeapPackedFlattenedBlockStorage(new PackedBitArray(bits, 4096, blockStatesTag.value()), palette);
        }
    }

    protected Palette parseBlockPalette(int bits, @NonNull ListTag<CompoundTag> paletteTag, @NonNull BlockRegistry registry) {
        Palette palette = new ArrayPalette(bits);
        for (CompoundTag tag : paletteTag) {
            BlockState state = registry.getDefaultState(Identifier.fromString(tag.getString("Name")));

            CompoundTag properties = tag.getCompound("Properties", null);
            if (properties != null) {
                //set properties
                for (Map.Entry<String, Tag> entry : properties) {
                    state = state.withProperty(entry.getKey(), ((StringTag) entry.getValue()).value());
                }
            }

            palette.get(state.runtimeId());
        }
        return palette;
    }

    protected NibbleArray parseNibbleArray(@NonNull CompoundTag tag, @NonNull String name) {
        ByteArrayTag data = tag.getTag(name, null);
        if (data == null) {
            return null;
        }
        return data.handle() != null
                ? new HeapNibbleArray.YZX(data.handle())
                : new HeapNibbleArray.YZX(data.value(), 0);
    }
}
