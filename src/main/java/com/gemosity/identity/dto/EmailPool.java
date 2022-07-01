package com.gemosity.identity.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmailPool {
    private long id;
    private String name;
    private List<EmailPoolRange> poolRangeList;
}
