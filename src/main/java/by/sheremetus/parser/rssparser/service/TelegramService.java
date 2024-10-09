package by.sheremetus.parser.rssparser.service;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class TelegramService {
    private final String pythonExecutablePath = "C:/Users/kirja/AppData/Local/Programs/Python/Python312/python.exe";
    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleMessage(String text, LocalDateTime scheduledDateTime, List<PublicationChannel> publicationChannelList) {
        taskScheduler.schedule(() -> sendMessage(text, publicationChannelList), scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void sendMessage(String text, List<PublicationChannel> publicationChannelList) {
        // Здесь вызывайте метод для отправки сообщения в Telegram
        // Например, используя telegram_poster.py через ProcessBuilder
        for (PublicationChannel pc : publicationChannelList) {
            try {
                ProcessBuilder pb = new ProcessBuilder(pythonExecutablePath, "telegram_poster.py", text, pc.getChannelName());
                Process p = pb.start();
                p.waitFor();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
