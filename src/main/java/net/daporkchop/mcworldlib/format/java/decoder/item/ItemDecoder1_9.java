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

package net.daporkchop.mcworldlib.format.java.decoder.item;

import lombok.NonNull;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.item.ItemMeta;
import net.daporkchop.mcworldlib.item.ItemStack;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import static net.daporkchop.mcworldlib.item.ItemMeta.*;

/**
 * @author DaPorkchop_
 */
public class ItemDecoder1_9 extends ItemDecoder1_8 {
    public ItemDecoder1_9() {
        this(new ItemDecoder1_8());
    }

    public ItemDecoder1_9(@NonNull ItemDecoder1_8 parent) {
        super(parent);
    }

    @Override
    protected void initialDecode(@NonNull ItemStack stack, @NonNull Cache cache, @NonNull CompoundTag root, CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
        int damage = root.getShort("Damage", (short) 0);

        BlockRegistry blockRegistry = world.parent().blockRegistryFor(version);
        if (blockRegistry.containsBlockId(stack.id())) {
            cache.meta.put(BLOCK_STATE, blockRegistry.getState(stack.id(), damage));
        } else {
            cache.meta.put(DAMAGE, damage);
        }
    }
}
