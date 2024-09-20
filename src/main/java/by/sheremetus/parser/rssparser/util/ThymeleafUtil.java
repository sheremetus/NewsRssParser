package by.sheremetus.parser.rssparser.util;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Base64;

@Component
public class ThymeleafUtil {
    public String encodeBase64(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }


}