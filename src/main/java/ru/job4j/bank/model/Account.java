package ru.job4j.bank.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class Account extends Id{
    @NonNull
    private String requisite;
    @NonNull
    private double balance;
    private User user;

}


