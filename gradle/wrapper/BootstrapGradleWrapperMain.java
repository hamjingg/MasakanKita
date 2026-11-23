package org.gradle.wrapper;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Tiny transparent bootstrap launcher for this educational project.
 * Downloads the official Gradle distribution URL declared in gradle-wrapper.properties,
 * extracts it to ~/.gradle/masakankita, then launches its gradle script.
 * This is not a copy of Gradle's official wrapper implementation.
 */
public final class BootstrapGradleWrapperMain {
    public static void main(String[] args) throws Exception {
        File project = new File(System.getProperty("user.dir"));
        File propertiesFile = new File(project, "gradle/wrapper/gradle-wrapper.properties");
        if (!propertiesFile.isFile()) {
            throw new FileNotFoundException("Run gradlew from the MasakanKita project folder");
        }
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(propertiesFile)) { props.load(in); }
        String distributionUrl = props.getProperty("distributionUrl");
        if (distributionUrl == null || !distributionUrl.startsWith("https://services.gradle.org/distributions/")) {
            throw new IOException("Unexpected Gradle distribution URL");
        }
        String distributionFile = distributionUrl.substring(distributionUrl.lastIndexOf('/') + 1);
        String folder = distributionFile.replace("-bin.zip", "").replace("-all.zip", "");
        File cache = new File(System.getProperty("user.home"), ".gradle/masakankita/" + folder);
        File install = new File(cache, folder);
        boolean windows = System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win");
        File script = new File(install, windows ? "bin/gradle.bat" : "bin/gradle");
        if (!script.isFile()) {
            if (!cache.isDirectory() && !cache.mkdirs()) {
                throw new IOException("Unable to create cache: " + cache);
            }
            File archive = new File(cache, distributionFile);
            if (!archive.isFile()) {
                File downloading = new File(cache, distributionFile + ".part");
                System.out.println("Downloading official Gradle distribution: " + distributionUrl);
                try (InputStream in = new URL(distributionUrl).openStream()) {
                    Files.copy(in, downloading.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                Files.move(downloading.toPath(), archive.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Extracting Gradle distribution...");
            Path root = cache.toPath().toAbsolutePath().normalize();
            try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)))) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    Path destination = root.resolve(entry.getName()).normalize();
                    if (!destination.startsWith(root)) throw new IOException("Invalid ZIP entry");
                    if (entry.isDirectory()) {
                        Files.createDirectories(destination);
                    } else {
                        Files.createDirectories(destination.getParent());
                        Files.copy(zip, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                    zip.closeEntry();
                }
            }
            if (!script.isFile()) throw new IOException("Gradle extraction incomplete: " + script);
            if (!windows) script.setExecutable(true);
            archive.delete();
        }
        List<String> cmd = new ArrayList<>();
        if (windows) {
            cmd.add("cmd.exe"); cmd.add("/c");
        }
        cmd.add(script.getAbsolutePath());
        cmd.addAll(Arrays.asList(args));
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        processBuilder.directory(project);
        processBuilder.inheritIO();
        int code = processBuilder.start().waitFor();
        System.exit(code);
    }
}
