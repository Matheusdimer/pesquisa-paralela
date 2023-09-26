package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

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
            int row = 1;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals(term)) {
                    parallelSearch.notifyFound(this, new SearchResult(file.getName(), term, row));
                }
                row++;
//                parallelSearch.incrementLinesTraveled();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
