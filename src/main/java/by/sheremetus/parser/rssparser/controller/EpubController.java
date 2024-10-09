package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.SearchResult;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
import by.sheremetus.parser.rssparser.service.TelegramService;
import by.sheremetus.parser.rssparser.util.SearchUtil;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
public class EpubController {
    private TemplateEngine templateEngine;

    private final String directoryPath = "D:/Java/Проекты/RSSParser/results";
    private final String pythonExecutablePath = "C:/Users/kirja/AppData/Local/Programs/Python/Python312/python.exe";
    private final String pythonScriptPath = "D:/Java/Проекты/RSSParser/process.py";

    @Autowired
    public EpubController() {
        this.templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setTemplateMode("HTML");
        resolver.setSuffix(".html");
        this.templateEngine.setTemplateResolver(resolver);
    }

    public void makeIdxFile(List<Integer> sourceIndices, String fileName) {
        try {
            Files.write(Paths.get(fileName), sourceIndices.stream().map(Object::toString).collect(Collectors.toList()));
        } catch (IOException e) {
            throw new RuntimeException("Error writing to index file", e);
        }
    }

    public List<SearchResult> searchBooks(String keyword) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        EpubReader epubReader = new EpubReader();

        List<String> filePaths = getEpubFilePaths();

        for (String filePath : filePaths) {
            results.addAll(searchBook(filePath, keyword, epubReader));
        }

        cleanDirectory();

        return results;
    }

    private List<String> getEpubFilePaths() {
        File directory = new File(directoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalStateException("Invalid directory path: " + directoryPath);
        }

        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".epub"));
        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files).map(File::getAbsolutePath).collect(Collectors.toList());
    }

    private List<SearchResult> searchBook(String filePath, String keyword, EpubReader epubReader) throws IOException {
        List<SearchResult> results = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Book book = epubReader.readEpub(fis);
            for (Resource resource : book.getContents()) {
                String content = new String(resource.getData());
                if (SearchUtil.isParseTextHasKey(content, keyword)) {
                    results.add(createSearchResult(content, book));
                }
            }
        }
        return results;
    }

    private SearchResult createSearchResult(String content, Book book) {
        Document doc = Jsoup.parse(content);
        List<byte[]> imgList = extractImages(doc, book);
        List<String> linksList = extractLinks(doc);
        return new SearchResult(doc.text(), imgList, linksList);
    }

    private List<byte[]> extractImages(Document doc, Book book) {
        return doc.select("img").stream()
                .map(img -> img.attr("src"))
                .filter(src -> src.startsWith("images/"))
                .map(book.getResources()::getByHref)
                .filter(Objects::nonNull)
                .map(imgResource -> {
                    try {
                        return imgResource.getData();
                    } catch (IOException e) {
                        System.err.println("Error reading image data: " + e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<String> extractLinks(Document doc) {
        return doc.select("a").stream()
                .map(link -> link.attr("href"))
                .collect(Collectors.toList());
    }

    private void cleanDirectory() {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && !file.delete()) {
                        System.err.println("Failed to delete: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }

    public void startProcessing() {

        if (pythonExecutablePath == null || pythonScriptPath == null) {
            throw new IllegalStateException("Python executable path or script path is not set");
        }
        ProcessBuilder pb = new ProcessBuilder(pythonExecutablePath, pythonScriptPath);
        pb.redirectErrorStream(true);

        try {
            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Скрипт парсинга выполнен успешно.");
            } else {
                System.err.println("Ошибка выполнения скрипта. Код выхода: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error executing Python script", e);
        }
    }




}