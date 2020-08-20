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
import net.daporkchop.mcworldlib.block.registry.BlockRegistry;
import net.daporkchop.mcworldlib.util.Identifier;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Represents a block state: a specific combination of trait values for a specific block type.
 *
 * @author DaPorkchop_
 * @see BlockRegistry for an explanation of what all the values mean
 */
public interface BlockState {
    /**
     * @return the block ID corresponding to this block state
     */
    Identifier blockId();

    /**
     * @return the block's legacy ID, or {@code -1} if none
     */
    int legacyId();

    /**
     * @return the block state's raw metadata value, or {@code -1} if none
     */
    int meta();

    /**
     * @return the block state's runtime ID
     */
    int runtimeId();

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Trait} set to the given value.
     *
     * @param trait the {@link Trait} key to change
     * @param value the new property value
     * @param <V>   the property's value type
     * @return a {@link BlockState} with the given {@link Trait} set to the given value
     * @throws IllegalArgumentException if the given {@link Trait} was not registered for the block
     * @throws IllegalArgumentException if the given {@link Trait} cannot store the given value
     * @see #withTrait(Trait.Int, int)
     * @see #withTrait(Trait.Boolean, boolean)
     */
    <V> BlockState withTrait(@NonNull Trait<V> trait, @NonNull V value);

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Trait.Int} set to the given value.
     *
     * @param trait the {@link Trait.Int} key to change
     * @param value the new trait value
     * @return a {@link BlockState} with the given {@link Trait.Int} set to the given value
     * @throws IllegalArgumentException if the given {@link Trait.Int} was not registered for the block
     * @throws IllegalArgumentException if the given {@link Trait.Int} cannot store the given value
     */
    BlockState withTrait(@NonNull Trait.Int trait, int value);

    /**
     * Gets a {@link BlockState} with the same block {@link Identifier} and the given {@link Trait.Boolean} set to the given value.
     *
     * @param trait the {@link Trait.Boolean} key to change
     * @param value the new trait value
     * @return a {@link BlockState} with the given {@link Trait.Boolean} set to the given value
     * @throws IllegalArgumentException if the given {@link Trait.Boolean} was not registered for the block
     */
    BlockState withTrait(@NonNull Trait.Boolean trait, boolean value);

    /**
     * Convenience method, gets a {@link BlockState} with the same block {@link Identifier} and the trait with the given name set to the given value.
     *
     * @param trait the trait name
     * @param value the {@link String} representation of the new trait value
     * @return a {@link BlockState} with the same block {@link Identifier} and the trait with the given name set to the given value
     * @throws IllegalArgumentException if the a trait with the given name was not registered for the block
     * @throws IllegalArgumentException if the trait with the given name cannot parse or store the given value
     */
    default BlockState withTrait(@NonNull String trait, @NonNull String value) {
        Trait<?> prop = this.type().trait(trait);
        return this.withTrait(prop, uncheckedCast(prop.decodeValue(value)));
    }

    /**
     * Gets the value associated with the given property.
     *
     * @param trait the property to get the value for
     * @param <T>   the property's value type
     * @return the property's value
     * @throws IllegalArgumentException if the given {@link Trait} was not registered for the block
     */
    <T> T propertyValue(@NonNull Trait<T> trait);

    /**
     * @return the block type
     */
    BlockType type();
}
