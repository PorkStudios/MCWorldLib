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
import net.daporkchop.lib.common.math.BinMath;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.StringTag;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.format.common.nibble.HeapNibbleArray;
import net.daporkchop.mcworldlib.format.common.nibble.NibbleArray;
import net.daporkchop.mcworldlib.format.common.section.flattened.SingleLayerFlattenedSection;
import net.daporkchop.mcworldlib.format.common.storage.flattened.HeapPackedFlattenedBlockStorage;
import net.daporkchop.mcworldlib.format.java.decoder.JavaSectionDecoder;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.util.nbt.AllocatedByteArrayTag;
import net.daporkchop.mcworldlib.util.nbt.AllocatedLongArrayTag;
import net.daporkchop.mcworldlib.util.palette.state.ArrayStatePalette;
import net.daporkchop.mcworldlib.util.palette.state.StatePalette;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.World;
import net.daporkchop.mcworldlib.world.section.Section;
import net.daporkchop.mcworldlib.world.storage.FlattenedBlockStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
public class PackedFlattenedSectionDecoder implements JavaSectionDecoder {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.15.2");

    @Override
    public Section decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world, int x, int z) {
        int y = tag.getByte("Y") & 0xFF;
        FlattenedBlockStorage blocks = this.parseBlockStorage(tag);

        NibbleArray blockLight = this.parseNibbleArray(tag, "BlockLight");
        NibbleArray skyLight = this.parseNibbleArray(tag, "SkyLight");
        return new SingleLayerFlattenedSection(version, x, y, z, blocks, blockLight, skyLight);
    }

    protected FlattenedBlockStorage parseBlockStorage(@NonNull CompoundTag tag) {
        ListTag<CompoundTag> paletteTag = tag.getList("Palette", CompoundTag.class);
        AllocatedLongArrayTag blockStatesTag = tag.remove("BlockStates");

        int bits = Math.max(BinMath.getNumBitsNeededFor(Math.max(paletteTag.size() - 1, 0)), 4);
        StatePalette palette = this.parseBlockPalette(bits, paletteTag);

        return new HeapPackedFlattenedBlockStorage(new PackedBitArray(bits, 4096, blockStatesTag.value(), blockStatesTag.alloc()), palette);
    }

    protected StatePalette parseBlockPalette(int bits, @NonNull ListTag<CompoundTag> paletteTag) {
        return new ArrayStatePalette(1 << bits, paletteTag.stream().map(tag -> {
            Identifier id = Identifier.fromString(tag.getString("Name"));

            Map<String, String> propertiesMap = new HashMap<>();
            CompoundTag properties = tag.getCompound("Properties", null);
            if (properties != null) {
                //set properties
                for (Map.Entry<String, Tag> entry : properties) {
                    propertiesMap.put(entry.getKey(), ((StringTag) entry.getValue()).value());
                }
            }

            return BlockState.of(id, propertiesMap);
        }).collect(Collectors.toList()));
    }

    protected NibbleArray parseNibbleArray(@NonNull CompoundTag tag, @NonNull String name) {
        AllocatedByteArrayTag data = tag.remove(name, null);
        return data != null ? new HeapNibbleArray.YZX(data.value(), data.alloc()) : null;
    }
}
