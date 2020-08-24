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

package net.daporkchop.mcworldlib.tileentity;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.util.property.PropertyKey;

import java.util.IdentityHashMap;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Default implementation of {@link TileEntity}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Accessors(fluent = true)
public class DefaultTileEntity extends IdentityHashMap<PropertyKey<?>, Object> implements TileEntity {
    @Getter
    @NonNull
    protected final Identifier id;

    @Override
    public boolean has(@NonNull PropertyKey<?> key) {
        return super.containsKey(key);
    }

    @Override
    public <T> T get(@NonNull PropertyKey<T> key) {
        return uncheckedCast(super.getOrDefault(key, key.defaultValue()));
    }

    @Override
    public TileEntity remove(@NonNull PropertyKey<?> key) {
        super.remove(key);
        return this;
    }

    @Override
    public <T> TileEntity put(@NonNull PropertyKey<T> key, T value) {
        if ((value = key.process(value)) != null) {
            super.put(key, uncheckedCast(value));
        } else {
            super.remove(key);
        }
        return this;
    }

    @Override
    public String toString() {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);

            builder.append(this.id());
            int idLength = builder.length();

            builder.append('{');
            this.forEach((key, value) -> {
                if (key.isSet(uncheckedCast(value))) {
                    key.append(builder, uncheckedCast(value));
                }
            });
            if (builder.length() == idLength + 1) {
                builder.setLength(idLength);
            } else {
                builder.setLength(builder.length() - 2);
                builder.append('}');
            }
            return builder.toString();
        }
    }
}
