package com.recipe.app.src.common.utils;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.KoreanAnalyzer;
import org.apache.lucene.analysis.ko.KoreanPartOfSpeechStopFilter;
import org.apache.lucene.analysis.ko.KoreanTokenizer.DecompoundMode;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public final class KoreanTokenizer {

    private static final KoreanAnalyzer ANALYZER = new KoreanAnalyzer(
            null,
            DecompoundMode.NONE,
            KoreanPartOfSpeechStopFilter.DEFAULT_STOP_TAGS,
            false
    );

    private KoreanTokenizer() {
    }

    public static String tokenize(String text) {

        if (text == null || text.isBlank()) return "";

        List<String> tokens = new ArrayList<>();
        try (TokenStream ts = ANALYZER.tokenStream(null, new StringReader(text))) {
            CharTermAttribute attr = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                String token = attr.toString();
                if (!token.isEmpty()) tokens.add(token);
            }
            ts.end();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to tokenize: " + text, e);
        }
        return String.join(" ", tokens);
    }
}
