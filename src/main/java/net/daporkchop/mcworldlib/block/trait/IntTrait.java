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

import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * Extension of {@link Trait} for {@code int} values.
 *
 * @author DaPorkchop_
 */
@Getter
public final class IntTrait implements Trait<Integer> {
    public static IntTrait of(@NonNull String name, int max) {
        return of(name, 0, max);
    }

    public static IntTrait of(@NonNull String name, int min, int max) {
        return of(name, min, max, min);
    }

    public static IntTrait of(@NonNull String name, int min, int max, int defaultValue) {
        return new IntTrait(name, min, max, defaultValue);
    }

    protected final String name;
    protected final int min;
    protected final int max;
    protected final int defaultValue;

    private IntTrait(@NonNull String name, int min, int max, int defaultValue) {
        checkArg(min < max, "min (%d) must be less than max (%d)", min, max);
        this.name = name;
        this.min = min;
        this.max = max;

        checkArg(this.isValid(defaultValue), "invalid default value for trait %s: %d", name, defaultValue);
        this.defaultValue = defaultValue;
    }

    @Override
    public List<Integer> values() {
        return IntStream.rangeClosed(this.min, this.max).boxed().collect(Collectors.toList());
    }

    @Override
    public Integer defaultValue() {
        return this.defaultValue;
    }

    public int defaultValueInt() {
        return this.defaultValue;
    }

    @Override
    public boolean isValid(@NonNull Integer value) {
        return this.isValid(value.intValue());
    }

    public boolean isValid(int value) {
        return value >= this.min && value <= this.max;
    }

    @Override
    public int valueIndex(@NonNull Integer value) {
        return this.valueIndex(value.intValue());
    }

    public int valueIndex(int value) {
        checkArg(this.isValid(value), "invalid value for trait %s: %d", this.name, value);
        return value - this.min;
    }

    @Override
    public Integer valueFromIndex(int index) {
        return this.valueFromIndexInt(index);
    }

    public int valueFromIndexInt(int index) {
        int value = index + this.min;
        checkArg(this.isValid(value), "invalid index for trait %s: %d", this.name, index);
        return value;
    }

    @Override
    public String encodeValue(@NonNull Integer value) {
        return value.toString();
    }

    @Override
    public Integer decodeValue(@NonNull String encoded) {
        int value = Integer.parseInt(encoded);
        checkArg(this.isValid(value), "invalid value for trait %s: %d", this.name, value);
        return value;
    }
}
