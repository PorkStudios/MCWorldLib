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
import net.daporkchop.lib.primitive.lambda.ObjIntConsumer;
import net.daporkchop.lib.primitive.map.open.ObjObjOpenHashMap;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.Trait;
import net.daporkchop.mcworldlib.block.common.DefaultBlockType;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * The global block registry.
 *
 * @author DaPorkchop_
 */
public final class GlobalBlockRegistry extends AbstractBlockRegistry {
    public static final GlobalBlockRegistry INSTANCE = new GlobalBlockRegistry();

    protected final Lock readLock;
    protected final Lock writeLock;

    @Getter
    protected final BlockState air;

    protected final Map<Identifier, BlockType> idToType = new ObjObjOpenHashMap<>();

    protected int runtimeId = -1;
    protected final IntSupplier runtimeIdAllocator = () -> ++this.runtimeId;

    private GlobalBlockRegistry() {
        ReadWriteLock lock = new ReentrantReadWriteLock();
        this.readLock = lock.readLock();
        this.writeLock = lock.writeLock();

        this.air = this.getState(0);
    }

    public BlockType register(@NonNull Identifier id, @NonNull Trait<?>... traits) {
        this.writeLock.lock();
        try {
            checkArg(!this.idToType.containsKey(id), "registry already contains block type with ID: %s", id);

            BlockType type = new DefaultBlockType(this.runtimeIdAllocator, id, traits);
            this.idToType.put(id, type);

            return type;
        } finally {
            this.writeLock.unlock();
        }
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

    //acquire read lock on all of these things

    @Override
    public int blocks() {
        this.readLock.lock();
        try {
            return super.blocks();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int states() {
        this.readLock.lock();
        try {
            return super.states();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsBlockId(@NonNull Identifier blockId) {
        this.readLock.lock();
        try {
            return super.containsBlockId(blockId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsLegacyId(int legacyId) {
        this.readLock.lock();
        try {
            return super.containsLegacyId(legacyId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsState(@NonNull Identifier blockId, int meta) {
        this.readLock.lock();
        try {
            return super.containsState(blockId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsState(int legacyId, int meta) {
        this.readLock.lock();
        try {
            return super.containsState(legacyId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsState(int runtimeId) {
        this.readLock.lock();
        try {
            return super.containsState(runtimeId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean hasLegacyId(@NonNull Identifier blockId) {
        this.readLock.lock();
        try {
            return super.hasLegacyId(blockId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int getLegacyId(@NonNull Identifier blockId) {
        this.readLock.lock();
        try {
            return super.getLegacyId(blockId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Identifier getBlockId(int legacyId) {
        this.readLock.lock();
        try {
            return super.getBlockId(legacyId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getState(@NonNull Identifier blockId, int meta) {
        this.readLock.lock();
        try {
            return super.getState(blockId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getState(int legacyId, int meta) {
        this.readLock.lock();
        try {
            return super.getState(legacyId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getDefaultState(@NonNull Identifier blockId) {
        this.readLock.lock();
        try {
            return super.getDefaultState(blockId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getDefaultState(int legacyId) {
        this.readLock.lock();
        try {
            return super.getDefaultState(legacyId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public BlockState getState(int runtimeId) {
        this.readLock.lock();
        try {
            return super.getState(runtimeId);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int getRuntimeId(@NonNull Identifier blockId, int meta) {
        this.readLock.lock();
        try {
            return super.getRuntimeId(blockId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public int getRuntimeId(int legacyId, int meta) {
        this.readLock.lock();
        try {
            return super.getRuntimeId(legacyId, meta);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachBlockId(@NonNull Consumer<? super Identifier> action) {
        this.readLock.lock();
        try {
            super.forEachBlockId(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachLegacyId(@NonNull IntConsumer action) {
        this.readLock.lock();
        try {
            super.forEachLegacyId(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.readLock.lock();
        try {
            super.forEachBlockId(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachState(@NonNull Consumer<? super BlockState> action) {
        this.readLock.lock();
        try {
            super.forEachState(action);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void forEachRuntimeId(@NonNull IntConsumer action) {
        this.readLock.lock();
        try {
            super.forEachRuntimeId(action);
        } finally {
            this.readLock.unlock();
        }
    }
}
