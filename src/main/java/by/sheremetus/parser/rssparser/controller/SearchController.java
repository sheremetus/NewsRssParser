package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.Source;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
import com.apptasticsoftware.rssreader.Item;
import com.apptasticsoftware.rssreader.RssReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
public class SearchController {

    @Autowired
    private SourceRepository sourceRepository;

    @GetMapping("/search")
    public String search(@RequestParam String keyword, Model model) {
        List<Source> sourceList = sourceRepository.findAll();
        model.addAttribute("sources", sourceList); // Добавляем список источников в модель

        RssReader reader = new RssReader();
        List<String> rssList = new ArrayList<>();
        for (Source s : sourceList) {
            rssList.add(s.getUrl());
        }
        Stream<Item> rssFeed = reader.read(rssList);
        List<Item> result = rssFeed.collect(Collectors.toList());
        model.addAttribute("searchResults", result);

        return "index"; // Возвращаем на главную страницу
    }
}