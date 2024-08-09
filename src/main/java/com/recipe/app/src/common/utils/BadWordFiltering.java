package com.recipe.app.src.common.utils;

import com.recipe.app.src.etc.exception.BadWordException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class BadWordFiltering {

    private List<String> badWords;

    public BadWordFiltering() {

        try {

            ClassPathResource resource = new ClassPathResource("badwords.txt");
            byte[] bdata = FileCopyUtils.copyToByteArray(resource.getInputStream());
            String text = new String(bdata, StandardCharsets.UTF_8);

            this.badWords = Arrays.stream(text.replaceAll("\'", "").split(", ")).toList();
        } catch (Exception e) {
            this.badWords = new ArrayList<>();
        }
    }


    public void check(String keyword) {

        for (String badWord : badWords) {
            if (keyword.contains(badWord)) {
                throw new BadWordException(badWord);
            }
        }
    }
}
