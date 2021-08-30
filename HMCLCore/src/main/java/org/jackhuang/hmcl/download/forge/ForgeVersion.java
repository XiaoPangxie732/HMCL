/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2020  huangyuhui <huanghongxun2008@126.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.jackhuang.hmcl.download.forge;

import org.jackhuang.hmcl.util.Immutable;

/**
 *
 * @author huangyuhui
 */
@Immutable
public final class ForgeVersion {

    private final String mcversion;
    private final String version;
    private final String extra;

    public ForgeVersion(String mcversion, String version) {
        this(mcversion, version, null);
    }

    public ForgeVersion(String mcversion, String version, String extra) {
        this.mcversion = mcversion;
        this.version = version;
        this.extra = extra;
    }

    public String getGameVersion() {
        return mcversion;
    }

    public String getVersion() {
        return version;
    }

    public String getExtra() {
        return extra;
    }

}