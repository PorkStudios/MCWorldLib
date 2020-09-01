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

package net.daporkchop.mcworldlib.format.anvil.storage;

import lombok.NonNull;
import net.daporkchop.mcworldlib.format.java.JavaFixers;
import net.daporkchop.mcworldlib.format.java.decoder.JavaSectionDecoder;
import net.daporkchop.mcworldlib.format.java.decoder.JavaTileEntityDecoder;
import net.daporkchop.mcworldlib.tileentity.TileEntity;
import net.daporkchop.mcworldlib.util.dirty.AbstractReleasableDirtiable;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.Chunk;
import net.daporkchop.mcworldlib.world.common.ISection;
import net.daporkchop.mcworldlib.world.common.IWorld;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * In-memory representation of a chunk cached by {@link AnvilWorldStorage}.
 * <p>
 * This class is thread-safe.
 *
 * @author DaPorkchop_
 */
public abstract class AnvilCachedChunk extends AbstractReleasableDirtiable {
    public abstract Chunk chunk();

    public abstract ISection section(int y);

    public static class ReadOnly extends AnvilCachedChunk {
        protected final Chunk chunk;
        protected final ISection[] sections = new ISection[16];

        public ReadOnly(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull JavaFixers fixers, @NonNull IWorld world) {
            this.chunk = fixers.chunk().ceilingEntry(version).getValue()
                    .decode(tag, version, world);

            CompoundTag levelTag = tag.getCompound("Level");

            JavaSectionDecoder sectionDecoder = fixers.section().ceilingEntry(version).getValue();
            for (CompoundTag sectionTag : levelTag.getList("Sections", CompoundTag.class)) {
                ISection section = sectionDecoder.decode(sectionTag, version, world, this.chunk.x(), this.chunk.z());
                checkState(this.sections[section.y()] == null, "duplicate section at y=%d!", section.y());
                this.sections[section.y()] = section;
            }

            JavaTileEntityDecoder tileEntityDecoder = fixers.tileEntity().ceilingEntry(version).getValue();
            for (CompoundTag tileEntityTag : levelTag.getList("TileEntities", CompoundTag.class)) {
                int x = tileEntityTag.getInt("x");
                int y = tileEntityTag.getInt("y");
                int z = tileEntityTag.getInt("z");
                TileEntity tileEntity = tileEntityDecoder.decode(tileEntityTag, version, world);
                this.sections[y >> 4].setTileEntity(x & 0xF, y & 0xF, z & 0xF, tileEntity);
            }
        }

        @Override
        public Chunk chunk() {
            return this.chunk.retain();
        }

        @Override
        public ISection section(int y) {
            ISection section = this.sections[y];
            return section != null ? section.retain() : null;
        }

        @Override
        protected void doRelease() {
            this.chunk.release();
            for (ISection section : this.sections) {
                if (section != null) {
                    section.release();
                }
            }
        }
    }

    //TODO
    /*public static class ReadWrite extends AnvilCachedChunk {
        protected final JavaVersion version;

        protected CompoundTag chunkTag;
        protected final CompoundTag[] sectionTags = new CompoundTag[16];
        protected final CompoundTag[][] sectionBlockEntityTags = new CompoundTag[16][];

        public ReadWrite(@NonNull JavaVersion version) {
            super(version);
        }

        @Override
        protected void doRelease() {
            this.chunkTag.release();
            for (CompoundTag sectionTag : this.sectionTags) {
                if (sectionTag != null) {
                    sectionTag.release();
                }
            }
            for (CompoundTag[] blockEntityTags : this.sectionBlockEntityTags) {
                if (blockEntityTags != null) {
                    for (CompoundTag blockEntityTag : blockEntityTags) {
                        blockEntityTag.release();
                    }
                }
            }
        }
    }*/
}
