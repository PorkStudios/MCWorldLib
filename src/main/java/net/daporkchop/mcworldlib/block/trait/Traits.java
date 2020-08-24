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

package net.daporkchop.mcworldlib.block.trait;

import lombok.experimental.UtilityClass;
import net.daporkchop.mcworldlib.block.trait.value.ButtonType;
import net.daporkchop.mcworldlib.block.trait.value.DoorType;
import net.daporkchop.mcworldlib.block.trait.value.FenceType;
import net.daporkchop.mcworldlib.block.trait.value.HingeSide;
import net.daporkchop.mcworldlib.block.trait.value.SlabPart;
import net.daporkchop.mcworldlib.block.trait.value.SlabType;
import net.daporkchop.mcworldlib.block.trait.value.StairShape;
import net.daporkchop.mcworldlib.block.trait.value.WeightedPressurePlateType;
import net.daporkchop.mcworldlib.block.trait.value.WoodType;
import net.daporkchop.mcworldlib.block.trait.value.StoneType;
import net.daporkchop.mcworldlib.block.trait.value.LeafType;
import net.daporkchop.mcworldlib.util.Axis;
import net.daporkchop.mcworldlib.util.BlockFace;
import net.daporkchop.mcworldlib.util.Direction;
import net.daporkchop.mcworldlib.util.Rotation;

/**
 * Helper class, contains static fields for all global block traits.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class Traits {
    public final BooleanTrait NORTH = BooleanTrait.of("north");
    public final BooleanTrait SOUTH = BooleanTrait.of("south");
    public final BooleanTrait EAST = BooleanTrait.of("east");
    public final BooleanTrait WEST = BooleanTrait.of("west");

    public final EnumTrait<Axis> AXIS = EnumTrait.of("axis", Axis.class, Axis.Y);
    public final EnumTrait<Direction> DIRECTION = EnumTrait.of("direction", Direction.class);
    public final EnumTrait<BlockFace> FACE = EnumTrait.of("face", BlockFace.class);
    public final EnumTrait<Direction> FACING = EnumTrait.of("facing", Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);
    public final EnumTrait<Rotation> ROTATION = EnumTrait.of("rotation", Rotation.class);

    public final EnumTrait<ButtonType> BUTTON_TYPE = EnumTrait.of("button_type", ButtonType.class, ButtonType.OAK);
    public final IntTrait DISTANCE = IntTrait.of("distance", 1, 7);
    public final EnumTrait<DoorType> DOOR_TYPE = EnumTrait.of("door_type", DoorType.class, DoorType.OAK);
    public final EnumTrait<FenceType> FENCE_TYPE = EnumTrait.of("fence_type", FenceType.class, FenceType.OAK);
    public final EnumTrait<HingeSide> HINGE_SIDE = EnumTrait.of("hinge_side", HingeSide.class);
    public final BooleanTrait IN_WALL = BooleanTrait.of("in_wall");
    public final EnumTrait<LeafType> LEAF_TYPE = EnumTrait.of("leaf_type", LeafType.class, LeafType.OAK);
    public final BooleanTrait OPEN = BooleanTrait.of("open");
    public final BooleanTrait PERSISTENT = BooleanTrait.of("persistent");
    public final IntTrait POWER = IntTrait.of("power", 15);
    public final BooleanTrait POWERED = BooleanTrait.of("powered");
    public final BooleanTrait PRESSED = BooleanTrait.of("pressed");
    public final IntTrait SAPLING_STAGE = IntTrait.of("sapling_stage", 1);
    public final EnumTrait<SlabPart> SLAB_PART = EnumTrait.of("slab_part", SlabPart.class);
    public final EnumTrait<SlabType> SLAB_TYPE = EnumTrait.of("slab_type", SlabType.class, SlabType.SMOOTH_STONE);
    public final BooleanTrait SNOWY = BooleanTrait.of("snowy");
    public final EnumTrait<StairShape> STAIR_SHAPE = EnumTrait.of("stair_shape", StairShape.class, StairShape.STRAIGHT);
    public final EnumTrait<StoneType> STONE_TYPE = EnumTrait.of("stone_type", StoneType.class, StoneType.STONE);
    public final BooleanTrait TOP = BooleanTrait.of("top");
    public final BooleanTrait UPSIDE_DOWN = BooleanTrait.of("upside_down");
    public final BooleanTrait WATER_FALLING = BooleanTrait.of("water_falling");
    public final IntTrait WATER_LEVEL = IntTrait.of("water_level", 7);
    public final EnumTrait<WeightedPressurePlateType> WEIGHTED_PRESSURE_PLATE_TYPE = EnumTrait.of("weighted_pressure_plate_type", WeightedPressurePlateType.class, WeightedPressurePlateType.LIGHT);
    public final EnumTrait<WoodType> WOOD_TYPE = EnumTrait.of("wood_type", WoodType.class, WoodType.OAK);
}
