package com.unesc.pesquisa.model;

import java.text.NumberFormat;

public class SearchResult {
    private final String file;
    private final String term;
    private final int row;
    private long searchTime;
    private final boolean found;
    private int linesTraveled;

    public SearchResult(String file, String term, int row) {
        this.file = file;
        this.term = term;
        this.row = row;
        this.found = true;
    }

    public SearchResult(String file, String term, int row, boolean found) {
        this.file = file;
        this.term = term;
        this.row = row;
        this.found = found;
    }

    public static SearchResult notFound(String file, String term) {
        return new SearchResult(file, term, 0, false);
    }

    public String getFile() {
        return file;
    }

    public String getTerm() {
        return term;
    }

    public int getRow() {
        return row;
    }

    public long getSearchTime() {
        return searchTime;
    }

    public boolean isFound() {
        return found;
    }

    public SearchResult withSearchTime(long searchTime) {
        this.searchTime = searchTime;
        return this;
    }

    public SearchResult withLinesTraveled(int linesTraveled) {
        this.linesTraveled = linesTraveled;
        return this;
    }

    @Override
    public String toString() {
        String message = String.format("""
                        Termo %s encontrado no arquivo %s na linha %s
                        Tempo de execução: %s ns (nano segundos)""",
                term, file, row, NumberFormat.getInstance().format(searchTime));

        if (linesTraveled > 0) {
            message += "Linhas percorridas: " + linesTraveled;
        }

        return message;
    }
}
