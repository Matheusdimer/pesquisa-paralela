package com.unesc.pesquisa;

import com.unesc.pesquisa.view.MainWindow;

public class Main {
    public static void main(String[] args) {
        new MainWindow((folder, searchValue, searchType) -> searchType.getSearchService().search(folder, searchValue));
    }
}
