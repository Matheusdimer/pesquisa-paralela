package com.unesc.pesquisa.model;

import com.unesc.pesquisa.services.AbstractSearchService;
import com.unesc.pesquisa.services.IndexSearch;
import com.unesc.pesquisa.services.ParallelSearch;
import com.unesc.pesquisa.services.SequencialSearch;

import java.util.function.Supplier;

public enum SearchType {
    SEQUENTIAL("Pesquisa sequencial", SequencialSearch::new),
    PARALLEL("Pesquisa paralela normal", () -> new ParallelSearch(false)),
    PARALLEL_BACKWARDS("Paralela 2 threads (trás pra frente)", () -> new ParallelSearch(true)),
    INDEX("Pesquisa usando indexação", IndexSearch::new);

    private final String description;
    private final Supplier<AbstractSearchService> searchService;

    SearchType(String description, Supplier<AbstractSearchService> searchService) {
        this.description = description;
        this.searchService = searchService;
    }

    public AbstractSearchService getSearchService() {
        return searchService.get();
    }

    @Override
    public String toString() {
        return description;
    }
}
