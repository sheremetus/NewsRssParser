package by.sheremetus.parser.rssparser.controller;

import by.sheremetus.parser.rssparser.controller.SimpleWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Controller
public class ScriptController {

    @Autowired
    private SimpleWebSocketHandler webSocketHandler;

    @GetMapping("/run-script")
    @ResponseBody
    public String runScript() {
        new Thread(() -> {
            try {
// Укажите путь к Git Bash и скрипту
                Process process = new ProcessBuilder("C:\\Program Files\\Git\\bin\\bash.exe", "-c", "D:\\Java\\Проекты\\RSSParser\\your-script.sh").start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    webSocketHandler.sendMessage(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return "Script started";
    }
}
