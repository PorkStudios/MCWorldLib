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

package net.daporkchop.mcworldlib.block.property;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.Property;
import net.daporkchop.mcworldlib.block.PropertyMap;

import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Default implementation of {@link Property.Boolean}.
 *
 * @author DaPorkchop_
 */
public class BooleanPropertyImpl implements Property.Boolean {
    @Getter
    protected final String name;

    public BooleanPropertyImpl(@NonNull String name) {
        this.name = name.intern();
    }

    @Override
    public Stream<java.lang.Boolean> values() {
        return Stream.of(java.lang.Boolean.FALSE, java.lang.Boolean.TRUE);
    }

    @Override
    public PropertyMap<java.lang.Boolean> propertyMap(@NonNull Function<java.lang.Boolean, BlockState> mappingFunction) {
        return new PropertyMapImpl(mappingFunction.apply(java.lang.Boolean.TRUE), mappingFunction.apply(java.lang.Boolean.FALSE));
    }

    @Override
    public String encodeValue(@NonNull java.lang.Boolean value) {
        return String.valueOf(value);
    }

    @Override
    public java.lang.Boolean decodeValue(@NonNull String encoded) {
        return java.lang.Boolean.parseBoolean(encoded);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @RequiredArgsConstructor
    protected static class PropertyMapImpl implements PropertyMap.Boolean {
        @NonNull
        protected final BlockState trueState;
        @NonNull
        protected final BlockState falseState;

        @Override
        public BlockState getState(boolean value) {
            return value ? this.trueState : this.falseState;
        }
    }
}
