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

package map;

import net.daporkchop.lib.common.misc.file.PFiles;
import net.daporkchop.lib.common.misc.string.PStrings;
import net.daporkchop.lib.common.pool.array.ArrayAllocator;
import net.daporkchop.lib.common.ref.ReferenceType;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.vector.i.Vec2i;
import net.daporkchop.lib.math.vector.i.Vec3i;
import net.daporkchop.lib.unsafe.PUnsafe;
import net.daporkchop.mcworldlib.format.anvil.AnvilSaveFormat;
import net.daporkchop.mcworldlib.format.anvil.AnvilSaveOptions;
import net.daporkchop.mcworldlib.registry.Registry;
import net.daporkchop.mcworldlib.save.Save;
import net.daporkchop.mcworldlib.save.SaveOptions;
import net.daporkchop.mcworldlib.util.Identifier;
import net.daporkchop.mcworldlib.util.WriteAccess;
import net.daporkchop.mcworldlib.world.World;
import net.daporkchop.mcworldlib.world.section.FlattenedSection;
import net.daporkchop.mcworldlib.world.section.LegacySection;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.StreamSupport;

import static net.daporkchop.lib.common.math.PMath.*;

/**
 * @author DaPorkchop_
 */
public class Mapper {
    public static final File PATH = new File("/home/daporkchop/mcworlds/redstone");
    public static final File OUT_DIR2D = new File(PATH, "mapTiles2d");
    public static final File OUT_DIR3D = new File(PATH, "mapTiles3d");

    public static final boolean DEBUG_ALLOCATIONS = true;

    public static void main(String... args) throws IOException {
        if (PFiles.checkDirectoryExists(OUT_DIR2D)) {
            PFiles.rmContents(OUT_DIR2D);
        } else {
            PFiles.ensureDirectoryExists(OUT_DIR2D);
        }
        if (PFiles.checkDirectoryExists(OUT_DIR3D)) {
            PFiles.rmContents(OUT_DIR3D);
        } else {
            PFiles.ensureDirectoryExists(OUT_DIR3D);
        }

        Map<Vec2i, BufferedImage> chunks = new HashMap<>();
        Map<Vec3i, BufferedImage> sections = new HashMap<>();

        try (Save save = new AnvilSaveFormat().open(PATH, SaveOptions.builder()
                .set(SaveOptions.ACCESS, WriteAccess.READ_ONLY)
                .set(SaveOptions.BYTE_ALLOC, ArrayAllocator.pow2(length -> {
                    if (DEBUG_ALLOCATIONS) {
                        System.out.printf("Allocating byte[%d]\n", length);
                    }
                    return new byte[length];
                }, ReferenceType.SOFT, 32))
                .set(SaveOptions.INT_ALLOC, ArrayAllocator.pow2(length -> {
                    if (DEBUG_ALLOCATIONS) {
                        System.out.printf("Allocating int[%d]\n", length);
                    }
                    return new int[length];
                }, ReferenceType.SOFT, 32))
                .set(SaveOptions.LONG_ALLOC, ArrayAllocator.pow2(length -> {
                    if (DEBUG_ALLOCATIONS) {
                        System.out.printf("Allocating long[%d]\n", length);
                    }
                    return new long[length];
                }, ReferenceType.SOFT, 32))
                .set(AnvilSaveOptions.MMAP_REGIONS, true)
                .set(AnvilSaveOptions.CHUNK_CACHE_SIZE, 1)
                .build())) {
            try (World world = save.world(Identifier.fromString("minecraft:overworld"))) {
                System.out.printf("processed %d chunks\n", StreamSupport.stream(world.storage().allChunks(), false)
                        .peek(chunk -> {
                            BufferedImage img = chunks.computeIfAbsent(new Vec2i(chunk.x() >> 8, chunk.z() >> 8),
                                    pos -> new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB));
                            img.setRGB(chunk.x() & 0xFF, chunk.z() & 0xFF, 0xFFFFFFFF);
                        })
                        .count());

                System.out.printf("processed %d sections\n", StreamSupport.stream(world.storage().allSections(), false)
                        .peek(section -> {
                            BufferedImage img = sections.computeIfAbsent(new Vec3i(section.x() >> 4, section.y(), section.z() >> 4),
                                    pos -> new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB));
                            if (section instanceof LegacySection) {
                                LegacySection s = (LegacySection) section;
                                for (int x = 0; x < 16; x++) {
                                    for (int z = 0; z < 16; z++) {
                                        for (int y = 15; y >= 0; y--) {
                                            int id = s.getBlockLegacyId(x, y, z);
                                            if (id != 0) {
                                                int color = 0xFF000000 | mix32(id); //generate random color based on block ID
                                                img.setRGB(((section.x() & 0xF) << 4) + x, ((section.z() & 0xF) << 4) + z, color);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else if (section instanceof FlattenedSection) {
                                FlattenedSection s = (FlattenedSection) section;
                                Registry registry = s.version().registries().block();
                                for (int x = 0; x < 16; x++) {
                                    for (int z = 0; z < 16; z++) {
                                        for (int y = 15; y >= 0; y--) {
                                            int id = registry.get(s.getBlockState(x, y, z).id());
                                            if (id != 0) {
                                                int color = 0xFF000000 | mix32(id); //generate random color based on block ID
                                                img.setRGB(((section.x() & 0xF) << 4) + x, ((section.z() & 0xF) << 4) + z, color);
                                                break;
                                            }
                                        }
                                    }
                                }
                            } else {
                                throw new IllegalArgumentException(PorkUtil.className(section));
                            }
                        })
                        .count());
            }
        }

        chunks.forEach((pos, img) -> {
            File file = new File(OUT_DIR2D, PStrings.lightFormat("%s.%s.png", pos.getX(), pos.getY()));
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                PUnsafe.throwException(e);
            }
        });
        sections.forEach((pos, img) -> {
            File file = new File(OUT_DIR3D, PStrings.lightFormat("%s.%s.%s.png", pos.getX(), pos.getZ(), pos.getY()));
            try {
                ImageIO.write(img, "png", file);
            } catch (IOException e) {
                PUnsafe.throwException(e);
            }
        });
    }
}
