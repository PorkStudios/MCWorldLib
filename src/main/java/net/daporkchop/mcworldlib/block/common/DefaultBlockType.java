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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.primitive.map.ObjIntMap;
import net.daporkchop.lib.primitive.map.open.ObjIntOpenHashMap;
import net.daporkchop.lib.primitive.map.open.ObjObjOpenHashMap;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.block.BlockType;
import net.daporkchop.mcworldlib.block.Trait;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * @author DaPorkchop_
 */
@Getter
public class DefaultBlockType implements BlockType {
    @Getter(AccessLevel.NONE)
    protected final Map<String, Trait<?>> traitsByName = new ObjObjOpenHashMap<>();

    protected final Identifier id;
    protected final BlockState defaultState;

    public DefaultBlockType(@NonNull IntSupplier runtimeIdAllocator, @NonNull Identifier id, @NonNull Trait<?>... traits) {
        for (Trait<?> trait : traits) {
            checkArg(this.traitsByName.put(Objects.requireNonNull(trait, "traits").name(), trait) == null, "duplicate trait: %s", trait);
        }
        this.id = id;

        if (traits.length == 0) {
            this.defaultState = new DefaultBlockState(this, runtimeIdAllocator.getAsInt(), new ObjIntOpenHashMap.Identity<>(), new BlockState[0][], new Object[0]);
        } else {
            ObjIntMap<Trait<?>> traitIndices = new ObjIntOpenHashMap.Identity<>();
            for (int i = 0; i < traits.length; i++) {
                traitIndices.put(traits[i], i);
            }

            Object[][] values = Arrays.stream(traits).map(Trait::values).map(s -> s.toArray()).toArray(Object[][]::new);
            Object[][] tempStates = new Object[Arrays.stream(values).mapToInt(arr -> arr.length).reduce(1, (a, b) -> a * b)][values.length];

            int[] traitFactors = new int[values.length];
            for (int i = values.length - 1, factor = 1; i >= 0; i--, factor *= values[i].length) {
                traitFactors[i] = factor;
            }

            //build trait values
            for (int traitIndex = values.length - 1; traitIndex >= 0; traitIndex--)  {
                Object[] traitValues = values[traitIndex];
                int factor = traitFactors[traitIndex];
                for (int stateIndex = 0; stateIndex < tempStates.length;) {
                    for (int valueIndex = 0; valueIndex < traitValues.length; valueIndex++) {
                        Object traitValue = traitValues[valueIndex];
                        for (int i = 0; i < factor; i++) {
                            tempStates[stateIndex++][traitIndex] = traitValue;
                        }
                    }
                }
            }

            //build actual block states
            BlockState[] states = Arrays.stream(tempStates)
                    .map(traitValues -> new DefaultBlockState(
                            this, runtimeIdAllocator.getAsInt(), traitIndices,
                            Arrays.stream(values).map(a -> new BlockState[a.length]).toArray(BlockState[][]::new), traitValues))
                    .toArray(BlockState[]::new);

            //build other states index
            for (int stateIndex = 0; stateIndex < states.length; stateIndex++) {
                DefaultBlockState state = (DefaultBlockState) states[stateIndex];
                for (int traitIndex = 0; traitIndex < values.length; traitIndex++) {
                    Object[] traitValues = values[traitIndex];
                    BlockState[] traitOtherStates = state.otherStates[traitIndex];
                    int traitFactor = traitFactors[traitIndex];
                    Trait<?> trait = traits[traitIndex];
                    int traitStateIndexBase = stateIndex - traitFactor * trait.valueIndex(uncheckedCast(state.traitValues[traitIndex]));
                    for (int valueIndex = 0; valueIndex < traitValues.length; valueIndex++) {
                        traitOtherStates[valueIndex] = states[traitStateIndexBase + traitFactor * valueIndex];
                    }
                }
            }

            int defaultStateIndex = 0;
            for (int traitIndex = 0; traitIndex < values.length; traitIndex++) {
                Trait<?> trait = traits[traitIndex];
                defaultStateIndex += traitFactors[traitIndex] * trait.valueIndex(uncheckedCast(trait.defaultValue()));
            }
            this.defaultState = states[defaultStateIndex];
        }
    }

    @Override
    public Collection<Trait<?>> traits() {
        return Collections.unmodifiableCollection(this.traitsByName.values());
    }

    @Override
    public <T> Trait<T> trait(@NonNull String name) {
        Trait<T> trait = uncheckedCast(this.traitsByName.get(name));
        checkArg(trait != null, "no trait found for name: %s", name);
        return trait;
    }
}
