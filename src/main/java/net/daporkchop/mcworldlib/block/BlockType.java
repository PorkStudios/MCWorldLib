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

import lombok.NonNull;
import net.daporkchop.mcworldlib.block.trait.BooleanTrait;
import net.daporkchop.mcworldlib.block.trait.IntTrait;
import net.daporkchop.mcworldlib.block.trait.Trait;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Collection;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Representation of a block type.
 *
 * @author DaPorkchop_
 */
public interface BlockType {
    /**
     * @return this block type's ID
     */
    Identifier id();

    /**
     * @return this block type's default state
     */
    BlockState defaultState();

    /**
     * @return all properties supported by the block
     */
    Collection<Trait<?>> traits();

    /**
     * Gets the {@link Trait} with the given name.
     *
     * @param name the name of the property to get
     * @param <T>  the property's value type
     * @return the {@link Trait} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     */
    <T> Trait<T> trait(@NonNull String name);

    /**
     * Gets the {@link IntTrait} with the given name.
     *
     * @param name the name of the property to get
     * @return the {@link IntTrait} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     * @throws ClassCastException       if the property is not a {@link IntTrait}
     */
    default IntTrait traitInt(@NonNull String name) {
        return uncheckedCast(this.trait(name));
    }

    /**
     * Gets the {@link BooleanTrait} with the given name.
     *
     * @param name the name of the property to get
     * @return the {@link BooleanTrait} with the given name
     * @throws IllegalArgumentException if the block does not have any properties with the given name
     * @throws ClassCastException       if the property is not a {@link BooleanTrait}
     */
    default BooleanTrait traitBoolean(@NonNull String name) {
        return uncheckedCast(this.trait(name));
    }
}
