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

package net.daporkchop.mcworldlib.util.nbt;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.lib.nbt.util.NBTObjectParser;
import net.daporkchop.mcworldlib.save.SaveOptions;

import java.io.IOException;

import static net.daporkchop.lib.nbt.tag.Tag.*;

/**
 * Implementation of {@link NBTObjectParser} which uses {@link ArrayAllocator}s for allocating {@code byte[]}s, {@code int[]}s and {@code long[]s}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public class AllocatingNBTObjectParser implements NBTObjectParser {
    @NonNull
    protected final NBTObjectParser delegate;

    protected final ArrayAllocator<byte[]> byteAlloc;
    protected final ArrayAllocator<int[]> intAlloc;
    protected final ArrayAllocator<long[]> longAlloc;

    public AllocatingNBTObjectParser(@NonNull NBTObjectParser delegate, @NonNull SaveOptions options) {
        this(delegate, options.get(SaveOptions.BYTE_ALLOC), options.get(SaveOptions.INT_ALLOC), options.get(SaveOptions.LONG_ALLOC));
    }

    @Override
    public Tag read(@NonNull DataIn in, @NonNull NBTOptions options, int id) throws IOException {
        switch (id) {
            case TAG_ARRAY_BYTE:
                return new AllocatedByteArrayTag(in, this.byteAlloc);
            case TAG_ARRAY_INT:
                return new AllocatedIntArrayTag(in, this.intAlloc);
            case TAG_ARRAY_LONG:
                return new AllocatedLongArrayTag(in, this.longAlloc);
        }
        return this.delegate.read(in, options, id);
    }
}
