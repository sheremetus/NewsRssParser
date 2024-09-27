package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.SearchResult;
import by.sheremetus.parser.rssparser.util.SearchUtil;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class EpubController {

    private TemplateEngine templateEngine;

    @Autowired
    public EpubController() {
        this.templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode("HTML");
        resolver.setSuffix(".html");
        this.templateEngine.setTemplateResolver(resolver);
    }


    public void makeIdxFile(List<Integer> sourceIndices, String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Integer index : sourceIndices) {
                writer.write(index.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void makeLimitFile(String limit, String fileName) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(limit);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public List<SearchResult> searchBooks(String keyword) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        EpubReader epubReader = new EpubReader();

        List<String> filePaths = new ArrayList<>();

        String directoryPath = "D:\\Java\\Проекты\\RSSParser\\results";
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        filePaths.add(file.getAbsolutePath());
                    }
                }
            }
        }


        for (String filePath : filePaths) {
            try (FileInputStream fis = new FileInputStream(filePath)) {
                Book book = epubReader.readEpub(fis);

                for (Resource resource : book.getContents()) {
                    String content = new String(resource.getData());
                    if (SearchUtil.isParseTextHasKey(content, keyword)) {
                        Document doc = Jsoup.parse(content);
                        Elements images = doc.select("img");
                        Elements links = doc.select("a");
                        List<String> linksList = new ArrayList<>();
                        List<byte[]> imgList = new ArrayList<>();

                        for (Element img : images) {
                            String imgSrc = img.attr("src");
                            byte[] image = null;

                            if (imgSrc.startsWith("images/")) {
                                Resource imgResource = book.getResources().getByHref(imgSrc);

                                if (imgResource != null) {
                                    image = imgResource.getData();
                                    imgList.add(image);
                                }
                            }

                        }

                        for (Element link : links) {
                            String linkSrc = link.attr("href");
                            linksList.add(linkSrc);
                        }
                        results.add(new SearchResult(Jsoup.parse(content).text(), imgList, linksList));
                    }
                }
            }
        }


        // отчистка директории
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        boolean deleted = file.delete();
                        if (deleted) {
                            System.out.println("Deleted: " + file.getAbsolutePath());
                        } else {
                            System.out.println("Failed to delete: " + file.getAbsolutePath());
                        }
                    }
                }
            }
        }


        return results;
    }

    public void startProcessing() {

        ProcessBuilder pb1 = new ProcessBuilder(
                "C:\\Users\\kirja\\AppData\\Local\\Programs\\Python\\Python312\\python.exe",
                "D:\\Java\\Проекты\\RSSParser\\process.py");
        pb1.redirectErrorStream(true); // Объединение stdout и stderr
        Process process1 = null;
        try {
            process1 = pb1.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

// Чтение вывода процесса
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

// Ожидание завершения процесса
        int exitCode = 0;
        try {
            exitCode = process1.waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        if (exitCode == 0) {
            System.out.println("Скрипт парсинга выполнен успешно.");
        } else {
            System.out.println("Ошибка выполнения скрипта. Код выхода: " + exitCode);
        }


    }
}