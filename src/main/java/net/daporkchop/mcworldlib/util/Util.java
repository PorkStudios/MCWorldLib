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

package net.daporkchop.mcworldlib.util;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.common.misc.InstancePool;
import net.daporkchop.lib.common.misc.string.PStrings;

import java.io.IOException;
import java.io.InputStream;

/**
 * Global utility methods.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Util {
    public <T> T doWithResourceThrowing(@NonNull Class<?> fromClass, @NonNull String name, @NonNull IOFunction<InputStream, T> function) {
        return doWithResourceThrowing(fromClass, name, function, true);
    }

    public <T> T doWithResourceThrowing(@NonNull Class<?> fromClass, @NonNull String name, @NonNull IOFunction<InputStream, T> function, boolean requireExists) {
        try (InputStream in = fromClass.getResourceAsStream(name)) {
            if (in == null) {
                if (requireExists) {
                    throw new IllegalArgumentException(PStrings.fastFormat("unable to find resource \"%s\" from %s!", name, fromClass));
                } else {
                    return null;
                }
            }
            return function.applyThrowing(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T parseJson(@NonNull Class<T> type, @NonNull String name) {
        return parseJson(type, type, name);
    }

    public <T> T parseJson(@NonNull Class<?> fromClass, @NonNull Class<T> type, @NonNull String name) {
        return doWithResourceThrowing(fromClass, name, in -> InstancePool.getInstance(JsonMapper.class).readValue(in, type));
    }
}
