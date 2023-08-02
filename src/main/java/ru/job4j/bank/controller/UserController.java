package ru.job4j.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.job4j.bank.model.User;
import ru.job4j.bank.service.BankService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 1)@ExceptionHandler позволяет отслеживать и обрабатывать
 * исключения на уровне класса. Если использовать ее
 * например в контроллере, то исключения только данного
 * контроллера будут обрабатываться.
 * 2) value = { IllegalArgumentException.class } указывает,
 * что обработчик будет обрабатывать только данное исключение.
 * Можно перечислить их больше, т.к. value это массив.
 * 3) Метод, помеченный как @ExceptionHandler, поддерживает
 * внедрение аргументов и возвращаемого типа в рантайме,
 * указанных в спецификации. По этому мы можем внедрить запрос,
 * ответ и само исключение, чтобы прописать какую-либо логику.
 * 3) В данном случае при возникновении исключения
 * IllegalArgumentException, метод exceptionHandler()
 * отлавливает его и меняет ответ, а именно его статус и тело.
 * Также в последней строке происходит логгирование.
 * 4) Недостаток этого подхода в том, что он работает только
 * для класса, в котором он используется, поэтому
 * его стоит использовать только для узко специфичных исключений
 */
@RestController
@RequestMapping("/user")
@Data
public class UserController {
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class.getSimpleName());
    private final BankService bankService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public User save(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            throw new NullPointerException("Username and password mustn't be empty");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Invalid password. Password length must be more than 5 characters.");
        }
        var user = new User(username, password);
        bankService.addUser(user);
        return user;
    }

    @GetMapping
    public User findByPassport(@RequestParam String password) {
        return bankService.findByPassport(password).orElse(null);
    }

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public void exceptionHandler(Exception e, HttpServletRequest request,
                                 HttpServletResponse response)  throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(new HashMap<>() { {
            put("message", e.getMessage());
            put("type", e.getClass());
        }}));
        LOG.error(e.getLocalizedMessage());
    }

}
