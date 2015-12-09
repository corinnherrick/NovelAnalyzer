package personal.herrickc.novelanalyzer;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.stanford.nlp.ie.crf.CRFClassifier.getClassifier;


/**
 * Created by herrickc on 12/7/15.
 */
public class NERTagger {
    private static String serializedClassifier = "classifiers/english.all.3class.distsim.crf.ser.gz";

    private static Map<String, String> overrides;
    private static Map<String, String> aliases;

    static {
        overrides = new HashMap<String, String>();
        overrides.put("Sethe", "PERSON");
        overrides.put("Denver", "PERSON");
        overrides.put("Miss Denver", "PERSON");
        overrides.put("Baby Suggs", "PERSON");
        overrides.put("Buglar", "PERSON");
        overrides.put("Úrsula", "PERSON");
        overrides.put("Amaranta", "PERSON");
        overrides.put("Melquiades", "PERSON");
        overrides.put("Santa Sofía de la Piedad", "PERSON");
        overrides.put("Petra Cotes", "PERSON");
        overrides.put("Germán", "PERSON");
        overrides.put("Gaston", "PERSON");
        overrides.put("Petrarch", "PERSON");
        overrides.put("Meme", "PERSON");
        overrides.put("Pietro Crespi", "PERSON");
        overrides.put("Riohacha", "LOCATION");
        overrides.put("Aureliano Segundo", "PERSON");
        overrides.put("Mercedes", "PERSON");
        overrides.put("Amaranta Úrsula", "PERSON");
        overrides.put("Aureliano Segundo", "PERSON");
        overrides.put("Curaçao", "LOCATION");

        aliases = new HashMap<String, String>();
        aliases.put("Petra Cates", "Petra Cotes");
        aliases.put("Piedad", "Santa Sofía de la Piedad");
        aliases.put("Auerliano", "Aureliano");
        aliases.put("AURELIANO", "Aureliano");
        aliases.put("Amarante", "Amaranta");
        aliases.put("Rebeca", "Rebeca Buendía");
        aliases.put("MEME", "Meme");
        aliases.put("PILAR TERNERA", "Pilar Ternera");
        aliases.put("Amparo", "Amparo Moscote");
    }


    private AbstractSequenceClassifier<CoreLabel> classifier;
    public NERTagger() throws IOException, ClassNotFoundException {
        classifier = getClassifier(serializedClassifier);

    }

    public Map<Integer, List<Pair<String, String>>> tag(String text) {
        Map<Integer, List<Pair<String, String>>> results = new HashMap<Integer, List<Pair<String, String>>>(); // currentType, currentChunk
        List<List<CoreLabel>> sentences = classifier.classify(text);
        for (int i=0; i<sentences.size(); i++) {
            List<CoreLabel> sentence = sentences.get(i);
            results.put(i, new ArrayList<Pair<String, String>>());
            String currentChunk = "";
            String currentType = "";

            for (CoreLabel token : sentence) {
                String type = token.get(CoreAnnotations.AnswerAnnotation.class);
                String name = token.word();
                if (type.equals(currentType)) {
                    currentChunk += " " + name;
                } else {
                    if (!currentType.equals("O") && !currentChunk.equals("")) {
                        if (aliases.containsKey(currentChunk)) {
                            currentChunk = aliases.get(currentChunk);
                        }
                        if (overrides.containsKey(currentChunk)) {
                            currentType = overrides.get(currentChunk);
                        }
                        results.get(i).add(new ImmutablePair<String, String>(currentType, currentChunk));
                    }
                    currentType = type;
                    currentChunk = name;
                }

            }
        }
        return results;
    }
}
