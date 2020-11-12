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

package net.daporkchop.mcworldlib.format.java.storage;

import lombok.NonNull;
import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.pool.handle.HandledPool;
import net.daporkchop.lib.compression.context.PInflater;
import net.daporkchop.lib.compression.zlib.Zlib;
import net.daporkchop.lib.compression.zlib.ZlibMode;
import net.daporkchop.lib.compression.zlib.options.ZlibInflaterOptions;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.format.anvil.AnvilSaveOptions;
import net.daporkchop.mcworldlib.format.anvil.region.RegionConstants;
import net.daporkchop.mcworldlib.format.anvil.region.RegionFileCache;
import net.daporkchop.mcworldlib.format.anvil.world.AbstractAnvilWorld;
import net.daporkchop.mcworldlib.format.java.JavaFixers;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.WriteAccess;
import net.daporkchop.mcworldlib.world.Chunk;
import net.daporkchop.mcworldlib.world.WorldStorage;
import net.daporkchop.mcworldlib.world.section.Section;

import java.io.File;
import java.util.concurrent.Executor;

/**
 * @author DaPorkchop_
 */
public abstract class AbstractJavaWorldStorage extends AbstractRefCounted implements WorldStorage {
    protected static final ZlibInflaterOptions INFLATER_OPTIONS = Zlib.PROVIDER.inflateOptions().withMode(ZlibMode.AUTO);
    protected static final HandledPool<PInflater> INFLATER_CACHE = HandledPool.threadLocal(() -> Zlib.PROVIDER.inflater(INFLATER_OPTIONS), 1);

    protected static Handle<PInflater> inflater(int version) {
        switch (version) {
            case RegionConstants.ID_ZLIB: //by far the more common one
            case RegionConstants.ID_GZIP:
                return INFLATER_CACHE.get();
        }
        throw new IllegalArgumentException("Unknown compression version: " + version);
    }

    protected final SaveOptions options;
    protected final JavaFixers fixers;
    protected final NBTOptions nbtOptions;
    protected final Executor ioExecutor;
    protected final AbstractAnvilWorld world;

    protected final File root;

    protected final boolean readOnly;

    public AbstractJavaWorldStorage(@NonNull File root, @NonNull AbstractAnvilWorld world) {
        this.root = PFiles.ensureDirectoryExists(root);

        this.options = world.options();
        this.readOnly = this.options.get(SaveOptions.ACCESS) == WriteAccess.READ_ONLY;
        this.fixers = this.options.get(AnvilSaveOptions.FIXERS);
        this.ioExecutor = this.options.get(SaveOptions.IO_EXECUTOR);
        this.nbtOptions = world.parent().chunkNBTOptions();
        this.world = world;

        if (!this.readOnly) {
            throw new UnsupportedOperationException("Anvil read/write mode is not implemented!");
        }
    }

    @Override
    public PFuture<Chunk> loadChunkAsync(int x, int z) {
        return PFutures.computeThrowableAsync(() -> this.loadChunk(x, z), this.ioExecutor);
    }

    @Override
    public PFuture<Section> loadSectionAsync(int x, int y, int z) {
        return PFutures.computeThrowableAsync(() -> this.loadSection(x, y, z), this.ioExecutor);
    }

    @Override
    public WorldStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }
}
