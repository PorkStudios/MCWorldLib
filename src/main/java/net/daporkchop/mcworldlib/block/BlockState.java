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

package net.daporkchop.mcworldlib.block;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.Tuple;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
@Getter
public final class BlockState {
    private static final Map<Tuple<Identifier, Map<String, String>>, BlockState> VALUES = new ConcurrentHashMap<>();

    protected final Identifier id;
    protected final Map<String, Object> properties;

    private final transient int hashCode;

    private BlockState(Identifier id, Map<String, String> properties) {
        this.id = id;
        this.properties = Collections.unmodifiableMap(properties);

        this.hashCode = mix32(properties.entrySet().stream()
                .mapToLong(entry -> mix64(entry.getKey().hashCode()) + entry.getValue().hashCode())
                .reduce(id.hashCode(), (a, b) -> mix64(a + b)));
    }

    private BlockState(Identifier id) {
        this.id = id;
        this.properties = Collections.emptyMap();

        this.hashCode = id.hashCode();
    }

    public static BlockState of(@NonNull Identifier id, @NonNull Map<String, String> properties) {
        BlockState state = VALUES.get(new Tuple<>(id, properties));
        if (state == null) { //need to register new state
            if (properties.isEmpty()) {
                properties = Collections.emptyMap();
                state = new BlockState(id);
            } else {
                properties = properties.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey().intern(), e -> e.getValue().intern()));
                state = new BlockState(id, properties);
            }
            state = PorkUtil.fallbackIfNull(VALUES.putIfAbsent(new Tuple<>(id, properties), state), state);
        }
        return state;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }
}
