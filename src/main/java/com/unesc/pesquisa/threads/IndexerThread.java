package com.unesc.pesquisa.threads;

import com.unesc.pesquisa.indexing.Indexer;
import com.unesc.pesquisa.view.IndexingWindow;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

public class IndexerThread extends Thread {

    private final IndexingWindow window;
    private final String folder;

    public IndexerThread(IndexingWindow window, String folder) {
        this.window = window;
        this.folder = folder;
    }

    @Override
    public void run() {
        try {
            File indexFolder = new File(folder + "/index");

            if (indexFolder.isDirectory()) {
                FileUtils.deleteDirectory(indexFolder);
            }

            Indexer indexer = new Indexer(folder + "/index");
            indexer.createIndex(folder);
            indexer.close();
            window.setVisible(false);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(window, "Erro ao indexar arquivos: " + e.getMessage());
            window.setVisible(false);
        }
    }
}
