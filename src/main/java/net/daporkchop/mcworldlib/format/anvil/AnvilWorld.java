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
import net.daporkchop.mcworldlib.format.anvil.storage.AnvilWorldStorage;
import net.daporkchop.mcworldlib.format.vanilla.VanillaWorld;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.WriteAccess;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.Dimension;
import net.daporkchop.mcworldlib.world.common.IWorld;
import net.daporkchop.mcworldlib.world.WorldInfo;

import java.io.File;

/**
 * Implementation of {@link IWorld} for the Anvil format.
 *
 * @author DaPorkchop_
 */
public class AnvilWorld extends VanillaWorld<AnvilSave> implements WorldInfo {
    @Getter
    protected final Dimension dimension;

    public AnvilWorld(AnvilSave parent, @NonNull Dimension dimension) {
        super(parent, dimension.id());

        this.dimension = dimension;

        this.blockRegistry = parent.blockRegistry();

        //anvil is implemented in a way that makes it a real pain to have their dimension be abstracted away from the individual worlds
        File root = dimension.legacyId() == 0 ? parent.root() : new File(parent.root(), "DIM" + dimension.legacyId());
        JavaVersion worldVersion = this.options.get(SaveOptions.ACCESS) == WriteAccess.READ_ONLY
                                   ? null //allow chunks in read-only worlds to be decoded using any implemented version for performance
                                   : (JavaVersion) parent.version(); //force chunks in writable worlds to be upgraded to the world version
        this.storage = new AnvilWorldStorage(root, this, parent.chunkNBTOptions(), worldVersion);

        this.validateState();
    }

    @Override
    public WorldInfo info() {
        return this;
    }

    @Override
    public int layers() {
        return 1; //anvil only supports a single block layer
    }

    @Override
    public boolean hasSkyLight() {
        return this.dimension.hasSkyLight();
    }
}
