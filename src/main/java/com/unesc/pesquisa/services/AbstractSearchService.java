package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;

public abstract class AbstractSearchService {
    public SearchResult search(String folder, String term) {
        long init = System.nanoTime();
        return runSearch(folder, term)
                .withSearchTime(System.nanoTime() - init);
    }

    protected abstract SearchResult runSearch(String folder, String term);
}
