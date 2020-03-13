import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NamedEntityRecognition {
    public static void main(String[] args) {
        try {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            try (InputStream is = classLoader.getResourceAsStream("exampleProfile.txt")) {
                if (is == null) return;
                try (InputStreamReader isr = new InputStreamReader(is);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String profile = reader.lines().collect(Collectors.joining(System.lineSeparator()));

                    // Language detection
//                    try (InputStream modelIn = NlpMain.class.getClassLoader()
//                            .getResourceAsStream("langdetect-183.bin")) {
//                        LanguageDetectorModel m = new LanguageDetectorModel(modelIn);
//                        LanguageDetector myCategorizer = new LanguageDetectorME(m);
//                        Language bestLanguage = myCategorizer.predictLanguage(profile);
//                        System.out.println("Best language: " + bestLanguage.getLang());
//                        System.out.println("Best language confidence: " + bestLanguage.getConfidence());
//                        Language[] languages = myCategorizer.predictLanguages(profile);
//                        Arrays.stream(languages).forEach(System.out::println);
//                    }
                    // Sentence detection
                    try (InputStream modelIn = NamedEntityRecognition.class.getClassLoader().getResourceAsStream("models/en-sent.bin")) {
                        SentenceModel model = new SentenceModel(modelIn);
                        SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
                        List<String> sentences = Arrays.stream(sentenceDetector.sentDetect(profile))
                                .flatMap(sentence -> Arrays.stream(sentence.split("\r\n")))
                                .filter(sentence -> sentence.length() > 0)
                                .collect(Collectors.toList());

                        // Tokenisation
                        try (InputStream tokenModel = NamedEntityRecognition.class.getClassLoader().getResourceAsStream("models/en-token.bin")) {
                            TokenizerModel tokenizerModel = new TokenizerModel(tokenModel);
                            TokenizerME tokenizer = new TokenizerME(tokenizerModel);
                            try (InputStream nameModel = NamedEntityRecognition.class.getClassLoader().getResourceAsStream("models/en-ner-tech.bin")) {
                                TokenNameFinderModel nameFinderModel = new TokenNameFinderModel(nameModel);
                                sentences.stream()
                                        .map(tokenizer::tokenize)
                                        .forEach(tokens -> {
                                            NameFinderME nameFinder = new NameFinderME(nameFinderModel);
                                            Span[] names = nameFinder.find(tokens);
                                            Arrays.stream(names).forEach(nameSpan -> {
                                                tokens[nameSpan.getStart()] = "[" + nameSpan.getType() + ": " + tokens[nameSpan.getStart()];
                                                tokens[nameSpan.getEnd() - 1 ] = tokens[nameSpan.getEnd() - 1] + "]";
                                            });
                                            nameFinder.clearAdaptiveData();
                                            System.out.println(String.join(" ", tokens));
                                        });
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
