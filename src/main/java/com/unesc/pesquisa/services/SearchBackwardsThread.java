package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class SearchBackwardsThread extends Thread {

    private final ParallelSearch parallelSearch;
    private final File file;
    private final String term;

    public SearchBackwardsThread(ParallelSearch parallelSearch, File file, String term) {
        this.parallelSearch = parallelSearch;
        this.file = file;
        this.term = term;
    }

    @Override
    public void run() {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            int rowCount = 0;
            long fileLength = randomAccessFile.length();
            StringBuilder reversedLine = new StringBuilder();
            long pointer = fileLength - 1;

            while (pointer >= 0) {
                randomAccessFile.seek(pointer);

                int currentByte = randomAccessFile.read();
                if (currentByte == -1) {
                    break; // Fim do arquivo
                }

                char currentChar = (char) currentByte;

                if (currentChar == '\n' || currentChar == '\r') {
                    rowCount++;
                    if (term.contentEquals(reversedLine.reverse())) {
                        parallelSearch.notifyFound(this, new SearchResult(file.getName(), term, rowCount));
                    }
                } else {
                    reversedLine.append(currentChar);
                }

                pointer--;
            }

            // Imprime a última linha, se houver alguma
            if (!reversedLine.isEmpty()) {
                rowCount++;
                if (term.contentEquals(reversedLine.reverse())) {
                    parallelSearch.notifyFound(this, new SearchResult(file.getName(), term, rowCount));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
