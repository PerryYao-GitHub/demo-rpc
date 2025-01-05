package com.ypy.common.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Book implements Serializable {
    private Long id;
    private String auth;
    private List<String> publishers;
}
