package com.pallaud.nytimessearch;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by pallaud on 6/23/16.
 */
@Parcel
public class SearchFilter {
    String query;
    String sort;
    String begin_date;
    ArrayList<String> newsDeskOpts;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public ArrayList<String> getNewsDeskOpts() {
        return newsDeskOpts;
    }

    public void setNewsDeskOpts(ArrayList<String> newsDeskOpts) {
        this.newsDeskOpts = newsDeskOpts;
    }

    public SearchFilter() {
    }

    public SearchFilter(String query, String sort, String begin_date, ArrayList<String> newsDeskOpts) {
        this.sort = sort;
        this.begin_date = begin_date;
        this.newsDeskOpts = newsDeskOpts;
        this.query = query;
    }
}
