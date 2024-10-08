package by.sheremetus.parser.rssparser.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class TelegramService {
    private final String pythonExecutablePath = "C:/Users/kirja/AppData/Local/Programs/Python/Python312/python.exe";
    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleMessage(String text, LocalDateTime scheduledDateTime) {
        taskScheduler.schedule(() -> sendMessage(text), scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private void sendMessage(String text) {
        // Здесь вызывайте метод для отправки сообщения в Telegram
        // Например, используя telegram_poster.py через ProcessBuilder
        try {
            ProcessBuilder pb = new ProcessBuilder(pythonExecutablePath, "telegram_poster.py", text);
            Process p = pb.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
