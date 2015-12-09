package personal.herrickc.novelanalyzer;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by herrickc on 12/7/15.
 */
public class Entity {
    private String name;
    private String type;
    private Set<Triple<Integer, Integer, Integer>> mentions; // sentenceID, paragraphID, pageID

    public Entity(String name, String type) {
        this.name = name;
        this.type = type;
        this.mentions = new HashSet<Triple<Integer, Integer, Integer>>();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public Set<Triple<Integer, Integer, Integer>> getMentions() {
        return mentions;
    }

    public void addMention(int sentenceID, int paragraphID, int pageID) {
        mentions.add(new ImmutableTriple<Integer, Integer, Integer>(sentenceID, paragraphID, pageID));
    }


    public int computeCoocurrenceParagraph(Entity other) {
        Set<Pair<Integer, Integer>> thisParagraphMentions = new HashSet<Pair<Integer, Integer>>();
        Set<Pair<Integer, Integer>> otherParagraphMentions = new HashSet<Pair<Integer, Integer>>();
        for (Triple<Integer, Integer, Integer> mention : this.getMentions()) {
            thisParagraphMentions.add(new ImmutablePair<Integer, Integer>(mention.getMiddle(), mention.getRight()));
        }
        for (Triple<Integer, Integer, Integer> mention : other.getMentions()) {
            otherParagraphMentions.add(new ImmutablePair<Integer, Integer>(mention.getMiddle(), mention.getRight()));
        }

        // Mentions must be in the same paragraph, but only one per page will be counted.
        Set<Integer> pageSet = new HashSet<Integer>();
        for (Pair<Integer, Integer> coords : computeCoocurrence(thisParagraphMentions, otherParagraphMentions)) {
            pageSet.add(coords.getRight());
        }

        return pageSet.size();

    }



    private <T> Set<T> computeCoocurrence(Set<T> s1, Set<T> s2) {
        Set intersection = new HashSet(s1);
        intersection.retainAll(s2);
        return intersection;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Entity && ((Entity) o).getName().equals(this.getName()) && ((Entity) o).getType().equals(this.getType());
    }

    @Override
    public int hashCode() {
        return 31*this.getName().hashCode() + this.getType().hashCode();
    }

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }

}
