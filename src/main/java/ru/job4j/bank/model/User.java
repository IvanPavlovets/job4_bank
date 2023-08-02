package ru.job4j.bank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Data
public class User extends Id{
    @NonNull
    private String passport;
    @NonNull
    private String username;
    private List<Account> accounts = new CopyOnWriteArrayList<>();

}
