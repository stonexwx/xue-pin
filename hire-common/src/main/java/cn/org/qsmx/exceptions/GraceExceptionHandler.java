package cn.org.qsmx.exceptions;

import cn.org.qsmx.result.GraceJSONResult;
import cn.org.qsmx.result.ResponseStatusEnum;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.security.SignatureException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GraceExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseBody
    public GraceJSONResult returnMaxUploadSizeExceededException(MaxUploadSizeExceededException e){
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.FILE_MAX_SIZE_500KB_ERROR);
    }

    @ExceptionHandler(ArithmeticException.class)
    @ResponseBody
    public GraceJSONResult returnArithmeticException(ArithmeticException e){
        e.printStackTrace();
        return GraceJSONResult.errorMsg(e.getMessage());
    }

    @ExceptionHandler(MyCustomException.class)
    @ResponseBody
    public GraceJSONResult returnMyCustomException(MyCustomException e){

        return GraceJSONResult.exception(e.getResponseStatusEnum());
    }

    @ExceptionHandler({
            SignatureException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            io.jsonwebtoken.security.SignatureException.class
    })
    @ResponseBody
    public GraceJSONResult returnSignatureException(SignatureException e){
        e.printStackTrace();
        return GraceJSONResult.exception(ResponseStatusEnum.JWT_SIGNATURE_ERROR);
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
