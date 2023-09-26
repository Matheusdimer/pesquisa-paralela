package com.unesc.pesquisa.model;

@FunctionalInterface
public interface OnSearch {
    SearchResult search(String folder, String searchValue, boolean useParallel);
}
