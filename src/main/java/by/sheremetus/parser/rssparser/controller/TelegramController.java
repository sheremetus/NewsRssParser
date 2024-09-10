package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.entity.TgSource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Controller
public class TelegramController {

    @GetMapping("/getTelegramSources")
    public String getTelegramSources(Model model) {

        try {
// Запуск первого скрипта для получения списка каналов
            ProcessBuilder pb1 = new ProcessBuilder("C:\\Users\\kirja\\AppData\\Local\\Programs\\Python\\Python312\\python.exe",
                    "D:\\Java\\Проекты\\RSSParser\\TelegramEbookConverter\\dialogs.py");
            pb1.redirectErrorStream(true); // Объединение stdout и stderr
            Process process1 = pb1.start();

// Чтение вывода процесса
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process1.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }

// Ожидание завершения процесса
            int exitCode = process1.waitFor();
            if (exitCode == 0) {
                System.out.println("Скрипт выполнен успешно.");
            } else {
                System.out.println("Ошибка выполнения скрипта. Код выхода: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        Gson gson = new Gson();

        try (JsonReader reader = new JsonReader(new FileReader("dialogs.json"))) {
            JsonArray channels = gson.fromJson(reader, JsonArray.class);
            List<TgSource> tgSources = new ArrayList<>();
            for (JsonElement element : channels) {
                JsonObject channel = element.getAsJsonObject();
                int index = channel.get("index").getAsInt();
                String name = channel.get("name").getAsString();
                TgSource tgSource = new TgSource(index, name);
                tgSources.add(tgSource);

                model.addAttribute("tgSources", tgSources);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return "index";
    }
}