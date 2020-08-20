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
import net.daporkchop.lib.common.function.PFunctions;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.mcworldlib.block.registry.BlockRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class DefaultBlockType implements BlockType {
    protected final BlockState defaultState;

    protected final Map<String, Trait<?>> traitsByName;
    protected final Collection<Trait<?>> traits;

    public DefaultBlockType(@NonNull BlockRegistry registry, @NonNull Trait... traits)   {
        this.traitsByName = Stream.of(PorkUtil.<Trait<?>[]>uncheckedCast(traits)).peek(Objects::requireNonNull).collect(Collectors.toMap(Trait::name, PFunctions.identity()));
        this.traits = Collections.unmodifiableCollection(this.traitsByName.values());
    }

    @Override
    public <T> Trait<T> trait(@NonNull String name) {
        Trait<T> trait = uncheckedCast(this.traitsByName.get(name));
        checkArg(trait != null, "unknown trait: %s", name);
        return trait;
    }
}
