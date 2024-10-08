package by.sheremetus.parser.rssparser.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RunPythonScripts {


    public static void main(String[] args) {
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
    }


}


