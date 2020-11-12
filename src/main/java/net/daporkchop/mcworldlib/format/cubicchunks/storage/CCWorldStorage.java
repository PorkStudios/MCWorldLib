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

package net.daporkchop.mcworldlib.format.cubicchunks.storage;

import cubicchunks.regionlib.impl.SaveCubeColumns;
import lombok.NonNull;
import net.daporkchop.lib.concurrent.PFuture;
import net.daporkchop.lib.concurrent.PFutures;
import net.daporkchop.mcworldlib.format.anvil.world.AbstractAnvilWorld;
import net.daporkchop.mcworldlib.format.java.storage.AbstractJavaWorldStorage;
import net.daporkchop.mcworldlib.world.Chunk;
import net.daporkchop.mcworldlib.world.section.Section;

import java.io.File;
import java.io.IOException;
import java.util.Spliterator;

/**
 * @author DaPorkchop_
 */
public class CCWorldStorage extends AbstractJavaWorldStorage {
    protected final SaveCubeColumns delegate;

    public CCWorldStorage(@NonNull File root, @NonNull AbstractAnvilWorld world) {
        super(root, world);

        try {
            this.delegate = SaveCubeColumns.create(root.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new UnsupportedOperationException("Cubic Chunks worlds are not supported yet!");
    }

    @Override
    public Chunk loadChunk(int x, int z) throws IOException {
        return null;
    }

    @Override
    public Section loadSection(int x, int y, int z) throws IOException {
        return null;
    }

    @Override
    public void save(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections) throws IOException {
        throw new UnsupportedOperationException(); //TODO
    }

    @Override
    public PFuture<Void> saveAsync(@NonNull Iterable<Chunk> chunks, @NonNull Iterable<Section> sections) {
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
    public Spliterator<Chunk> allChunks() throws IOException {
        return null;
    }

    @Override
    public Spliterator<Section> allSections() throws IOException {
        return null;
    }

    @Override
    protected void doRelease() {
        try {
            this.flush();
            this.delegate.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
