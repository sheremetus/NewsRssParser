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

    @PostMapping("/addPublicationChannels")
    public String addPublicationChannels(@RequestParam String channelName) {
        PublicationChannel publicationChannel = new PublicationChannel();
        publicationChannel.setChannelName(channelName);
        publicationChannelRepository.save(publicationChannel);
        return "redirect:/";
    }

    @PostMapping("/deletePublicationChannels")
    public String deletePublicationChannels(@RequestParam Long id) {
        publicationChannelRepository.deleteById(id);
        return "redirect:/";
    }


    @PostMapping("/changePublicationChannels")
    public String changePublicationChannels(@RequestParam(name = "ids", required = false) List<Long> ids, Model model) {
// Получаем все публикационные каналы
        List<PublicationChannel> allPublicationChannels = publicationChannelRepository.findAll();

        if (ids != null) {
            for (PublicationChannel p : allPublicationChannels) {
                p.setActive(!ids.isEmpty() && ids.contains(p.getId()));
            }
        } else {
            for (PublicationChannel p : allPublicationChannels) {

                p.setActive(false);
            }

        }

// Сохраняем все изменения
        publicationChannelRepository.saveAll(allPublicationChannels);

        return "redirect:/";
    }

}
