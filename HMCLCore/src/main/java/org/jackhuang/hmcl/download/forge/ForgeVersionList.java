/*
 * Hello Minecraft! Launcher
 * Copyright (C) 2021  huangyuhui <huanghongxun2008@126.com> and contributors
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

import org.jackhuang.hmcl.download.DownloadProvider;
import org.jackhuang.hmcl.download.VersionList;
import org.jackhuang.hmcl.util.StringUtils;
import org.jackhuang.hmcl.util.io.HttpRequest;
import org.jackhuang.hmcl.util.versioning.VersionNumber;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author huangyuhui
 */
public final class ForgeVersionList extends VersionList<ForgeRemoteVersion> {
    private final DownloadProvider downloadProvider;

    public ForgeVersionList(DownloadProvider downloadProvider) {
        this.downloadProvider = downloadProvider;
    }

    @Override
    public boolean hasType() {
        return false;
    }

    @Override
    public CompletableFuture<?> refreshAsync() {
        return HttpRequest.GET(downloadProvider.injectURL(FORGE_LIST)).getStringAsync()
                .thenAcceptAsync(data -> {
                    lock.writeLock().lock();

                    try {
                        List<ForgeVersion> versionList = new ArrayList<>();
                        try {
                            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder builder = factory.newDocumentBuilder();
                            Document doc = builder.parse(new InputSource(new StringReader(data)));
                            NodeList versions = doc.getDocumentElement().getElementsByTagName("version");
                            for (int i = 0; i < versions.getLength(); i++) {
                                String version = versions.item(i).getTextContent();
                                int index = version.indexOf('-');
                                String mcversion = version.substring(0, index);
                                int extra = version.lastIndexOf('-');
                                if (index == extra) {
                                    versionList.add(new ForgeVersion(mcversion, version.substring(index + 1)));
                                } else {
                                    versionList.add(new ForgeVersion(mcversion, version.substring(index + 1, extra), version.substring(extra + 1)));
                                }
                            }
                        } catch (Exception ignore) {
                        }
                        if (versionList.isEmpty())
                            return;
                        versions.clear();

                        for (ForgeVersion version : versionList) {
                            String gameVersion = VersionNumber.normalize(version.getGameVersion());
                            String selfVersion = version.getVersion();
                            String artifactVersion = gameVersion + '-' + selfVersion +
                                    (version.getExtra() == null ? "" : '-' + version.getExtra());
                            String jar = FORGE_URL_BASE + artifactVersion + "/forge-" + artifactVersion + "-installer.jar";
                            versions.put(gameVersion, new ForgeRemoteVersion(
                                    gameVersion, selfVersion, null, Collections.singletonList(jar)
                            ));
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                });
    }

    public static final String FORGE_URL_BASE = "https://maven.minecraftforge.net/net/minecraftforge/forge/";
    public static final String FORGE_LIST = FORGE_URL_BASE + "maven-metadata.xml";
}
