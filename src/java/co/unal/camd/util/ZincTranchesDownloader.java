package co.unal.camd.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class ZincTranchesDownloader {

    private static Pattern FILE_PATTERN = Pattern.compile(".*\\/(.*\\/.*smi)");
    private String tranchesType;
    private File tranchesUrlsFile;
    private File reportFile;

    public ZincTranchesDownloader(String urlsPath) {
        String filePath = ZincTranchesDownloader.class.getResource(urlsPath).getFile();

        System.out.println(filePath);
        tranchesUrlsFile = new File(filePath);
        tranchesType = tranchesUrlsFile.getParentFile().getName();

        try {
            reportFile = new File("tranches/" + tranchesType + ".report.info");
            FileUtils.write(reportFile, "Failed Tranches\n");
        } catch (Exception e) {
            System.out.println("PROBLEMS WRITING REPORT FILE " + reportFile.getName());
            e.printStackTrace();
        }
    }

    private void downloadTranches() throws IOException {
        FileUtils.readLines(tranchesUrlsFile).forEach(this::downloadTranche);
    }

    private void downloadTranche(String url) {
        System.out.println("Downloading trance " + url);
        try {
            Matcher fileMatcher = FILE_PATTERN.matcher(url);
            fileMatcher.find();
            String locationSuffix = fileMatcher.group(1);
            System.out.println(locationSuffix);
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File trancheFile = new File("tranches/" + tranchesType + "/" + locationSuffix);
            FileUtils.touch(trancheFile);
            FileOutputStream fos = new FileOutputStream(trancheFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Exception e) {
            try {
                Files.write(reportFile.toPath(), (url + "\n").getBytes(StandardCharsets.UTF_8), APPEND, CREATE);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    enum TrancheFile {
        TESTS("/tranches-urls/test/ZINC-downloader-2D-smi.uri"),
        WAIT_OK("/tranches-urls/wait-ok/ZINC-maxWaitOk-max350da-maxReact-smi.uri"),
        BOUTIQUE("/tranches-urls/boutique/ZINC-onlyBoutique-max350da-maxReact-smi.uri"),
        ANNOTATED("/tranches-urls/annotated/ZINC-onlyAnnotated-max350da-maxReact-smi.uri");

        private final String path;

        TrancheFile(String path) {
            this.path = path;
        }
    }

    public static void main(String... args) throws IOException {
        String urlsPath = TrancheFile.TESTS.path;
        ZincTranchesDownloader zincTranchesDownloader = new ZincTranchesDownloader(urlsPath);
        zincTranchesDownloader.downloadTranches();
    }

}
