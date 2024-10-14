package by.sheremetus.parser.rssparser.service;

import by.sheremetus.parser.rssparser.entity.PublicationChannel;
import by.sheremetus.parser.rssparser.entity.TgSource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Service
public class TelegramService {
    private final String pythonExecutablePath = "C:/Users/kirja/AppData/Local/Programs/Python/Python312/python.exe";

    @Autowired
    private TaskScheduler taskScheduler;

    public void scheduleMessage(String text, LocalDateTime scheduledDateTime,
                                List<PublicationChannel> channels, List<String> base64Images) {
        taskScheduler.schedule(() -> sendMessage(text, channels, base64Images),
                scheduledDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public void sendMessage(String text, List<PublicationChannel> channels, List<String> base64Images) {
        for (PublicationChannel channel : channels) {
            try {
                List<String> command = new ArrayList<>(Arrays.asList(
                        pythonExecutablePath,
                        "telegram_poster.py",
                        text,
                        channel.getChannelName()
                ));

                if (base64Images != null && !base64Images.isEmpty()) {
                    Path tempDir = Files.createTempDirectory("telegram_images");
                    List<String> imagePaths = new ArrayList<>();

                    for (int i = 0; i < base64Images.size(); i++) {
                        String base64Image = base64Images.get(i);
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
                        Path imagePath = tempDir.resolve("image_" + i + ".jpg");
                        Files.write(imagePath, imageBytes);
                        imagePaths.add(imagePath.toString());
                    }

                    command.add(String.join(",", imagePaths));
                }

                ProcessBuilder pb = new ProcessBuilder(command);
                Process p = pb.start();
                p.waitFor();

                // Clean up temporary files if they were created
                if (command.size() > 4) {
                    Path tempDir = Paths.get(command.get(4).split(",")[0]).getParent();
                    Files.walk(tempDir)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public List<TgSource> getTelegramSources(Model model) {
        try {
            ProcessBuilder pb1 = new ProcessBuilder(pythonExecutablePath, "D:\\Java\\Проекты\\RSSParser\\dialogs.py");
            pb1.redirectErrorStream(true);
            Process process1 = pb1.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

            int exitCode = process1.waitFor();
            if (exitCode != 0) {
                System.out.println("Ошибка выполнения скрипта. Код выхода: " + exitCode);
                return new ArrayList<>();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }

        List<TgSource> tgSources = new ArrayList<>();
        try (JsonReader reader = new JsonReader(new FileReader("dialogs.json"))) {
            JsonArray channels = new Gson().fromJson(reader, JsonArray.class);
            for (JsonElement element : channels) {
                JsonObject channel = element.getAsJsonObject();
                tgSources.add(new TgSource(
                        channel.get("idx").getAsInt(),
                        channel.get("name").getAsString()
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tgSources;
    }
}
