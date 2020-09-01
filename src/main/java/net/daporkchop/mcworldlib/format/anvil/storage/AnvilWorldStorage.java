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

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.binary.stream.DataIn;
import net.daporkchop.lib.common.math.BinMath;
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
import net.daporkchop.mcworldlib.format.anvil.AnvilSaveOptions;
import net.daporkchop.mcworldlib.format.anvil.AnvilWorld;
import net.daporkchop.mcworldlib.format.anvil.region.RawChunk;
import net.daporkchop.mcworldlib.format.anvil.region.RegionConstants;
import net.daporkchop.mcworldlib.format.anvil.region.RegionFile;
import net.daporkchop.mcworldlib.format.anvil.region.RegionFileCache;
import net.daporkchop.mcworldlib.format.java.JavaFixers;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.WriteAccess;
import net.daporkchop.mcworldlib.version.DataVersion;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.common.IChunk;
import net.daporkchop.mcworldlib.world.common.ISection;
import net.daporkchop.mcworldlib.world.common.IWorldStorage;
import net.daporkchop.lib.nbt.NBTFormat;
import net.daporkchop.lib.nbt.NBTOptions;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.primitive.lambda.LongObjObjFunction;
import net.daporkchop.lib.primitive.map.LongObjMap;
import net.daporkchop.lib.primitive.map.concurrent.LongObjConcurrentHashMap;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;
import java.util.concurrent.Executor;

/**
 * Implementation of {@link IWorldStorage} for the Anvil save format.
 * <p>
 * Since Anvil compresses sections and chunks together, but the {@link IWorldStorage} interface requires chunks and sections to be handled individually,
 * this interface uses a more complex caching mechanism to improve performance and reduce allocations.
 *
 * @author DaPorkchop_
 */
public class AnvilWorldStorage extends AbstractRefCounted implements IWorldStorage {
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

    protected final LongObjMap<AnvilCachedChunk> cachedChunks = new LongObjConcurrentHashMap<>();
    protected final RegionFile regionCache;

    protected final boolean readOnly;

    protected final SaveOptions options;
    protected final JavaFixers fixers;
    protected final NBTOptions nbtOptions;
    protected final Executor ioExecutor;
    protected final AnvilWorld world;

    @Getter
    protected final JavaVersion worldVersion;
    protected final File root;

    public AnvilWorldStorage(@NonNull File root, @NonNull AnvilWorld world, @NonNull NBTOptions nbtOptions, JavaVersion worldVersion) {
        this.root = PFiles.ensureDirectoryExists(root);
        this.regionCache = new RegionFileCache(world.options(), new File(root, "region"));

        this.options = world.options();
        this.readOnly = this.options.get(SaveOptions.ACCESS) == WriteAccess.READ_ONLY;
        this.fixers = this.options.get(AnvilSaveOptions.FIXERS);
        this.ioExecutor = this.options.get(SaveOptions.IO_EXECUTOR);
        this.nbtOptions = nbtOptions;
        this.worldVersion = worldVersion;
        this.world = world;
    }

    @Override
    public IChunk loadChunk(int _x, int _z) throws IOException {
        IChunk[] ref = new IChunk[1]; //TODO: reuse this
        this.cachedChunks.compute(BinMath.packXY(_x, _z), (ChunkUpdater) (x, z, cached) -> {
            if (cached == null) {
                cached = this.load(x, z);
            }
            ref[0] = cached.chunk();
            return cached;
        });
        return ref[0];
    }

    @Override
    public ISection loadSection(int _x, int _y, int _z) throws IOException {
        ISection[] ref = new ISection[1]; //TODO: reuse this
        this.cachedChunks.compute(BinMath.packXY(_x, _z), (ChunkUpdater) (x, z, cached) -> {
            if (cached == null) {
                cached = this.load(x, z);
            }
            ref[0] = cached.section(_y);
            return cached;
        });
        return ref[0];
    }

    @Override
    public PFuture<IChunk> loadChunkAsync(int x, int z) {
        return PFutures.computeThrowableAsync(() -> this.loadChunk(x, z), this.ioExecutor);
    }

    @Override
    public PFuture<ISection> loadSectionAsync(int x, int y, int z) {
        return PFutures.computeThrowableAsync(() -> this.loadSection(x, y, z), this.ioExecutor);
    }

