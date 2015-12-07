package personal.herrickc.novelanalyzer;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;

/**
 * This class converts a pdf file to a simplified xml file (with only <paragraph> and <page> tags
 */
public class PDFToTextConverter {
    private PDFTextStripper pdfTextStripper;

    public PDFToTextConverter() throws IOException {
        this.pdfTextStripper = new PDFTextStripper();
        this.pdfTextStripper.setPageStart("\n\n<page>\n\n");
        this.pdfTextStripper.setPageEnd("\n\n</page>\n\n");
        this.pdfTextStripper.setParagraphStart("\n<paragraph>\n");
        this.pdfTextStripper.setParagraphEnd("\n</paragraph>\n");
        this.pdfTextStripper.setDropThreshold((float) 2.0);
    }

    public void convert(String inputFilename, String outputFilename) throws IOException {
        PDDocument pdDoc = PDDocument.load(new FileInputStream(inputFilename));
        FileWriter fw = new FileWriter(outputFilename);
        fw.write("<?xml version=\"1.0\"?>\n<novel>\n\n");
        pdfTextStripper.writeText(pdDoc, fw);
        fw.write("\n\n</novel>");
        fw.close();


    }


}
