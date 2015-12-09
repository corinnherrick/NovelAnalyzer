package personal.herrickc.novelanalyzer;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by herrickc on 12/7/15.
 */
public class Paragraph {
    private final String text;
    private final Page page;

    public Paragraph(String text, Page page) {
        this.text = text.trim();
        this.page = page;
    }

    public Paragraph(Node paragraph, Page page) {
        this(paragraph.getTextContent(), page);
    }

    // Returns true if the paragraph is valid.
    public boolean clean () {
        return StringUtils.countMatches(text.trim(), "\n")>3;
    }

    public void write(FileWriter fw) throws IOException {
        fw.write("\n<paragraph>\n");
        fw.write(text);
        fw.write("\n</paragraph>\n");
    }

    public int getId() {
        return this.page.getParagraphId(this);
    }

    public String getText() {
        return text;
    }


}
