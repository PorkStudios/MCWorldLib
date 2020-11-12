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

package net.daporkchop.mcworldlib.version.java;

import lombok.Getter;
import lombok.NonNull;
import net.daporkchop.lib.common.pool.handle.Handle;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.nbt.tag.CompoundTag;
import net.daporkchop.lib.nbt.tag.ListTag;
import net.daporkchop.mcworldlib.registry.DefaultRegistry;
import net.daporkchop.mcworldlib.registry.Registries;
import net.daporkchop.mcworldlib.registry.Registry;
import net.daporkchop.mcworldlib.registry.java.JavaRegistries;
import net.daporkchop.mcworldlib.util.Identifier;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author DaPorkchop_
 */
@Getter
public class ForgeVersion extends JavaVersion {
    public static JavaVersion extractForgeInformation(@NonNull JavaVersion vanilla, @NonNull CompoundTag levelDat) {
        CompoundTag fmlTag;
        if ((fmlTag = levelDat.getCompound("FML", null)) != null || (fmlTag = levelDat.getCompound("fml", null)) != null) {
            return new ForgeVersion(vanilla, extractModList(fmlTag), extractRegistries(fmlTag));
        }
        return vanilla;
    }

    protected static Map<String, String> extractModList(@NonNull CompoundTag fmlTag) {
        ListTag<CompoundTag> modListTag;
        if ((modListTag = fmlTag.getList("ModList", CompoundTag.class, null)) != null || (modListTag = fmlTag.getList("LoadingModList", CompoundTag.class, null)) != null) {
            return modListTag.stream().collect(Collectors.toMap(tag -> tag.getString("ModId"), tag -> tag.getString("ModVersion")));
        }
        throw new IllegalStateException("Unable to find Forge mod list in level.dat!");
    }

    protected static Registries extractRegistries(@NonNull CompoundTag fmlTag) {
        Map<Identifier, Registry> registries = new IdentityHashMap<>();
        fmlTag.getCompound("Registries").forEach((name, _registryTag) -> {
            Identifier id = Identifier.fromString(name);
            CompoundTag registryTag = (CompoundTag) _registryTag;
            DefaultRegistry.Builder builder = DefaultRegistry.builder(id);
            registryTag.getList("ids", CompoundTag.class).forEach(entryTag -> builder.register(Identifier.fromStringLenient(entryTag.getString("K")), entryTag.getInt("V")));
            //TODO: figure out what the "overrides", "blocked", "aliases" and "dummied" tags actually do
            registries.put(id, builder.build());
        });
        return new JavaRegistries(registries);
    }

    protected static String generateToString(JavaVersion vanilla, Map<String, String> mods) {
        try (Handle<StringBuilder> handle = PorkUtil.STRINGBUILDER_POOL.get()) {
            StringBuilder builder = handle.get();
            builder.setLength(0);
            builder.append("Java Edition ").append(vanilla.name()).append(" w. Forge [");

            if (!mods.isEmpty()) {
                mods.forEach((modid, version) -> builder.append(modid).append('@').append(version).append(", "));
                builder.setLength(builder.length() - 2);
            }
            builder.append(']');

            return builder.toString();
        }
    }

    protected final Map<String, String> mods;
    protected final Registries registries;

    protected ForgeVersion(JavaVersion vanilla, @NonNull Map<String, String> mods, @NonNull Registries registries) {
        super(vanilla.name(), vanilla.data(), generateToString(vanilla, mods));

        this.mods = Collections.unmodifiableMap(mods);
        this.registries = registries;
    }
}
