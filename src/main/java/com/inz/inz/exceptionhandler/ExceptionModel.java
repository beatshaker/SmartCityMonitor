package com.inz.inz.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionModel extends Exception {
    private String caused;
    private String code;
    private Field field;

}
