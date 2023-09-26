package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;

import java.io.*;

public class SequencialSearch extends AbstractSearchService {

    private int linesTraveled = 0;

    @Override
    protected SearchResult runSearch(String folder, String term) {
        File fileFolder = new File(folder);
        File[] textFiles = fileFolder.listFiles((dir, name) -> name.endsWith(".txt"));

        if (textFiles == null) {
            return SearchResult.notFound(folder, term);
        }

        for (File textFile : textFiles) {
            SearchResult searchResult = findInFile(textFile, term);
            if (searchResult.isFound()) {
                return searchResult.withLinesTraveled(linesTraveled);
            }
        }


        return SearchResult.notFound(folder, term);
    }

    private SearchResult findInFile(File file, String term) {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            int row = 1;
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().equals(term)) {
                    return new SearchResult(file.getName(), term, row);
                }
                row++;
                linesTraveled++;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return SearchResult.notFound(file.getName(), term);
    }
}
