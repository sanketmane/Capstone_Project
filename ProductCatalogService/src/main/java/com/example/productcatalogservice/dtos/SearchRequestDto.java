package com.example.productcatalogservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SearchRequestDto {

    private String searchString;
    private Integer pageNumber; // indicates page no. you want to return
    private Integer pageSize; // indicates no. of elements to return in a page
    private List<SortParam> sortParamList = new ArrayList<>();

    /*
    {
    "searchString": "iphone16",
    "pageNumber": 1, => return 1st page, starts from 0
    "pageSize": 2, => return 2 elements for a given page, starts from 1
    "sortParamList": [
        {
            "sortCriteria": "price",
            "sortType": "DESC"
        },
        {
            "sortCriteria": "id",
            "sortType": "ASC"
        }
    ]
}

     */

}
