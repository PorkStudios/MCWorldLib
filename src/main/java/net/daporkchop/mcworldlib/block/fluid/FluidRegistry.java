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

package net.daporkchop.mcworldlib.block.fluid;

import net.daporkchop.mcworldlib.block.BlockRegistry;

/**
 * Registry for keeping track of fluids.
 * <p>
 * This is an internal API. All methods operate on runtime IDs.
 *
 * @author DaPorkchop_
 * @see BlockRegistry
 */
public interface FluidRegistry {
    /**
     * Extracts the fluid component type from the given block.
     *
     * @param block the block to extract the fluid component type from
     * @return the extracted fluid type, or {@code 0} if the given block does not contain a fluid
     */
    int extractFluid(int block);

    /**
     * Removes the fluid component from the given block.
     *
     * @param block the block to remove the fluid component from
     * @return the runtime ID of the block with the fluid component removed
     */
    int stripFluid(int block);

    /**
     * Adds the fluid type to the given block as the fluid component.
     *
     * @param block the block to add the fluid component to
     * @param fluid the type of the fluid component to add
     * @return the runtime ID of the block with the given fluid component added, or {@code -1} if the given fluid type cannot be added
     */
    int addFluid(int block, int fluid);
}
