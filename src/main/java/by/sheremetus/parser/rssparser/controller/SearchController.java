package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.SearchResult;
import by.sheremetus.parser.rssparser.entity.Source;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SearchController {

    @Autowired
    private SourceRepository sourceRepository;

/*    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {

        List<Item> result = searchRSS(keyword, model);
        model.addAttribute("searchResults", result);

        return "index"; // Возвращаем на главную страницу

    }*/

    @GetMapping("/searchAll")
    public String searchAll(@RequestParam String keyword, Model model,
                            @RequestParam("sourceIndex") List<Integer> sourceIndices,
                            @RequestParam("limit") String limit,
                            @RequestParam("start_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date start_date,
                            @RequestParam("end_date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date end_date

    ) {

        List<Item> resultsRSS = searchRSS(keyword, model);
        EpubController epubController = new EpubController();
        DateUtil.makeStartDateFile(new SimpleDateFormat("yyyy-MM-dd").format(start_date), "start_date.txt");
        DateUtil.makeEndDateFile(new SimpleDateFormat("yyyy-MM-dd").format(end_date), "end_date.txt");
        epubController.makeIdxFile(sourceIndices, "indices.txt");
        epubController.makeLimitFile(limit, "limit.txt");
        epubController.startProcessing();
        List<SearchResult> resultsTg = null;

        try {
            resultsTg = epubController.searchBooks(keyword);
        } catch (IOException e) {
            e.printStackTrace();
        }
        model.addAttribute("resultsTg", resultsTg);
        model.addAttribute("resultsRSS", resultsRSS);

        return "index"; // Возвращаем на главную страницу

    }


    public List<Item> searchRSS(@RequestParam String keyword, Model model) {
        List<Source> sourceList = sourceRepository.findAll();
        model.addAttribute("sources", sourceList); // Добавляем список источников в модель

        RssReader reader = new RssReader();
        List<String> rssList = new ArrayList<>();
        for (Source s : sourceList) {
            rssList.add(s.getUrl());
        }
        Stream<Item> rssFeed = reader.read(rssList);


        List<Item> result = rssFeed.collect(Collectors.toList());

        List<Item> parseList = new ArrayList<>();

        for (Item i : result) {
            if (i.getTitle().isPresent() && i.getDescription().isPresent()) {



                if (SearchUtil.isParseTextHasKey(i.getDescription().get(), keyword) ||
                        SearchUtil.isParseTextHasKey(i.getTitle().get(), keyword))

                    parseList.add(i);

            }

        }
        return parseList;
    }


}

