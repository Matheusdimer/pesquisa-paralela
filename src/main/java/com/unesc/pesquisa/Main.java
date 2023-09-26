package com.unesc.pesquisa;

import com.unesc.pesquisa.services.AbstractSearchService;
import com.unesc.pesquisa.services.ParallelSearch;
import com.unesc.pesquisa.services.SequencialSearch;
import com.unesc.pesquisa.view.MainWindow;

public class Main {
    public static void main(String[] args) {
        new MainWindow((folder, searchValue, useParallel) -> {
            AbstractSearchService searchService = useParallel ? new ParallelSearch() : new SequencialSearch();
            return searchService.search(folder, searchValue);
        });
    }
}
