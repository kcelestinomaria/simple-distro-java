package client.utils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PDFGenerator {
    public static void generateReport(String reportData, String filePath) throws DocumentException, FileNotFoundException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        // Split the report data by newlines and add each line as a paragraph for better formatting.
        for (String line : reportData.split("\\n")) {
            document.add(new Paragraph(line));
        }
        document.close();
    }
}
