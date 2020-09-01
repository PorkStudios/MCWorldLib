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

package net.daporkchop.mcworldlib.world.common;

import net.daporkchop.lib.common.misc.refcount.RefCounted;
import net.daporkchop.mcworldlib.save.Save;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;
import net.daporkchop.mcworldlib.world.Chunk;
import net.daporkchop.mcworldlib.world.WorldInfo;

/**
 * Representation of a Minecraft world, consisting of {@link Chunk}s identified by their integer X, Z coordinates.
 * <p>
 * Worlds keep a reference to their {@link IWorldStorage} instance which is not released until the world is released.
 *
 * @author DaPorkchop_
 */
public interface IWorld<I extends IWorld, St extends IWorldStorage, S extends Save> extends RefCounted {
    /**
     * @return the {@link Save} that loaded this world
     */
    S parent();

    /**
     * @return the {@link Identifier} used to identify this world in its parent {@link Save}
     */
    Identifier id();

    /**
     * @return the {@link WorldInfo} which describes this world
     */
    WorldInfo info();

    /**
     * @return the {@link IWorldStorage} used for handling I/O of chunks and cubes
     */
    St storage();

    @Override
    int refCnt();

    @Override
    I retain() throws AlreadyReleasedException;

    @Override
    boolean release() throws AlreadyReleasedException;
}
