package org.fitness.userservice.exceptions;


import org.fitness.userservice.responses.ResponseGlobal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseGlobal> handleNotValid(MethodArgumentNotValidException ex){
        ResponseGlobal global = new ResponseGlobal();
        String message = ex.getBindingResult().getFieldErrors().stream().map(x -> x.getDefaultMessage()).findFirst().orElse("Validation Failed");
        global.setMessage(message);
        global.setData(null);

        return new ResponseEntity<>(global, HttpStatus.BAD_REQUEST);
    }
}
