import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {
    Map<String, List<PageEntry>> map = new HashMap<>();

    public BooleanSearchEngine(File pdfsDir) throws IOException {
        File[] listOfFiles = pdfsDir.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                var doc = new PdfDocument(new PdfReader("pdfs/" + file.getName()));
                for (int i = 0; i < doc.getNumberOfPages(); i++) {
                    var text = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1));
                    var words = text.split("\\P{IsAlphabetic}+");
                    Map<String, Integer> freqs = new HashMap<>();
                    for (var word : words) {
                        if (word.isEmpty()) {
                            continue;
                        }
                        word = word.toLowerCase();
                        freqs.put(word, freqs.getOrDefault(word, 0) + 1);
                    }
                    for (var word : freqs.keySet()) {
                        if (map.containsKey(word)) {
                            List<PageEntry> list = map.get(word);
                            list.add(new PageEntry(file.getName(), i + 1, freqs.get(word)));
                            list.sort(PageEntry::compareTo);
                            map.replace(word, list);
                        } else {
                            List<PageEntry> list = new ArrayList<>();
                            list.add(new PageEntry(file.getName(), i + 1, freqs.get(word)));
                            map.put(word, list);
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        return map.get(word);
    }
}
