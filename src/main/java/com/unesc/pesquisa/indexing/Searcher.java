package com.unesc.pesquisa.indexing;

import com.unesc.pesquisa.util.LuceneConstants;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;

public class Searcher {

    private final IndexSearcher indexSearcher;
    private final QueryParser queryParser;

    public Searcher(String indexDirectoryPath) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath).toPath());
        indexSearcher = new IndexSearcher(DirectoryReader.open(indexDirectory));
        queryParser = new QueryParser(LuceneConstants.FIELD_CONTENT, new StandardAnalyzer());
    }

    public TopDocs search(String searchQuery)
            throws IOException, ParseException {
        Query query = queryParser.parse(searchQuery);
        return indexSearcher.search(query, 1);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws IOException {
        return indexSearcher.doc(scoreDoc.doc);
    }
}