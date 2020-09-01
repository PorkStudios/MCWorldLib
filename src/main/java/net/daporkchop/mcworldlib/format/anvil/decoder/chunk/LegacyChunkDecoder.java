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

package net.daporkchop.mcworldlib.format.anvil.decoder.chunk;

import lombok.NonNull;
import net.daporkchop.mcworldlib.format.java.decoder.JavaChunkDecoder;
import net.daporkchop.mcworldlib.format.vanilla.VanillaChunk;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.common.IChunk;
import net.daporkchop.mcworldlib.world.common.IWorld;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * Codec for serialization of chunks in the pre-flattening format used by Minecraft versions 1.12.2 and older.
 *
 * @author DaPorkchop_
 */
public class LegacyChunkDecoder implements JavaChunkDecoder {
    public static final JavaVersion VERSION = JavaVersion.fromName("1.12.2");

    @Override
    public IChunk decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull IWorld world) {
        CompoundTag level = tag.getCompound("Level");
        int x = level.getInt("xPos");
        int z = level.getInt("zPos");

        return new VanillaChunk(x, z);
    }
}
