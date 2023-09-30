package com.unesc.pesquisa.services;

import com.unesc.pesquisa.model.SearchResult;
import com.unesc.pesquisa.threads.SearchBackwardsThread;
import com.unesc.pesquisa.threads.SearchThread;
import com.unesc.pesquisa.util.TxtFilter;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParallelSearch extends AbstractSearchService {

    private boolean found = false;
    private SearchResult searchResult;
    private final Set<Thread> threadPoll = ConcurrentHashMap.newKeySet();
    private int linesTraveled = 0;
    private boolean useBackwards = false;

    public ParallelSearch(boolean useBackwards) {
        this.useBackwards = useBackwards;
    }

    @Override
    protected SearchResult runSearch(String folder, String term) {
        try {
            File fileFolder = new File(folder);
            File[] textFiles = fileFolder.listFiles(new TxtFilter());

            if (textFiles == null) {
                return SearchResult.notFound(folder, term);
            }

            for (File textFile : textFiles) {
                SearchThread searchThread = new SearchThread(this, textFile, term);
                threadPoll.add(searchThread);
                searchThread.start();

                if (useBackwards) {
                    SearchBackwardsThread searchBackwardsThread = new SearchBackwardsThread(this, textFile, term);
                    threadPoll.add(searchBackwardsThread);
                    searchBackwardsThread.start();
                }
            }

            for (Thread thread : threadPoll) {
                thread.join();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return searchResult == null ? SearchResult.notFound(folder, term) : searchResult;
    }

    public void notifyFound(Thread callerThread, SearchResult searchResult) {
        synchronized (this) {
            this.found = true;
            this.searchResult = searchResult.withLinesTraveled(linesTraveled);
        }

        for (Thread thread : threadPoll) {
            if (thread != callerThread) {
                thread.stop();
            }
        }
    }

    public synchronized void incrementLinesTraveled() {
        this.linesTraveled++;
    }

    public boolean isFound() {
        return found;
    }
}
