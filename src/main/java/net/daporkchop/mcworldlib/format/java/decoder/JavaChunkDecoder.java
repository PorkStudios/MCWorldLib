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

package net.daporkchop.mcworldlib.format.java.decoder;

import lombok.NonNull;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.Chunk;
import net.daporkchop.mcworldlib.world.common.IWorld;
import net.daporkchop.lib.nbt.tag.CompoundTag;

/**
 * A function for decoding a chunk from NBT data.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface JavaChunkDecoder {
    /**
     * Decodes a chunk.
     *
     * @param tag     the {@link CompoundTag} containing the chunk data
     * @param version the version of the chunk data
     * @param world   the {@link IWorld} that the chunk is in
     * @return the decoded chunk
     */
    Chunk decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull IWorld world);
}
