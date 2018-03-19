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

public class TranchesDownloader {

    private static Pattern FILE_PATTERN = Pattern.compile(".*\\/(.*\\/.*smi)");
    private String tranchesType;
    private File tranchesUrlsFile;
    private File reportFile;
    private String urlsPath;

    public TranchesDownloader(String urlsPath) {
        this.urlsPath = urlsPath;
        String filePath = TranchesDownloader.class.getResource(urlsPath).getFile();

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

    public static void main(String... args) throws IOException {
        /*TESTS*/
        //        String urlsPath = "/tranches-urls/test/ZINC-downloader-2D-smi.uri";
        /*WAIT OK*/
        //        String urlsPath = "/tranches-urls/wait-ok/ZINC-maxWaitOk-max350da-maxReact-smi.uri";
        /*BOUTIQUE*/
        //        String urlsPath = "/tranches-urls/boutique/ZINC-onlyBoutique-max350da-maxReact-smi.uri";
        /*ANNOTATED*/
        String urlsPath = "/tranches-urls/annotated/ZINC-onlyAnnotated-max350da-maxReact-smi.uri";

        TranchesDownloader tranchesDownloader = new TranchesDownloader(urlsPath);
        tranchesDownloader.downloadTranches();
    }

    private void downloadTranches() throws IOException {
        FileUtils.readLines(tranchesUrlsFile).forEach(url ->
                downloadTranche(url)
        );
    }

    private void downloadTranche(String url) {
        System.out.println("Downloading tranch " + url);
        try {
            Matcher fileMatcher = FILE_PATTERN.matcher(url);
            fileMatcher.find();
            String locationSuffix = fileMatcher.group(1);
            System.out.println(locationSuffix);
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File tranchFile = new File("tranches/" + tranchesType + "/" + locationSuffix);
            FileUtils.touch(tranchFile);
            FileOutputStream fos = new FileOutputStream(tranchFile);
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
}
