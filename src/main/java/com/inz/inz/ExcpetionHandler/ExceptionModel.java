package com.inz.inz.ExcpetionHandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionModel extends Throwable {
    private String caused;
    private String code;
    private Field field;

}
