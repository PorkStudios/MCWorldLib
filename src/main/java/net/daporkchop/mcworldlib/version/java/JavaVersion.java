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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.primitive.map.IntObjMap;
import net.daporkchop.lib.primitive.map.open.IntObjOpenHashMap;
import net.daporkchop.mcworldlib.registry.Registries;
import net.daporkchop.mcworldlib.registry.java.JavaRegistries;
import net.daporkchop.mcworldlib.util.Util;
import net.daporkchop.mcworldlib.version.MinecraftEdition;
import net.daporkchop.mcworldlib.version.MinecraftVersion;

import java.util.HashMap;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * A version of Minecraft: Java Edition.
 *
 * @author DaPorkchop_
 */
@Getter
public class JavaVersion extends MinecraftVersion {
    private static final Map<String, JavaVersion> NAME_CACHE = new HashMap<>();
    private static final IntObjMap<JavaVersion> DATA_VERSION_CACHE = new IntObjOpenHashMap<>();

    static {
        JavaVersion[] versions = Util.parseJson(JavaVersion[].class, "versions.json");
        for (JavaVersion version : versions) {
            checkState(NAME_CACHE.putIfAbsent(version.name, version) == null, "duplicate version name: %s", version.name);

            //the list is sorted from newest to oldest, using putIfAbsent ensures that data versions shared by multiple game versions will always show up as the latest
            DATA_VERSION_CACHE.putIfAbsent(version.data, version);
        }
    }

    public static JavaVersion fromName(@NonNull String nameIn) {
        return NAME_CACHE.getOrDefault(nameIn, pre15w32a());
    }

    public static JavaVersion fromDataVersion(int dataVersion) {
        JavaVersion version = DATA_VERSION_CACHE.get(dataVersion);
        checkArg(version != null, "unsupported data version: %d", dataVersion);
        return version;
    }

    /**
     * @return the {@link JavaVersion} representing the latest version of the game supported by this library
     */
    public static JavaVersion latest() {
        return LatestVersion.LATEST;
    }

    /**
     * @return the {@link JavaVersion} used for all versions of Java edition prior to snapshot 15w32a, in which data versions were first added
     */
    public static JavaVersion pre15w32a() {
        return OldVersion.OLD;
    }

    protected final int data;
    protected final String toString;

    @JsonCreator
    protected JavaVersion(@JsonProperty("name") String name, @JsonProperty("protocol") int protocol, @JsonProperty("data") int data) {
        this(name, data, "Java Edition " + name);
    }

    protected JavaVersion(@NonNull String name, int data, @NonNull String toString) {
        super(MinecraftEdition.JAVA, name);

        this.data = data;
        this.toString = toString;
    }

    @Override
    public int compareTo(MinecraftVersion o) {
        if (o instanceof JavaVersion) {
            JavaVersion j = (JavaVersion) o;
            if (this.data != j.data) { //compare by data version rather than by name where possible
                return this.data - j.data;
            }
        }
        return super.compareTo(o);
    }

    @Override
    public Registries registries() {
        return JavaRegistries.forVersion(this);
    }

    @UtilityClass
    private static class OldVersion {
        private final JavaVersion OLD = new JavaVersion("[unknown] (≤ 1.8.9)", -1, -1);
    }

    @UtilityClass
    private static class LatestVersion {
        private final JavaVersion LATEST = fromName("1.16.1");
    }
}
