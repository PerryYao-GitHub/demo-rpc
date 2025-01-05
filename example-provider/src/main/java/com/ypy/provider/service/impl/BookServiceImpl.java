package com.ypy.provider.service.impl;

import com.ypy.common.model.Book;
import com.ypy.common.service.BookService;

import java.util.List;

public class BookServiceImpl implements BookService {
    @Override
    public Book getBookById(long id) {
        Book bk = new Book();
        bk.setId(id);
        bk.setAuth("xjp");
        bk.setPublishers(List.of("people's daily", "ri ren min bao"));
        return bk;
    }
}
