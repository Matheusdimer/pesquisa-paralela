package com.unesc.pesquisa.indexing;

import com.unesc.pesquisa.util.TxtFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static com.unesc.pesquisa.util.LuceneConstants.*;

public class Indexer {

    private final IndexWriter writer;

    public Indexer(String indexDirectoryPath) throws IOException {
        Directory indexDirectory = FSDirectory.open(new File(indexDirectoryPath).toPath());

        //cria o indexador
        writer = new IndexWriter(indexDirectory, new IndexWriterConfig(new StandardAnalyzer())
                .setCommitOnClose(true));
    }

    public void close() throws IOException {
        writer.close();
    }

    private void indexFile(File file) throws IOException {
        System.out.println("Indexando arquivo " + file.getCanonicalPath());
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            AtomicInteger lineNumber = new AtomicInteger(1);

            bufferedReader.lines().forEach(line -> {
                try {
                    Document document = new Document();
                    document.add(new Field(FIELD_CONTENT, line, TextField.TYPE_STORED));
                    document.add(new Field(FIELD_FILE, file.getName(), TextField.TYPE_STORED));
                    document.add(new Field(FIELD_LINE_NUMBER, Integer.toString(lineNumber.getAndIncrement()), TextField.TYPE_STORED));
                    writer.addDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void createIndex(String path) throws IOException {
        File[] files = new File(path).listFiles(new TxtFilter());

        for (File file : files) {
            if (!file.isDirectory()
                    && !file.isHidden()
                    && file.exists()
                    && file.canRead()
            ) {
                indexFile(file);
            }
        }
    }
}