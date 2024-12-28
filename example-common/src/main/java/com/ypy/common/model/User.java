package com.ypy.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * user model
 */
public class User implements Serializable {
    @Getter
    @Setter
    private String name;
}
