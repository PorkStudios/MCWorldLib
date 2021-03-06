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

package net.daporkchop.mcworldlib.util.nbt;

import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.lib.nbt.tag.Tag;

import java.util.Iterator;
import java.util.Map;

import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Static helper methods for dealing with NBT tags that contain allocated data.
 *
 * @author DaPorkchop_
 */
@UtilityClass
public class AllocatedNBTHelper {
    public void release(@NonNull Tag<?> tag) {
        if (tag instanceof CompoundTag) {
            ((CompoundTag) tag).forEach((name, childTag) -> release(childTag));
        } else if (tag instanceof ListTag) {
            ((ListTag<?>) tag).forEach(AllocatedNBTHelper::release);
        } else if (tag instanceof AllocatedTag) {
            ((AllocatedTag) tag).release();
        }
    }

    public <T extends Tag> T toNormalAndRelease(@NonNull Tag<?> tag) {
        if (tag instanceof CompoundTag) {
            for (Iterator<Map.Entry<String, Tag>> itr = ((CompoundTag) tag).iterator(); itr.hasNext(); ) {
                Map.Entry<String, Tag> entry = itr.next();
                entry.setValue(toNormalAndRelease(entry.getValue()));
            }
        } else if (tag instanceof ListTag) {
            ((ListTag<?>) tag).list().replaceAll(AllocatedNBTHelper::toNormalAndRelease);
        } else if (tag instanceof AllocatedTag) {
            tag = ((AllocatedTag) tag).toNormalAndRelease();
        }
        return uncheckedCast(tag);
    }
}
