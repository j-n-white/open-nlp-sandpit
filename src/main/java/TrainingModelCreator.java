import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.namefind.TokenNameFinderFactory;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class TrainingModelCreator {

    private static final String TAG_NAME = "tech";
    private static final String LANGUAGE_CODE = "en";

    public static void main(String[] args) {

        try (ObjectStream<String> lineStream =
                     new PlainTextByLineStream(() -> TrainingModelCreator.class.getClassLoader().getResourceAsStream("trainingData/trainingData.txt"), StandardCharsets.UTF_8);
             ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream)) {
            TokenNameFinderModel model = NameFinderME.train(LANGUAGE_CODE, TAG_NAME, sampleStream, TrainingParameters.defaultParams(), new TokenNameFinderFactory());


            try (OutputStream modelOut = new BufferedOutputStream(new FileOutputStream("out/" + LANGUAGE_CODE + "-ner-" + TAG_NAME + ".bin"))) {
                model.serialize(modelOut);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
