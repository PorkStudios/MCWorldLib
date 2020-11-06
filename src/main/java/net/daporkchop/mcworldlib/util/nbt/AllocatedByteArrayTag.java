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

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.binary.stream.DataOut;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.nbt.tag.ByteArrayTag;
import net.daporkchop.lib.nbt.tag.Tag;

import java.io.IOException;
import java.util.Arrays;

/**
 * @author DaPorkchop_
 */
@Getter
public class AllocatedByteArrayTag extends Tag<AllocatedByteArrayTag> implements AllocatedTag {
    protected final byte[] value;
    protected final int length;

    protected final ArrayAllocator<byte[]> alloc;

    public AllocatedByteArrayTag(@NonNull DataIn in, ArrayAllocator<byte[]> alloc) throws IOException {
        this.length = in.readInt();

        if ((this.alloc = alloc) != null) {
            this.value = alloc.atLeast(this.length);
        } else {
            this.value = new byte[this.length];
        }

        in.readFully(this.value, 0, this.length);
    }

    @Override
    public void write(@NonNull DataOut out) throws IOException {
        out.writeInt(this.length);
        out.write(this.value, 0, this.length);
    }

    @Override
    public String typeName() {
        return "Byte_Array";
    }

    @Override
    public void release() {
        if (this.alloc != null) {
            this.alloc.release(this.value);
        }
    }

    @Override
    public Tag<?> toNormalAndRelease() {
        byte[] clonedArray;
        if (this.alloc != null) {
            clonedArray = Arrays.copyOf(this.value, this.length);
            this.alloc.release(this.value);
        } else {
            clonedArray = this.value;
        }
        return new ByteArrayTag(clonedArray);
    }

    @Override
    public int id() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AllocatedByteArrayTag clone() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void toString(StringBuilder builder, int depth, String name, int index) {
        super.toString(builder, depth, name, index);
        builder.append('[').append(this.length).append(" bytes]\n");
    }
}
