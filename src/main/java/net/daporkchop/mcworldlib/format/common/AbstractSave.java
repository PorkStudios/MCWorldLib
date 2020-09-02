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

package net.daporkchop.mcworldlib.format.common;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.daporkchop.lib.common.misc.refcount.AbstractRefCounted;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.registry.Registries;
import net.daporkchop.mcworldlib.save.Save;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.version.MinecraftVersion;
import net.daporkchop.mcworldlib.world.Dimension;
import net.daporkchop.mcworldlib.world.common.IWorld;
import net.daporkchop.lib.unsafe.util.exception.AlreadyReleasedException;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static net.daporkchop.lib.common.util.PValidation.*;
import static net.daporkchop.lib.common.util.PorkUtil.*;

/**
 * Base implementation of {@link Save}.
 *
 * @author DaPorkchop_
 */
@RequiredArgsConstructor
@Getter
public abstract class AbstractSave<V extends MinecraftVersion, I extends Save, W extends IWorld<W, ?, I>> extends AbstractRefCounted implements Save<I, W> {
    @NonNull
    protected final SaveOptions options;
    @NonNull
    protected final File root;

    protected final Map<Identifier, W> worlds = new HashMap<>();
    protected final Set<Identifier> worldIds = Collections.unmodifiableSet(this.worlds.keySet());
    protected V version;

    /**
     * Ensures that the implementation constructor has initialized all the required fields.
     */
    protected void validateState() {
        checkState(this.version != null, "version must be set!");
    }

    protected void putWorld(@NonNull Dimension dimension) {
        this.worlds.put(dimension.id(), this.openWorld(dimension));
    }

    protected abstract W openWorld(@NonNull Dimension dimension);

    @Override
    public Stream<W> worlds() {
        return this.worlds.values().stream();
    }

    @Override
    public W world(@NonNull Identifier id) {
        W world = this.worlds.get(id);
        checkArg(world != null, id);
        return uncheckedCast(world.retain());
    }

    @Override
    public I retain() throws AlreadyReleasedException {
        super.retain();
        return uncheckedCast(this);
    }

    @Override
    protected void doRelease() {
        this.worlds.values().forEach(IWorld::release);
        this.worlds.clear();
    }
}
