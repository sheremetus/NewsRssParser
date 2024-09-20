package by.sheremetus.parser.rssparser.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class DateUtil {

    public void parseRssFromDate(Date date) {


    }

    public void parseTgFromDate(Date date) {


    }

    public static void makeStartDateFile(String startDate, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(startDate);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void makeEndDateFile(String endDate, String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(endDate);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
