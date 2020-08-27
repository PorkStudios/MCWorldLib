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

package net.daporkchop.mcworldlib.block.java;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.daporkchop.lib.common.function.io.IOFunction;
import net.daporkchop.lib.primitive.map.concurrent.ObjObjConcurrentHashMap;
import net.daporkchop.mcworldlib.block.BlockRegistry;
import net.daporkchop.mcworldlib.block.FluidRegistry;
import net.daporkchop.mcworldlib.block.RegistryConverter;
import net.daporkchop.mcworldlib.block.common.GlobalRegistryConverter;
import net.daporkchop.mcworldlib.block.common.NoopFluidRegistry;
import net.daporkchop.mcworldlib.block.common.json.JsonBlockRegistry;
import net.daporkchop.mcworldlib.registry.java.JavaRegistries;
import net.daporkchop.mcworldlib.version.DataVersion;
import net.daporkchop.mcworldlib.version.java.JavaVersion;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static net.daporkchop.lib.common.util.PValidation.*;

/**
 * @author DaPorkchop_
 */
public class JavaBlockRegistry extends JsonBlockRegistry {
    private static final Map<String, BlockRegistry> CACHE = new ObjObjConcurrentHashMap<>(); //this has a faster computeIfAbsent implementation

    public static BlockRegistry forVersion(@NonNull JavaVersion versionIn) {
        if (versionIn.data() < DataVersion.DATA_1_12_2) {
            versionIn = JavaVersion.fromName("1.12.2"); //1.12.2 is used as an intermediate translation point for all legacy versions
        }
        return CACHE.computeIfAbsent(versionIn.name(), (IOFunction<String, BlockRegistry>) version -> {
            try (InputStream in = JavaBlockRegistry.class.getResourceAsStream(version + ".json")) {
                checkArg(in != null, "no registry stored for version: %s", version);
                return new JavaBlockRegistry(in, JavaVersion.fromName(version));
            }
        });
    }

    public static BlockRegistry latest() {
        return Latest.LATEST;
    }

    private static RegistryConverter converter(@NonNull JavaVersion version) {
        if (version == JavaVersion.latest()) {
            return new GlobalRegistryConverter();
        }
        //TODO: make registry converter
        return new GlobalRegistryConverter();
    }

    private static FluidRegistry fluidRegistry(@NonNull JavaVersion version) {
        //TODO: implement this
        return new NoopFluidRegistry();
    }

    @Getter
    protected final RegistryConverter toGlobal;
    @Getter
    protected final FluidRegistry fluids;

    protected JavaBlockRegistry(@NonNull InputStream in, @NonNull JavaVersion version) throws IOException {
        super(in, JavaRegistries.forVersion(version).get(BlockRegistry.ID));

        this.toGlobal = converter(version);
        this.fluids = fluidRegistry(version);
    }

    @UtilityClass
    private static final class Latest {
        private final BlockRegistry LATEST = forVersion(JavaVersion.latest());
    }
}
