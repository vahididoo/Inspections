package com.gwservices.inspections.gosu.scope;

import com.intellij.openapi.diagnostic.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.*;
import com.intellij.openapi.vfs.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.zip.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class GWProductManager {
    private static GWProductManager instance;
    private static Logger LOG = Logger.getInstance(GWProductManager.class.getCanonicalName());
    private static ZipFile zipFile;

    private GWProductManager() {

    }

    static class BaseEntry {
        String fileName;
        long fileSize;

        BaseEntry(String fileName, long size) {
            this.fileName = fileName;
            this.fileSize = size;

        }

        public BaseEntry(ZipEntry entry) {
            fileName = entry.getName();
            fileSize = entry.getSize();
        }
    }

    synchronized public static GWProductManager getInstance(Project project) {

        if (instance == null) {
            if (ProjectManager.getInstance().getOpenProjects().length > 0 && ProjectManager.getInstance()
                                                                                           .getOpenProjects()[0]
                    .isInitialized()) {
                String moduleFilePath = ModuleManager.getInstance(project).getModules()[0].getModuleFile().getParent
                        ().getParent().getCanonicalPath();
                String zipPath = moduleFilePath + File.separator + "base.zip";
                if (zipPath != null) {
                    try {
                        zipFile = new ZipFile(zipPath);
                    } catch (IOException e) {
                        LOG.error(e);
                    }
                }
                instance = new GWProductManager();
            }
        }
        return instance;
    }

    public boolean isClientFile(@NotNull VirtualFile file) {
        BaseEntry entry = getBaseEntry(file);
        return entry == null || file.getLength() > entry.fileSize;

    }

    private BaseEntry getBaseEntry(@NotNull VirtualFile file) {
        if (file != null) {
            Project project = ProjectManager.getInstance().getOpenProjects()[0];
            String path = ModuleManager.getInstance(project).getModules()[0].getModuleFile().getParent().getPath();
            String relativePath = "base/" + FileUtil.getRelativePath(path, file.getCanonicalPath(), '/');
            ZipEntry entry = zipFile.getEntry(relativePath);
            return entry != null ? new BaseEntry(entry) : null;
        }
        return null;
    }

    public long getBloatingFactor(@NotNull VirtualFile file) {

        BaseEntry entry = getBaseEntry(file);
        if (entry == null) return 100;
        long baseFileSize = entry.fileSize;
        long currentFileSize = file.getLength();
        if (baseFileSize > 0L && currentFileSize > baseFileSize) {
            return (long) (((float) (currentFileSize - baseFileSize) / baseFileSize) * 100);
        }
        return 0;
    }
}
