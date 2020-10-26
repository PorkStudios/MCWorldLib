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

package net.daporkchop.mcworldlib.util.palette.state;

import lombok.NonNull;
import net.daporkchop.mcworldlib.block.BlockState;

/**
 * @author DaPorkchop_
 */
public class ArrayStatePalette implements StatePalette {
    protected final BlockState[] values;
    protected int nextId;

    public ArrayStatePalette(int capacity) {
        this.values = new BlockState[capacity];
        this.nextId = 0;
    }

    public ArrayStatePalette(int capacity, @NonNull Iterable<BlockState> initialContents) {
        this(capacity);

        for (BlockState state : initialContents) {
            this.values[this.nextId++] = state;
        }
    }

    @Override
    public int stateToId(@NonNull BlockState state) {
        for (int i = 0; i < this.nextId; i++) {
            if (this.values[i] == state) {
                return i;
            }
        }

        if (this.values.length > this.nextId) {
            int id = this.nextId++;
            this.values[id] = state;
            return id;
        }
        return -1;
    }

    @Override
    public BlockState idToState(int id) {
        if (id >= 0 && id < this.values.length) {
            return this.values[id];
        } else {
            return null;
        }
    }

    @Override
    public int size() {
        return this.nextId;
    }
}
