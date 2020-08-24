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
import lombok.experimental.UtilityClass;
import net.daporkchop.mcworldlib.block.registry.GlobalBlockRegistry;
import net.daporkchop.mcworldlib.block.trait.Trait;
import net.daporkchop.mcworldlib.block.trait.Traits;

/**
 * Helper class, contains static fields for all global block types.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Blocks {
    public final BlockType AIR = GlobalBlockRegistry.INSTANCE.air().type();

    public final BlockType BUTTON = register("button", Traits.BUTTON_TYPE, Traits.PRESSED, Traits.FACE, Traits.FACING);
    public final BlockType COARSE_DIRT = register("coarse_dirt");
    public final BlockType DIRT = register("dirt");
    public final BlockType DOOR = register("door", Traits.DOOR_TYPE, Traits.TOP, Traits.HINGE_SIDE, Traits.OPEN, Traits.POWERED);
    public final BlockType FENCE = register("fence", Traits.FENCE_TYPE, Traits.NORTH, Traits.EAST, Traits.SOUTH, Traits.WEST);
    public final BlockType FENCE_GATE = register("fence_gate", Traits.FENCE_TYPE, Traits.FACING, Traits.IN_WALL, Traits.OPEN, Traits.POWERED);
    public final BlockType GRASS = register("grass", Traits.SNOWY);
    public final BlockType LEAVES = register("leaves", Traits.LEAF_TYPE, Traits.DISTANCE, Traits.PERSISTENT);
    public final BlockType LOG = register("log", Traits.WOOD_TYPE, Traits.AXIS);
    public final BlockType PLANKS = register("planks", Traits.WOOD_TYPE);
    public final BlockType PRESSURE_PLATE = register("pressure_plate", Traits.BUTTON_TYPE, Traits.PRESSED);
    public final BlockType SAPLING = register("sapling", Traits.WOOD_TYPE, Traits.SAPLING_STAGE);
    public final BlockType SIGN = register("sign", Traits.WOOD_TYPE, Traits.FACING);
    public final BlockType SLAB = register("slab", Traits.SLAB_TYPE, Traits.SLAB_PART);
    public final BlockType STAIRS = register("stairs", Traits.SLAB_TYPE, Traits.FACING, Traits.UPSIDE_DOWN, Traits.STAIR_SHAPE);
    public final BlockType STANDING_SIGN = register("standing_sign", Traits.WOOD_TYPE, Traits.ROTATION);
    public final BlockType STONE = register("stone", Traits.STONE_TYPE);
    public final BlockType TRAPDOOR = register("trapdoor", Traits.DOOR_TYPE, Traits.FACING, Traits.OPEN, Traits.POWERED);
    public final BlockType WATER = register("water", Traits.WATER_FALLING, Traits.WATER_LEVEL);
    public final BlockType WEIGHTED_PRESSURE_PLATE = register("weighted_pressure_plate", Traits.WEIGHTED_PRESSURE_PLATE_TYPE, Traits.POWER);

    private static BlockType register(@NonNull String id, @NonNull Trait<?>... traits) {
        return GlobalBlockRegistry.INSTANCE.register(id, traits);
    }
}
