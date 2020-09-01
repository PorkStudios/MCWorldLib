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

package net.daporkchop.mcworldlib.block.access;

import lombok.NonNull;
import net.daporkchop.mcworldlib.block.BlockState;
import net.daporkchop.mcworldlib.util.Identifier;

/**
 * Provides access to fluid data at given coordinates.
 *
 * @author DaPorkchop_
 */
public interface FluidAccess {
    //
    //
    // getters
    //
    //

    /**
     * Gets the {@link BlockState} of the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to get
     * @param y the Y coordinate of the fluid to get
     * @param z the Z coordinate of the fluid to get
     * @return the {@link BlockState}, or air if there is no fluid at the given coordinates
     */
    BlockState getFluidState(int x, int y, int z);

    /**
     * Gets the {@link Identifier} of the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to get
     * @param y the Y coordinate of the fluid to get
     * @param z the Z coordinate of the fluid to get
     * @return the {@link Identifier} of the fluid, or air if there is no fluid at the given coordinates
     */
    Identifier getFluidId(int x, int y, int z);

    /**
     * Gets the legacy ID of the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to get
     * @param y the Y coordinate of the fluid to get
     * @param z the Z coordinate of the fluid to get
     * @return the legacy ID of the fluid, or air if there is no fluid at the given coordinates
     */
    int getFluidLegacyId(int x, int y, int z);

    /**
     * Gets the metadata of the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to get
     * @param y the Y coordinate of the fluid to get
     * @param z the Z coordinate of the fluid to get
     * @return the metadata of the fluid, or air if there is no fluid at the given coordinates
     */
    int getFluidMeta(int x, int y, int z);

    /**
     * Gets the runtime ID of the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to get
     * @param y the Y coordinate of the fluid to get
     * @param z the Z coordinate of the fluid to get
     * @return the runtime ID of the fluid, or air if there is no fluid at the given coordinates
     */
    int getFluidRuntimeId(int x, int y, int z);

    //
    //
    // setters
    //
    //

    /**
     * Removes the fluid at the given coordinates.
     *
     * @param x the X coordinate of the fluid to remove
     * @param y the Y coordinate of the fluid to remove
     * @param z the Z coordinate of the fluid to remove
     */
    void setFluidEmpty(int x, int y, int z);

    /**
     * Sets the {@link BlockState} at the given coordinates.
     *
     * @param x     the X coordinate of the fluid to set
     * @param y     the Y coordinate of the fluid to set
     * @param z     the Z coordinate of the fluid to set
     * @param state the new {@link BlockState}
     */
    void setFluidState(int x, int y, int z, @NonNull BlockState state);

    /**
     * Sets the fluid state at the given coordinates.
     *
     * @param x    the X coordinate of the fluid to set
     * @param y    the Y coordinate of the fluid to set
     * @param z    the Z coordinate of the fluid to set
     * @param id   the new {@link Identifier}
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given block {@link Identifier}
     */
    void setFluidState(int x, int y, int z, @NonNull Identifier id, int meta);

    /**
     * Sets the fluid state at the given coordinates.
     *
     * @param x        the X coordinate of the fluid to set
     * @param y        the Y coordinate of the fluid to set
     * @param z        the Z coordinate of the fluid to set
     * @param legacyId the new legacy ID
     * @param meta     the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the given legacy block ID
     */
    void setFluidState(int x, int y, int z, int legacyId, int meta);

    /**
     * Sets the {@link Identifier} of the fluid at the given coordinates.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x  the X coordinate of the fluid to set
     * @param y  the Y coordinate of the fluid to set
     * @param z  the Z coordinate of the fluid to set
     * @param id the new {@link Identifier}
     */
    void setFluidId(int x, int y, int z, @NonNull Identifier id);

    /**
     * Sets the legacy ID of the fluid at the given coordinates.
     * <p>
     * The metadata value will be automatically set to the default (normally {@code 0}).
     *
     * @param x        the X coordinate of the fluid to set
     * @param y        the Y coordinate of the fluid to set
     * @param z        the Z coordinate of the fluid to set
     * @param legacyId the new legacy ID
     */
    void setFluidLegacyId(int x, int y, int z, int legacyId);

    /**
     * Sets the metadata of the fluid at the given coordinates.
     * <p>
     * the fluid ID will remain unaffected by this change.
     *
     * @param x    the X coordinate of the fluid to set
     * @param y    the Y coordinate of the fluid to set
     * @param z    the Z coordinate of the fluid to set
     * @param meta the new metadata
     * @throws IllegalArgumentException if the given metadata value was not registered for the fluid at the given coordinates
     */
    void setFluidMeta(int x, int y, int z, int meta);

    /**
     * Sets the runtime ID of the fluid at the given coordinates.
     *
     * @param x         the X coordinate of the fluid to set
     * @param y         the Y coordinate of the fluid to set
     * @param z         the Z coordinate of the fluid to set
     * @param runtimeId the new runtime ID
     */
    void setFluidRuntimeId(int x, int y, int z, int runtimeId);
}
