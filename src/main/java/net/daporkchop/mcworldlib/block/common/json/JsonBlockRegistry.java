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

package net.daporkchop.mcworldlib.block.common.json;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.reflection.type.PTypes;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.Property;
import net.daporkchop.mcworldlib.block.common.AbstractBlockRegistry;
import net.daporkchop.mcworldlib.block.common.DefaultBlockState;
import net.daporkchop.mcworldlib.block.property.BooleanPropertyImpl;
import net.daporkchop.mcworldlib.block.property.EnumPropertyImpl;
import net.daporkchop.mcworldlib.block.property.IntPropertyImpl;
import net.daporkchop.mcworldlib.registry.Registry;
import net.daporkchop.mcworldlib.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Implements the loading of block registries from their json form.
 *
 * @author DaPorkchop_
 */
public abstract class JsonBlockRegistry extends AbstractBlockRegistry {
    private static final Type BLOCK_MAP_TYPE = PTypes.parameterized(Map.class, String.class, JsonBlock.class);

    public static Builder parse(@NonNull InputStream in, @NonNull Registry legacyBlockRegistry) throws IOException {
        Map<String, JsonBlock> map = InstancePool.getInstance(Gson.class).fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), BLOCK_MAP_TYPE);

        Builder builder = new Builder();
        map.forEach((name, block) -> {
            Identifier id = Identifier.fromString(name);
            BlockBuilder blockBuilder = builder.startBlock(id);

            Map<String, Property<?>> propertyLookup = block.properties.entrySet().stream()
                    .map(e -> makeProperty(Identifier.fromString(e.getKey()), e.getValue()))
                    .collect(Collectors.toMap(Property::name, PFunctions.identity()));

            blockBuilder.propertyLookup(propertyLookup)
                    .states(block.states)
                    .legacyId(legacyBlockRegistry.get(id));
        });
        return builder;
    }

    private static Property<?> makeProperty(@NonNull Identifier name, @NonNull List<String> values) {
        try {
            int min = values.stream().mapToInt(Integer::parseUnsignedInt).min().orElse(0);
            int max = values.stream().mapToInt(Integer::parseUnsignedInt).max().orElse(0) + 1;
            return new IntPropertyImpl(name.name(), min, max);
        } catch (NumberFormatException ignored) {
        }

        if (values.size() == 2 && values.contains("true") && values.contains("false")) {
            return new BooleanPropertyImpl(name.name());
        } else {
            return new EnumPropertyImpl(name.name(), values);
        }
    }

    public JsonBlockRegistry(@NonNull InputStream in, @NonNull Registry legacyBlockRegistry) throws IOException {
        super(parse(in, legacyBlockRegistry));
    }

    @Getter
    public static final class JsonBlock {
        public Map<String, List<String>> properties = Collections.emptyMap();
        public List<JsonState> states;
    }

    @Getter
    public static final class JsonState {
        public Map<String, String> properties = Collections.emptyMap();

        public int id;

        @SerializedName("default")
        public boolean isDefault = false;
        @SerializedName("virtual")
        public boolean isVirtual = false;
    }

    @RequiredArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Builder extends AbstractBlockRegistry.Builder<JsonBlockRegistry.Builder, JsonBlockRegistry.BlockBuilder, JsonBlockRegistry> {
        @Override
        protected JsonBlockRegistry.BlockBuilder blockBuilder(@NonNull Identifier id) {
            return new JsonBlockRegistry.BlockBuilder(this, id);
        }

        @Override
        public JsonBlockRegistry build() {
            throw new IllegalStateException();
        }
    }

    @Getter
    public static class BlockBuilder extends AbstractBlockRegistry.BlockBuilder<JsonBlockRegistry.BlockBuilder, JsonBlockRegistry.Builder, JsonBlockRegistry> {
        protected Map<String, Property<?>> propertyLookup;
        protected List<JsonState> statesList;
        protected Map<Map<Property<?>, ?>, JsonState> states;
        protected int legacyId = -1;
        protected int firstRuntimeId = -1;

        protected BlockBuilder(JsonBlockRegistry.Builder parent, Identifier id) {
            super(parent, id);
        }

        public JsonBlockRegistry.BlockBuilder legacyId(int legacyId) {
            this.legacyId = notNegative(legacyId, "legacyId");
            return this;
        }

        public JsonBlockRegistry.BlockBuilder propertyLookup(@NonNull Map<String, Property<?>> propertyLookup) {
            this.propertyLookup = propertyLookup;
            this.properties = propertyLookup.values().toArray(new Property[propertyLookup.size()]);
            return this;
        }

        public JsonBlockRegistry.BlockBuilder states(@NonNull List<JsonState> states) {
            this.firstRuntimeId = states.stream().mapToInt(JsonState::id).min().orElseThrow(IllegalArgumentException::new);
            this.statesList = states;
            return this;
        }

        @Override
        protected void validateState() {
            checkState(this.legacyId >= 0, "legacyId must be set! (block: %s)", this.id);
            checkState(this.propertyLookup != null, "propertyLookup must be set! (block: %s)", this.id);
            checkState(this.statesList != null, "states must be set! (block: %s)", this.id);

            this.states = this.statesList.stream().collect(Collectors.toMap(
                    state -> state.properties.entrySet().stream().collect(Collectors.toMap(
                            e -> this.propertyLookup.get(e.getKey()),
                            e -> this.propertyLookup.get(e.getKey()).decodeValue(e.getValue()))),
                    PFunctions.identity()));
        }

        @Override
        protected DefaultBlockState makeState(@NonNull JsonBlockRegistry registry, @NonNull Map<Property<?>, ?> properties) {
            JsonState state = this.states.get(properties);
            int meta = state.id - this.firstRuntimeId;
            return new DefaultBlockState(registry, this.id, this.legacyId, meta, state.id);
        }

        @Override
        protected BlockState[] getMetaArray(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates) {
            BlockState[] arr = new BlockState[propertiesToStates.values().stream().mapToInt(DefaultBlockState::meta).max().orElse(-1) + 1];
            propertiesToStates.values().forEach(state -> arr[state.runtimeId() - this.firstRuntimeId] = state);
            return arr;
        }

        @Override
        protected DefaultBlockState getDefaultState(@NonNull Map<Map<Property<?>, ?>, DefaultBlockState> propertiesToStates, @NonNull BlockState[] metas) {
            return propertiesToStates.entrySet().stream()
                    .filter(e -> this.states.get(e.getKey()).isDefault)
                    .map(Map.Entry::getValue)
                    .findAny()
                    .orElseThrow(IllegalStateException::new);
        }
    }
}
