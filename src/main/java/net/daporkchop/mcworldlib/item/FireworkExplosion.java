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

package net.daporkchop.mcworldlib.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.awt.Color;
import java.util.List;

/**
 * Representation of a firework explosion.
 *
 * @author DaPorkchop_
 */
@ToString
@Getter
@Setter
@Accessors(fluent = true, chain = true)
public class FireworkExplosion {
    /**
     * Whether or not this firework explosion has the "flicker" effect.
     */
    protected boolean flicker = false;

    /**
     * Whether or not this firework explosion has the "trail" effect.
     */
    protected boolean trail = false;

    /**
     * The type of firework explosion effect to use.
     */
    protected int type = 0;

    /**
     * The primary colors of this firework explosion.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * {@code null} elements will be ignored.
     * <p>
     * Every color's alpha channel will be ignored.
     */
    protected List<Color> colors = null;

    /**
     * The fade colors of this firework explosion.
     * <p>
     * {@code null} values will be treated as unset.
     * <p>
     * {@code null} elements will be ignored.
     * <p>
     * Every color's alpha channel will be ignored.
     */
    protected List<Color> fadeColors = null;
}
