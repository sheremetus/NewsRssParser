package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.entity.Source;
import by.sheremetus.parser.rssparser.repo.PublicationChannelRepository;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
import by.sheremetus.parser.rssparser.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SourceController {
    @Autowired
    private SourceRepository sourceRepository;
    @Autowired
    private TelegramController telegramController;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private PublicationChannelRepository publicationChannelRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<Source> sourceList = sourceRepository.findAll();
        List<PublicationChannel> publicationChannelsList = publicationChannelRepository.findAll();
        model.addAttribute("sources", sourceList);
        model.addAttribute("publicationChannels", publicationChannelsList);
        model.addAttribute("tgSources", telegramService.getTelegramSources(model)) ;


        return "index";
    }

    @PostMapping("/add")
    public String addSource(@RequestParam String url) {
        Source source = new Source();
        source.setUrl(url);
        sourceRepository.save(source);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String deleteSource(@RequestParam Long id) {
        sourceRepository.deleteById(id);
        return "redirect:/";
    }

}
