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

package net.daporkchop.mcworldlib.block.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.Trait;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public class DefaultBlockState implements BlockState {
    @NonNull
    protected final BlockType type;
    protected final int runtimeId;

    @NonNull
    protected final ObjIntMap<Trait<?>> traitIndices;
    @NonNull
    protected final BlockState[][] otherStates;
    @NonNull
    protected final Object[] traitValues;

    @Override
    public <V> BlockState withTrait(@NonNull Trait<V> trait, @NonNull V value) {
        int traitIndex = this.traitIndices.getOrDefault(trait, -1);
        checkArg(traitIndex >= 0, "unknown trait: %s", trait);
        return this.otherStates[traitIndex][trait.valueIndex(value)];
    }

    @Override
    public BlockState withTrait(@NonNull Trait.Int trait, int value) {
        int traitIndex = this.traitIndices.getOrDefault(trait, -1);
        checkArg(traitIndex >= 0, "unknown trait: %s", trait);
        checkArg(trait.isValid(value), "invalid value for trait %s: %d", trait, value);
        return this.otherStates[traitIndex][value];
    }

    @Override
    public BlockState withTrait(@NonNull Trait.Boolean trait, boolean value) {
        int traitIndex = this.traitIndices.getOrDefault(trait, -1);
        checkArg(traitIndex >= 0, "unknown trait: %s", trait);
        return this.otherStates[traitIndex][value ? 1 : 0];
    }

    @Override
    public <T> T propertyValue(@NonNull Trait<T> trait) {
        int traitIndex = this.traitIndices.getOrDefault(trait, -1);
        checkArg(traitIndex >= 0, "unknown trait: %s", trait);
        return uncheckedCast(this.traitValues[traitIndex]);
    }
}
