package cn.org.qsmx.exceptions;

import cn.org.qsmx.result.ResponseStatusEnum;

public class GraceException {
    public static void display(ResponseStatusEnum responseStatusEnum){
        throw new MyCustomException(responseStatusEnum);
    }
}
