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

package net.daporkchop.mcworldlib.block.trait;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.string.PStrings;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Extension of {@link Trait} for {@code boolean} values.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public final class BooleanTrait implements Trait<Boolean> {
    protected static final List<Boolean> VALUES_LIST = Collections.unmodifiableList(Arrays.asList(Boolean.FALSE, Boolean.TRUE));

    public static BooleanTrait of(@NonNull String name) {
        return of(name, false);
    }

    public static BooleanTrait of(@NonNull String name, boolean defaultValue) {
        return new BooleanTrait(name, defaultValue);
    }

    @NonNull
    protected final String name;
    protected final boolean defaultValue;

    @Override
    public List<Boolean> values() {
        return VALUES_LIST;
    }

    @Override
    public Boolean defaultValue() {
        return this.defaultValue;
    }

    public boolean defaultValueBoolean() {
        return this.defaultValue;
    }

    @Override
    public boolean isValid(@NonNull Boolean value) {
        return true;
    }

    @Override
    public int valueIndex(@NonNull Boolean value) {
        return this.valueIndex(value.booleanValue());
    }

    public int valueIndex(boolean value) {
        return value ? 1 : 0;
    }

    @Override
    public Boolean valueFromIndex(int index) {
        return this.valueFromIndexBoolean(index);
    }

    public boolean valueFromIndexBoolean(int index) {
        if (index == 0) {
            return false;
        } else if (index == 1) {
            return true;
        } else {
            throw new IllegalArgumentException(PStrings.fastFormat("invalid index for trait %s: %d", this.name, index));
        }
    }

    @Override
    public String encodeValue(@NonNull Boolean value) {
        return value.toString();
    }

    @Override
    public Boolean decodeValue(@NonNull String encoded) {
        return Boolean.parseBoolean(encoded);
    }
}
