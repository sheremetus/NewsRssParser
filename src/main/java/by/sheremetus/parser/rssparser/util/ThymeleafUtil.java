package by.sheremetus.parser.rssparser.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ThymeleafUtil {
    public String encodeBase64(byte[] input) {
        if (input == null) {
            return "";
        }
        return Base64.getEncoder().encodeToString(input);
    }

    public String encodeBase64List(List<byte[]> images) {
        List<String> base64Images = images.stream()
                .map(this::encodeBase64)
                .collect(Collectors.toList());
        try {
            return new ObjectMapper().writeValueAsString(base64Images);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

}