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

package net.daporkchop.mcworldlib.format.java.decoder;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.daporkchop.mcworldlib.tileentity.TileEntity;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.version.java.JavaVersion;
import net.daporkchop.mcworldlib.world.World;
import net.daporkchop.lib.nbt.tag.CompoundTag;

import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A function for decoding a tile entity from NBT data.
 *
 * @author DaPorkchop_
 */
@FunctionalInterface
public interface JavaTileEntityDecoder {
    /**
     * Decodes a tile entity.
     *
     * @param tag     the {@link CompoundTag} containing the tile entity data
     * @param version the version of the tile entity data
     * @param world   the {@link World} that the tile entity is in
     * @return the decoded tile entity
     */
    TileEntity decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world);

    @RequiredArgsConstructor
    @Accessors(fluent = true, chain = true)
    class Builder {
        @Setter
        @NonNull
        protected JavaTileEntityDecoder unknownDecoder;

        protected JavaVersion currentVersion;
        protected final Map<Identifier, JavaTileEntityDecoder> currentVersionDecoders = new IdentityHashMap<>();
        protected final Map<String, Identifier> aliases = new HashMap<>();

        protected final NavigableMap<JavaVersion, JavaTileEntityDecoder> result = new TreeMap<>();

        public Builder begin(@NonNull JavaVersion version) {
            if (this.currentVersion != null) {
                checkState(version.compareTo(this.currentVersion) > 0, "version (%s) must be greater than previous version (%s)", version, this.currentVersion);
                this.finishVersion();
            }
            this.currentVersion = version;
            return this;
        }

        public Builder putDecoder(@NonNull Identifier id, @NonNull JavaTileEntityDecoder decoder) {
            this.currentVersionDecoders.put(id, decoder);
            return this;
        }

        public Builder putAlias(@NonNull String from, @NonNull Identifier to) {
            this.aliases.put(from, to);
            return this;
        }

        public Builder purgeAliases() {
            this.aliases.clear();
            return this;
        }

        public NavigableMap<JavaVersion, JavaTileEntityDecoder> build() {
            if (this.currentVersion != null) {
                this.finishVersion();
            }
            return this.result;
        }

        protected void finishVersion() {
            this.result.put(this.currentVersion, new MappedDecoder(
                    this.aliases.isEmpty() ? Collections.emptyMap() : new HashMap<>(this.aliases),
                    new IdentityHashMap<>(this.currentVersionDecoders),
                    this.unknownDecoder));
        }

        @RequiredArgsConstructor
        protected static class MappedDecoder implements JavaTileEntityDecoder {
            @NonNull
            protected final Map<String, Identifier> aliases;
            @NonNull
            protected final Map<Identifier, JavaTileEntityDecoder> delegates;
            @NonNull
            protected final JavaTileEntityDecoder unknownDecoder;

            @Override
            public TileEntity decode(@NonNull CompoundTag tag, @NonNull JavaVersion version, @NonNull World world) {
                String idText = tag.getString("id", "unknown");
                Identifier id = this.aliases.get(idText);
                if (id == null) {
                    id = Identifier.fromString(idText);
                } else {
                    tag.putString("id", id.toString());
                }
                return this.delegates.getOrDefault(id, this.unknownDecoder).decode(tag, version, world);
            }
        }
    }
}
