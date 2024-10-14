package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.repo.PublicationChannelRepository;
import by.sheremetus.parser.rssparser.repo.SourceRepository;
import by.sheremetus.parser.rssparser.service.TelegramService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TelegramController {

    @Autowired
    public SourceRepository sourceRepository;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private PublicationChannelRepository publicationChannelRepository;


    @PostMapping("/postToTelegram")
    public String postToTelegram(@RequestParam("text") String text,
                                            @RequestParam(name = "scheduleTime", required = false) String scheduleTime,
                                            @RequestParam(required = false) String imageBase64) throws IOException {
        List<PublicationChannel> publicationChannelList = publicationChannelRepository.findByActiveIsTrue();

        List<String> base64Images = new ArrayList<>();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            base64Images = new ObjectMapper().readValue(imageBase64, new TypeReference<List<String>>() {
            });
        }

        if (!scheduleTime.isEmpty() && !scheduleTime.isBlank()) {
            LocalDateTime scheduledDateTime = LocalDateTime.parse(scheduleTime);
            telegramService.scheduleMessage(text, scheduledDateTime, publicationChannelList, base64Images);
        } else {
            telegramService.sendMessage(text, publicationChannelList, base64Images);
        }


        return "redirect:/";
    }


    private String saveImageTemporarily(MultipartFile image) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = image.getOriginalFilename();
        Path path = Paths.get(tempDir, fileName);
        Files.write(path, image.getBytes());
        return path.toString();
    }

    @PostMapping("/postRssToTelegram")
    public String postRssToTelegram(@RequestParam String text,
                                    @RequestParam(required = false) String scheduleTime,
                                    RedirectAttributes redirectAttributes) {
        // Logic for posting RSS content to Telegram
        // Similar to postToTelegram method
        return "redirect:/searchAll";
    }
}