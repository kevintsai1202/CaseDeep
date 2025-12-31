package com.casemgr.response;

import java.util.List;

public class PaginatedUserRankingResponse {
    private List<UserRankingResponse> rankings;
    private int currentPage;
    private int totalPages;
    private long totalItems;

    public PaginatedUserRankingResponse() {}

    public PaginatedUserRankingResponse(List<UserRankingResponse> rankings, int currentPage, int totalPages, long totalItems) {
        this.rankings = rankings;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }

    public List<UserRankingResponse> getRankings() {
        return rankings;
    }

    public void setRankings(List<UserRankingResponse> rankings) {
        this.rankings = rankings;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(long totalItems) {
        this.totalItems = totalItems;
    }
}