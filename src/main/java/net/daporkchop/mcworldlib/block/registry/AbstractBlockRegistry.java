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

import lombok.NonNull;
import net.daporkchop.lib.primitive.lambda.ObjIntConsumer;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractBlockRegistry implements BlockRegistry {
    protected final IntObjMap<BlockState> runtimeToState = new IntObjOpenHashMap<>();
    protected final ObjIntMap<Identifier> idToLegacy = new ObjIntOpenHashMap<>();
    protected final IntObjMap<Identifier> legacyToId = new IntObjOpenHashMap<>();
    protected final IntObjMap<BlockState> legacyToDefault = new IntObjOpenHashMap<>();

    @Override
    public int blocks() {
        return 0;
    }

    @Override
    public int states() {
        return this.runtimeToState.size();
    }

    @Override
    public boolean containsBlockId(@NonNull Identifier blockId) {
        return this.idToLegacy.containsKey(blockId);
    }

    @Override
    public boolean containsLegacyId(int legacyId) {
        return this.runtimeToState.containsKey(legacyId);
    }

    @Override
    public boolean containsState(@NonNull Identifier blockId, int meta) {
        return false; //TODO
    }

    @Override
    public boolean containsState(int legacyId, int meta) {
        return false; //TODO
    }

    @Override
    public boolean containsState(int runtimeId) {
        return this.runtimeToState.containsKey(runtimeId);
    }

    @Override
    public boolean hasLegacyId(@NonNull Identifier blockId) {
        return this.idToLegacy.containsKey(blockId);
    }

    @Override
    public int getLegacyId(@NonNull Identifier blockId) {
        int legacyId = this.idToLegacy.getOrDefault(blockId, -1);
        checkArg(legacyId >= 0, "unknown block ID: %s", blockId);
        return legacyId;
    }

    @Override
    public Identifier getBlockId(int legacyId) {
        Identifier blockId = this.legacyToId.get(legacyId);
        checkArg(blockId != null, "unknown legacy ID: %d", legacyId);
        return blockId;
    }

    @Override
    public BlockState getState(@NonNull Identifier blockId, int meta) {
        return null; //TODO
    }

    @Override
    public BlockState getState(int legacyId, int meta) {
        return null; //TODO
    }

    @Override
    public BlockState getDefaultState(@NonNull Identifier blockId) {
        return this.getDefaultState(this.getLegacyId(blockId));
    }

    @Override
    public BlockState getDefaultState(int legacyId) {
        BlockState state = this.legacyToDefault.get(legacyId);
        checkArg(state != null, "unknown legacy ID: %d", legacyId);
        return state;
    }

    @Override
    public BlockState getState(int runtimeId) {
        BlockState state = this.runtimeToState.get(runtimeId);
        checkArg(state != null, "unknown runtime ID: %d", runtimeId);
        return state;
    }

    @Override
    public int getRuntimeId(@NonNull Identifier blockId, int meta) {
        return 0; //TODO
    }

    @Override
    public int getRuntimeId(int legacyId, int meta) {
        return 0; //TODO
    }

    @Override
    public void forEachBlockId(@NonNull Consumer<? super Identifier> action) {
        this.idToLegacy.keySet().forEach(action);
    }

    @Override
    public void forEachLegacyId(@NonNull IntConsumer action) {
        this.idToLegacy.values().forEach(action);
    }

    @Override
    public void forEachBlockId(@NonNull ObjIntConsumer<? super Identifier> action) {
        this.idToLegacy.forEach(action);
    }

    @Override
    public void forEachState(@NonNull Consumer<? super BlockState> action) {
        this.runtimeToState.values().forEach(action);
    }

    @Override
    public void forEachRuntimeId(@NonNull IntConsumer action) {
        this.runtimeToState.keySet().forEach(action);
    }
}
