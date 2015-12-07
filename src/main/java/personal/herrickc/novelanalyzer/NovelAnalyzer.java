package personal.herrickc.novelanalyzer;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by herrickc on 12/6/15.
 */
public class NovelAnalyzer {

    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException, TransformerException {
        String inputFile = "/home/herrickc/Classes/21L512/Marquez.pdf";
        String outputFile = "/home/herrickc/Classes/21L512/Marquez.xml";
        PDFToTextConverter converter = new PDFToTextConverter();
        converter.convert(inputFile, outputFile);

        NovelCleaner cleaner = new NovelCleaner();
        cleaner.clean(outputFile, outputFile + ".cleaned");

    }
}
