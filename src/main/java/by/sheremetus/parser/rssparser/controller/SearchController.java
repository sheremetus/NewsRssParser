package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.entity.SearchResult;
import by.sheremetus.parser.rssparser.entity.Source;
import by.sheremetus.parser.rssparser.repo.PublicationChannelRepository;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
import by.sheremetus.parser.rssparser.service.TelegramService;
import by.sheremetus.parser.rssparser.util.DateUtil;
import by.sheremetus.parser.rssparser.util.SearchUtil;
import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private PublicationChannelRepository publicationChannelRepository;
    @Autowired
    TelegramService telegramService;

    private static final String START_DATE_FILE = "start_date.txt";
    private static final String END_DATE_FILE = "end_date.txt";
    private static final String INDICES_FILE = "indices.txt";

    @GetMapping("/searchAll")
    public String searchAll(@RequestParam String keyword, Model model,
                            @RequestParam(name = "sourceIndex", required = false) List<Integer> sourceIndices,
                            @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                            @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        List<Item> resultsRSS = searchRSS(keyword);
        if (sourceIndices != null) {
            List<SearchResult> resultsTg = searchTelegram(keyword, sourceIndices, startDate, endDate);
            model.addAttribute("resultsTg", resultsTg);
        }
        List<Source> sourceList = sourceRepository.findAll();
        List<PublicationChannel> publicationChannelsList = publicationChannelRepository.findAll();

        model.addAttribute("sources", sourceList);
        model.addAttribute("publicationChannels", publicationChannelsList);
        model.addAttribute("tgSources", telegramService.getTelegramSources(model));
        model.addAttribute("resultsRSS", resultsRSS);
        model.addAttribute("keywords", Arrays.asList(keyword.trim().split(",")));

        return "index";
    }

    private List<SearchResult> searchTelegram(String keyword, List<Integer> sourceIndices, Date startDate, Date endDate) {
        EpubController epubController = new EpubController();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        DateUtil.makeStartDateFile(dateFormat.format(startDate), START_DATE_FILE);
        DateUtil.makeEndDateFile(dateFormat.format(endDate), END_DATE_FILE);
        epubController.makeIdxFile(sourceIndices, INDICES_FILE);
        epubController.startProcessing();

        try {
            return epubController.searchBooks(keyword);
        } catch (IOException e) {
            // Log the error and return an empty list or throw a custom exception
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Item> searchRSS(String keyword) {
        List<Source> sourceList = sourceRepository.findAll();
        RssReader reader = new RssReader();

        List<String> rssList = sourceList.stream()
                .map(Source::getUrl)
                .collect(Collectors.toList());

        return reader.read(rssList)
                .filter(item -> item.getTitle().isPresent() && item.getDescription().isPresent())
                .filter(item -> SearchUtil.isParseTextHasKey(item.getDescription().get(), keyword) ||
                        SearchUtil.isParseTextHasKey(item.getTitle().get(), keyword))
                .collect(Collectors.toList());
    }


}