    @Override
    public void save(@NonNull Iterable<IChunk> chunks, @NonNull Iterable<ISection> sections) throws IOException {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public PFuture<Void> saveAsync(@NonNull Iterable<IChunk> chunks, @NonNull Iterable<ISection> sections) {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public PFuture<Void> flushAsync() {
        return PFutures.successful(null, this.ioExecutor);
    }

    @Override
    public Spliterator<IChunk> allChunks() throws IOException {
        return this.readOnly && this.options.get(SaveOptions.SPLITERATOR_CACHE)
        ? new CachedAnvilSpliterator.OfChunk(this)
        : new UncachedAnvilSpliterator.OfChunk(this);
    }

    @Override
    public Spliterator<ISection> allSections() throws IOException {
        return this.readOnly && this.options.get(SaveOptions.SPLITERATOR_CACHE)
               ? new CachedAnvilSpliterator.OfSection(this)
               : new UncachedAnvilSpliterator.OfSection(this);
    }

    @Override
    public IWorldStorage retain() throws AlreadyReleasedException {
        super.retain();
        return this;
    }

    @Override
    protected void doRelease() {
        try {
            this.flush();
            this.cachedChunks.forEach((l, cached) -> cached.release());
            this.cachedChunks.clear();
            this.regionCache.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    protected File[] listRegions() {
        return this.regionCache.file().listFiles(f -> f.isFile() && RegionConstants.REGION_PATTERN.matcher(f.getName()).matches());
    }

    /**
     * Loads an entire chunk from disk for caching.
     * <p>
     * If the world is open in read-write mode, all data stored in the chunk will also be upgraded to the world version.
     *
     * @param x the X coordinate of the chunk to cache
     * @param z the Z coordinate of the chunk to cache
     * @return the cached chunk, or {@code null} if the chunk doesn't exist
     */
    protected AnvilCachedChunk load(int x, int z) throws IOException {
        return this.load(this.regionCache, x, z);
    }

    protected boolean prefetch(int x, int z) throws IOException  {
        return this.cachedChunks.computeIfAbsent(BinMath.packXY(x, z), l -> {
            try {
                return this.load(BinMath.unpackX(l), BinMath.unpackY(l));
            } catch (IOException e) {
                PUnsafe.throwException(e);
                throw new RuntimeException(e); //unreachable
            }
        }) != null;
    }

    protected AnvilCachedChunk load(@NonNull RegionFile region, int x, int z) throws IOException {
        CompoundTag tag = null;
        try {
            ByteBuf uncompressed = null;
            try {
                try (RawChunk chunk = region.read(x, z)) {
                    if (chunk == null) { //chunk doesn't exist on disk
                        return null;
                    }
                    uncompressed = this.options.get(SaveOptions.NETTY_ALLOC).ioBuffer(1 << 18); //256 KiB
                    try (Handle<PInflater> handle = inflater(chunk.data().readByte() & 0xFF)) {
                        handle.get().decompress(chunk.data(), uncompressed);
                    }
                } //release compressed chunk data before parsing NBT
                tag = NBTFormat.BIG_ENDIAN.readCompound(DataIn.wrap(uncompressed, false), this.nbtOptions);
            } finally { //release uncompressed chunk data before constructing chunk instance
                if (uncompressed != null) {
                    uncompressed.release();
                }
            }
            int dataVersion = tag.getInt("DataVersion", 0);
            JavaVersion version = dataVersion < DataVersion.DATA_15w32a ? JavaVersion.pre15w32a() : JavaVersion.fromDataVersion(dataVersion);
            return this.readOnly
                   ? new AnvilCachedChunk.ReadOnly(tag, version, this.fixers, this.world)
                   : null; //TODO
        } finally {
            if (tag != null) {
                tag.release();
            }
        }
    }

    public interface ChunkUpdater extends LongObjObjFunction<AnvilCachedChunk, AnvilCachedChunk> {
        @Override
        default AnvilCachedChunk apply(long l, AnvilCachedChunk chunk) {
            try {
                return this.applyThrowing(BinMath.unpackX(l), BinMath.unpackY(l), chunk);
            } catch (IOException e) {
                PUnsafe.throwException(e);
                throw new RuntimeException(e);
            }
        }

        AnvilCachedChunk applyThrowing(int x, int z, AnvilCachedChunk chunk) throws IOException;
    }
}
