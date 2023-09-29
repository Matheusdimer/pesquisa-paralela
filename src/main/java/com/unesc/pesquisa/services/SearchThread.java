package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchThread extends Thread {

    private final ParallelSearch parallelSearch;
    private final File file;
    private final String term;

    public SearchThread(ParallelSearch parallelSearch, File file, String term) {
        this.parallelSearch = parallelSearch;
        this.file = file;
        this.term = term;
    }

    @Override
    public void run() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            AtomicInteger row = new AtomicInteger(1);

            bufferedReader.lines().forEach(line -> {
                if (line.trim().equals(term)) {
                    parallelSearch.notifyFound(this, new SearchResult(file.getName(), term, row.get()));
                }
                row.getAndIncrement();
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
