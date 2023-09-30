package com.unesc.pesquisa.services;

import com.unesc.pesquisa.indexing.Searcher;
import com.unesc.pesquisa.model.SearchResult;
import com.unesc.pesquisa.util.LuceneConstants;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

public class IndexSearch extends AbstractSearchService {

    private static Searcher searcher;

    @Override
    protected SearchResult runSearch(String folder, String term) {
        try {
            if (searcher == null) {
                searcher = new Searcher(folder + "/index");
            }

            TopDocs docs = searcher.search(term);

            for (ScoreDoc scoreDoc : docs.scoreDocs) {
                Document document = searcher.getDocument(scoreDoc);

                return new SearchResult(
                        document.get(LuceneConstants.FIELD_FILE),
                        document.get(LuceneConstants.FIELD_CONTENT),
                        Integer.parseInt(document.get(LuceneConstants.FIELD_LINE_NUMBER))
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SearchResult.notFound(folder, term);
    }
}
