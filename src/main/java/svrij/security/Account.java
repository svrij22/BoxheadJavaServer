package svrij.security;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.security.Principal;
import java.util.ArrayList;

public class Account implements Principal {
    private static ArrayList<Account> accounts = new ArrayList<>();

    private String name;
    @JsonIgnore private String password;
    private String rol;

    public Account(String name, String password, String rol) {
        this.name = name;
        this.password = password;
        this.rol = rol;
        if (!accounts.contains(this)) accounts.add(this);
    }

    public static Account getAccountByName(String name) {
        return accounts.stream()
                .filter(a->a.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public static ArrayList<?> geefAlle() {
        return accounts;
    }

    public static String validateLogin(String name, String pw) {
        Account find = getAccountByName(name);
        if (find != null) return pw.equals(find.password) ? find.getRole() : null;
        return null;
    }

    public String getRole() {
        return rol;
    }

    public String getName() {
        return name;
    }
}
