package cn.org.qsmx.exceptions;

import cn.org.qsmx.result.GraceJSONResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GraceExceptionHandler {
    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e){

        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MethodArgumentNotValidException e){
        BindingResult result = e.getBindingResult();
        Map<String,String> map = getErrors(result);
        return GraceJSONResult.errorMap(map);
    }

    public Map<String, String> getErrors(BindingResult result){
        List< FieldError> errorList = result.getFieldErrors();
        Map<String,String> map= new HashMap<>();
        for (FieldError fe : errorList){
            //错误所对应的属性字段名
            String field = fe.getField();
            //错误信息
            String message = fe.getDefaultMessage();
            map.put(field,message);
        }
        return map;
    }
}
