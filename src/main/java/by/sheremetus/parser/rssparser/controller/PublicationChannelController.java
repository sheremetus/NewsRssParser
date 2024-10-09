package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.repo.PublicationChannelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PublicationChannelController {

    @Autowired
    PublicationChannelRepository publicationChannelRepository;

    @PostMapping("/changePublicationChannels")
    public String changePublicationChannels(@RequestParam("ids") List<Long> ids,
                                            Model model) {

        List<PublicationChannel> publicationChannelList = publicationChannelRepository.findAllById(ids);
        for (PublicationChannel p : publicationChannelList) {
            p.setActive(true);
        }
        publicationChannelRepository.saveAll(publicationChannelList);

        return "redirect:/";
    }

}
