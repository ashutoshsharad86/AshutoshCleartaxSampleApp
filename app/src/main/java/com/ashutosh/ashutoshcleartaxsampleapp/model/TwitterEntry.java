package com.ashutosh.ashutoshcleartaxsampleapp.model;

import java.util.Map;

/**
 * Created by ashutosh.k on 7/13/2016.
 */
public class TwitterEntry {

    public Map.Entry<String, Integer> entry ;

    @Override
    public String toString(){
        return entry.getKey();
    }
}
