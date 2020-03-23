# Open-npl

* Requires Java 11 or higher
* Run `gradlew build` to build the project

## TrainingModelCreator
* Add training data to file `src/main/resources/trainingData/trainingData.txt`
  * This should be in the format of 1 sentence per line with tags round the works of interest `<START:tagName> tokens to be tagged <END>`
* Run the main method in `TrainingModelCreator.java`
* This out puts a binary training model file to the `out` directory

## NamedEntityRecognition
* Move the binary file created above to `src/resources/models`
* Run the main method in `NamedEntityRecognition.java`
* This will output to the tagged profile to the console.

Pretrained models were downloaded from http://opennlp.sourceforge.net/models-1.5/