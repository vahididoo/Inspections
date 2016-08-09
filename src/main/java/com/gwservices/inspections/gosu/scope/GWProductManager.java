package com.gwservices.inspections.gosu.scope;

import com.intellij.openapi.application.*;
import com.intellij.openapi.diagnostic.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import org.apache.commons.csv.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Created by vmansoori on 8/3/2016.
 */
public class GWProductManager {
    public static final String PRODUCT_PROPERTIES = "product.properties";
    public static final String BASE_MODULE_NAME = "configuration";
    private static GWProductManager instance;
    private static Map<String, BaseEntry> baseEntriesCache;
    private static Logger LOG = Logger.getInstance(GWProductManager.class.getCanonicalName());
    private static boolean initialized;
    private String code;
    private String majorVersion;
    private String minorVersion;
    private String patchVersion;

    private GWProductManager(String code, String majorVersion, String minorVersion, String patchVersion) {
        this.code = code;
        this.majorVersion = majorVersion;
        this.minorVersion = minorVersion;
        this.patchVersion = patchVersion;
        initializeBaseFileSet();
    }

    static class BaseEntry {
        String fileName;
        long fileSize;

        BaseEntry(String fileName, long size) {
            this.fileName = fileName;
            this.fileSize = size;

        }
    }

    synchronized public static GWProductManager getInstance(Project project) {

        if (instance == null) {
            ApplicationManager.getApplication().runReadAction(() -> {
                PsiFile[] filesByName = FilenameIndex.getFilesByName(project, PRODUCT_PROPERTIES, GlobalSearchScope
                        .projectScope(project));

                PsiFile productProperties = null;
                if (filesByName.length > 0) {
                    productProperties = filesByName[0];
                    Properties prop = new Properties();
                    try {
                        prop.load(productProperties.getVirtualFile().getInputStream());
                        String productCode = prop.getProperty("product.code");
                        String majorVersion1 = prop.getProperty("product.majorversion");
                        String minorVersion1 = prop.getProperty("product.minorversion");
                        String patchVersion1 = prop.getProperty("product.patchversion");
                        instance = new GWProductManager(productCode, majorVersion1, minorVersion1, patchVersion1);

                    } catch (IOException e) {
                        LOG.error(e);
                    }

                }
            });
        }
        return instance;
    }

    @Override
    public String toString() {
        return code + majorVersion + "." + minorVersion + "." + patchVersion;
    }

    private URL getManifestFile() {

        return this.getClass().getClassLoader().getResource("/manifests/" + code + "/" + majorVersion + "/" +
                minorVersion + "/" + patchVersion + "/manifest.csv");
    }

    synchronized private void initializeBaseFileSet() {

        baseEntriesCache = new HashMap<>();
        try {
            URL resource = getManifestFile();
            FileInputStream inputStream = new FileInputStream(resource.getFile());
            InputStreamReader reader = new InputStreamReader(inputStream);
            CSVParser records = CSVFormat.DEFAULT.parse(reader);
            for (CSVRecord record : records) {
                baseEntriesCache.put(record.get(0), new BaseEntry(record.get(0), Long.parseLong(record.get(1))));
            }
            this.initialized = true;
        } catch (IOException e) {
            LOG.error(e);
        }

    }

    private BaseEntry getEntryForFile(VirtualFile file) {
        if (file.getCanonicalPath() != null) {
            Module module = ModuleUtil.findModuleForFile(file, ProjectUtil.guessProjectForFile(file));
            String relativePath = FileUtil.getRelativePath(module.getModuleFilePath(), file.getCanonicalPath(), '/')
                                          .replace("../", "");
            return baseEntriesCache.get(relativePath);
        }
        return null;
    }

    public boolean isClientFile(@NotNull VirtualFile file) {
        BaseEntry entry = getEntryForFile(file);
        return entry == null || file.getLength() > entry.fileSize;

    }

    public long getBloatingFactor(@NotNull VirtualFile file) {

        BaseEntry entry = getEntryForFile(file);
        if (entry == null) return 100;
        long baseFileSize = entry.fileSize;
        long currentFileSize = file.getLength();
        if (baseFileSize > 0L && currentFileSize > baseFileSize) {
            return (long) (((float) (currentFileSize - baseFileSize) / baseFileSize) * 100);
        }
        return 0;
    }

}
