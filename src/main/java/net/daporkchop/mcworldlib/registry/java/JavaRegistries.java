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

package net.daporkchop.mcworldlib.registry.java;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.primitive.map.concurrent.ObjObjConcurrentHashMap;
import net.daporkchop.mcworldlib.registry.DefaultRegistry;
import net.daporkchop.mcworldlib.registry.Registries;
import net.daporkchop.mcworldlib.registry.Registry;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.util.Util;
import net.daporkchop.mcworldlib.version.java.DataVersion;
import net.daporkchop.mcworldlib.version.java.JavaVersion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A collection of mappings of Java edition {@link Registry}s for legacy IDs.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
public final class JavaRegistries implements Registries {
    private static final Map<String, Registries> CACHE = new ObjObjConcurrentHashMap<>(); //this has a faster computeIfAbsent implementation

    public static Registries forVersion(@NonNull JavaVersion versionIn) {
        if (versionIn.data() < DataVersion.DATA_1_12_2) {
            versionIn = JavaVersion.fromName("1.12.2"); //1.12.2 is used as an intermediate translation point for all previous versions
        }
        return CACHE.computeIfAbsent(versionIn.name(), (IOFunction<String, Registries>) version -> {
            Map<String, JsonRegistry> map = Util.parseJson(JavaRegistries.class, new TypeReference<Map<String, JsonRegistry>>() {}, version + ".json");

            Map<Identifier, Registry> registries = new HashMap<>();
            map.forEach((name, registry) -> {
                Identifier id = Identifier.fromString(name);
                DefaultRegistry.Builder builder = DefaultRegistry.builder(id);
                registry.entries.forEach((entryName, entry) -> builder.register(Identifier.fromString(entryName), entry.protocol_id));
                registries.put(id, builder.build());
            });
            return new JavaRegistries(registries);
        });
    }

    @NonNull
    private final Map<Identifier, Registry> registries;

    @Override
    public int size() {
        return this.registries.size();
    }

    @Override
    public boolean has(@NonNull Identifier id) {
        return this.registries.containsKey(id);
    }

    @Override
    public Registry get(@NonNull Identifier id) {
        Registry registry = this.registries.get(id);
        checkArg(registry != null, "unknown registry ID: %s", id);
        return registry;
    }

    @Override
    public Iterator<Registry> iterator() {
        return this.registries.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super Registry> action) {
        this.registries.values().forEach(action);
    }

    @Override
    public Spliterator<Registry> spliterator() {
        return this.registries.values().spliterator();
    }

    private static class JsonRegistry {
        public final String def;
        public final Map<String, JsonRegistryEntry> entries;

        @JsonCreator
        public JsonRegistry(@JsonProperty("default") String def, @JsonProperty("entries") Map<String, JsonRegistryEntry> entries, @JsonProperty("protocol_id") int protocol_id) {
            this.def = def;
            this.entries = entries;
        }
    }

    private static class JsonRegistryEntry {
        public final int protocol_id;

        @JsonCreator
        public JsonRegistryEntry(@JsonProperty("protocol_id") int protocol_id) {
            this.protocol_id = protocol_id;
        }
    }
}
