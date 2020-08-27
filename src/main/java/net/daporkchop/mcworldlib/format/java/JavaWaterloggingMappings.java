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

package net.daporkchop.mcworldlib.format.java;

import lombok.NonNull;
import net.daporkchop.lib.primitive.map.IntIntMap;
import net.daporkchop.lib.primitive.map.concurrent.ObjObjConcurrentHashMap;
import net.daporkchop.lib.primitive.map.open.IntIntOpenHashMap;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.block.Property;

import java.util.Map;
import java.util.function.Function;

/**
 * Maps waterlogged runtime IDs to their non-waterlogged equivalents.
 *
 * @author DaPorkchop_
 */
public final class JavaWaterloggingMappings {
    protected static final Map<BlockRegistry, JavaWaterloggingMappings> LOOKUP = new ObjObjConcurrentHashMap<>();
    protected static final Function<BlockRegistry, JavaWaterloggingMappings> COMPUTE_FUNCTION = JavaWaterloggingMappings::new;

    /**
     * Gets the {@link JavaWaterloggingMappings} for the given {@link BlockRegistry}.
     *
     * @param registry the {@link BlockRegistry}
     * @return the {@link JavaWaterloggingMappings} for the given {@link BlockRegistry}
     */
    public static JavaWaterloggingMappings forRegistry(@NonNull BlockRegistry registry) {
        return LOOKUP.computeIfAbsent(registry, COMPUTE_FUNCTION);
    }

    protected final IntIntMap drain = new IntIntOpenHashMap();
    protected final IntIntMap fill = new IntIntOpenHashMap();

    private JavaWaterloggingMappings(@NonNull BlockRegistry registry) {
        registry.forEachState(state -> {
            Property.Boolean prop = state.tryPropertyBoolean("waterlogged");
            if (prop != null) {
                int drained = state.withProperty(prop, false).runtimeId();
                int filled = state.withProperty(prop, true).runtimeId();
                this.drain.put(filled, drained);
                this.fill.put(drained, filled);
            }
        });
    }

    /**
     * Gets the equivalent, non-waterlogged block state.
     *
     * @param runtimeId the block state
     * @return the runtime ID of the non-waterlogged block state, or {@code -1} if the given state isn't waterlogged
     */
    public int drain(int runtimeId) {
        return this.drain.getOrDefault(runtimeId, -1);
    }

    /**
     * Gets the equivalent, waterlogged block state.
     *
     * @param runtimeId the block state
     * @return the runtime ID of the waterlogged block state, or {@code -1} if the given state doesn't have a waterlogged equivalent
     */
    public int fill(int runtimeId) {
        return this.fill.getOrDefault(runtimeId, -1);
    }
}
