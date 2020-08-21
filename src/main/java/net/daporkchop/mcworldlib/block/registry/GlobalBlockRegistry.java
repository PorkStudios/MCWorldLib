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

package net.daporkchop.mcworldlib.block.registry;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.lib.primitive.map.open.ObjObjOpenHashMap;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.Blocks;
import net.daporkchop.mcworldlib.block.trait.Trait;
import net.daporkchop.mcworldlib.block.common.DefaultBlockType;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.stream.IntStream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * The global block registry.
 *
 * @author DaPorkchop_
 */
public final class GlobalBlockRegistry implements BlockRegistry {
    public static final GlobalBlockRegistry INSTANCE;

    static {
        INSTANCE = new GlobalBlockRegistry();
        PUnsafe.ensureClassInitialized(Blocks.class);
    }

    protected final Lock readLock;
    protected final Lock writeLock;

    @Getter
    protected final BlockState air;

    protected final Map<Identifier, BlockType> idToType = new ObjObjOpenHashMap<>();
    protected final IntObjMap<BlockState> runtimeToState = new IntObjOpenHashMap<>();

    protected int runtimeId = -1;
    protected final IntSupplier runtimeIdAllocator = () -> ++this.runtimeId;
    protected final Consumer<BlockState> blockStateCallback = state -> this.runtimeToState.put(state.runtimeId(), state);

    private GlobalBlockRegistry() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();

        this.air = this.register("air").defaultState();
    }

    /**
     * Convenience overload for {@link #register(Identifier, Trait[])}.
     *
     * @see #register(Identifier, Trait[])
     */
    public BlockType register(@NonNull String id, @NonNull Trait<?>... traits) {
        return this.register(Identifier.fromString(id), traits);
    }

    public BlockType register(@NonNull Identifier id, @NonNull Trait<?>... traits) {
        this.writeLock.lock();
        try {
            checkArg(!this.idToType.containsKey(id), "registry already contains block type with ID: %s", id);

            BlockType type = new DefaultBlockType(this.runtimeIdAllocator, this.blockStateCallback, id, traits);
            this.idToType.put(id, type);

            return type;
        } finally {
            this.writeLock.unlock();
        }
    }

    public int types() {
        this.readLock.lock();
        try {
            return this.idToType.size();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int states() {
        return this.maxRuntimeId();
    }

    @Override
    public int maxRuntimeId() {
        this.readLock.lock();
        try {
            return this.runtimeId;
        } finally {
            this.readLock.unlock();
        }
    }

    public boolean containsType(@NonNull Identifier typeId) {
        this.readLock.lock();
        try {
            return this.idToType.containsKey(typeId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsState(int runtimeId) {
        this.readLock.lock();
        try {
            return this.runtimeToState.containsKey(runtimeId);
        } finally {
            this.readLock.unlock();
        }
    }

    public BlockType getType(@NonNull Identifier typeId) {
        this.readLock.lock();
        try {
            BlockType type = this.idToType.get(typeId);
            checkArg(type != null, "unknown block type ID: %d", typeId);
            return type;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getState(int runtimeId) {
        this.readLock.lock();
        try {
            BlockState state = this.runtimeToState.get(runtimeId);
            checkArg(state != null, "unknown runtime ID: %d", runtimeId);
            return state;
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachState(@NonNull Consumer<? super BlockState> action) {
        this.readLock.lock();
        try {
            IntStream.rangeClosed(0, this.runtimeId).mapToObj(this.runtimeToState::get).forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachRuntimeId(@NonNull IntConsumer action) {
        this.readLock.lock();
        try {
            IntStream.rangeClosed(0, this.runtimeId).forEach(action);
        } finally {
            this.readLock.unlock();
        }
    }
}
