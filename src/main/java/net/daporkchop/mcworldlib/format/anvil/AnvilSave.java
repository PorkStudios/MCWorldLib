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

package net.daporkchop.mcworldlib.format.anvil;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.Tag;
import net.daporkchop.mcworldlib.format.common.AbstractSave;
import net.daporkchop.mcworldlib.format.common.DefaultDimension;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.nbt.AllocatingNBTObjectParser;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.Dimension;

import java.io.File;

/**
 * @author DaPorkchop_
 */
public class AnvilSave extends AbstractSave<JavaVersion> {
    @Getter
    protected final NBTOptions chunkNBTOptions;

    public AnvilSave(SaveOptions options, CompoundTag levelData, File root) {
        super(options, root);

        this.chunkNBTOptions = NBTOptions.DEFAULT
                .withObjectParser(new AllocatingNBTObjectParser(Tag.DEFAULT_NBT_PARSER, options));

        this.version = this.extractVersion(levelData);

        //find worlds
        this.openWorld(new DefaultDimension(Dimension.ID_OVERWORLD, 0, true, true));
        if (PFiles.checkDirectoryExists(new File(this.root, "DIM-1"))) {
            this.openWorld(new DefaultDimension(Dimension.ID_NETHER, -1, false, false));
        }
        if (PFiles.checkDirectoryExists(new File(this.root, "DIM1"))) {
            this.openWorld(new DefaultDimension(Dimension.ID_END, 1, false, false));
        }

        this.validateState();
    }

    protected void openWorld(@NonNull Dimension dimension) {
        this.worlds.put(dimension.id(), new AnvilWorld(this, dimension));
    }

    protected JavaVersion extractVersion(@NonNull CompoundTag levelData) {
        CompoundTag versionTag = levelData.getCompound("Data").getCompound("Version", null);
        if (versionTag == null) { //older than 15w32a
            return JavaVersion.pre15w32a();
        }
        return JavaVersion.fromName(versionTag.getString("Name"));
    }
}
