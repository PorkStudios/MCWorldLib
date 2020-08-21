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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Extension of {@link Trait} for {@link Enum} values.
 *
 * @author DaPorkchop_
 */
@Getter
public final class EnumTrait<E extends Enum<E>> implements Trait<E> {
    public static <E extends Enum<E>> EnumTrait<E> of(@NonNull String name, @NonNull Class<E> clazz) {
        return of(name, clazz.getEnumConstants());
    }

    public static <E extends Enum<E>> EnumTrait<E> of(@NonNull String name, @NonNull Class<E> clazz, @NonNull E defaultValue) {
        return of(name, defaultValue, clazz.getEnumConstants());
    }

    public static <E extends Enum<E>> EnumTrait<E> of(@NonNull String name, @NonNull E... values) {
        return of(name, values[0], values);
    }

    public static <E extends Enum<E>> EnumTrait<E> of(@NonNull String name, @NonNull E defaultValue, @NonNull E... values) {
        return new EnumTrait<>(name, Arrays.asList(values), defaultValue);
    }

    protected final String name;
    protected final List<E> values;
    protected final E defaultValue;

    private EnumTrait(@NonNull String name, @NonNull List<E> values, @NonNull E defaultValue) {
        checkArg(!values.isEmpty(), "values may not be empty!");
        if (values.contains(null)) {
            throw new NullPointerException("values");
        }
        checkArg(values.size() == values.stream().distinct().count(), "duplicate values not allowed! %s", values);
        checkArg(values.contains(defaultValue), "default value (%s) is not present in list of values: %s", defaultValue, values);

        this.name = name;
        this.values = Arrays.asList(values.toArray(uncheckedCast(Array.newInstance(defaultValue.getClass(), values.size()))));
        this.defaultValue = defaultValue;
    }

    @Override
    public List<E> values() {
        return Collections.unmodifiableList(this.values);
    }

    @Override
    public boolean isValid(@NonNull E value) {
        return this.values.contains(value);
    }

    @Override
    public int valueIndex(@NonNull E value) {
        int index = this.values.indexOf(value);
        checkArg(index >= 0, "invalid value for trait %s: %s", this.name, value);
        return index;
    }

    @Override
    public E valueFromIndex(int index) {
        return this.values.get(index);
    }

    @Override
    public String encodeValue(@NonNull E value) {
        return value.toString();
    }

    @Override
    public E decodeValue(@NonNull String encoded) {
        Optional<E> optional = this.values.stream().filter(v -> encoded.equalsIgnoreCase(v.name())).findAny();
        checkArg(optional.isPresent(), "invalid value name for trait %s: \"%s\"", this.name, encoded);
        return optional.get();
    }
}
