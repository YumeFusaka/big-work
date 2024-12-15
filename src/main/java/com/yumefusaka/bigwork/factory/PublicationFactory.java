package com.yumefusaka.bigwork.factory;

import com.yumefusaka.bigwork.model.Book;
import com.yumefusaka.bigwork.model.Magazine;
import com.yumefusaka.bigwork.model.Publication;

public class PublicationFactory {
    public static Publication createPublication(String type) {
        switch (type.toUpperCase()) {
            case "BOOK":
                return new Book();
            case "MAGAZINE":
                return new Magazine();
            default:
                throw new IllegalArgumentException("Unknown publication type");
        }
    }
} 