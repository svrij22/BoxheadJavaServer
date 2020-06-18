package nl.hu.bep.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nl.hu.bep.model.Player;

import java.security.Principal;
import java.util.ArrayList;

public class Account implements Principal {
    private static ArrayList<Account> accounts = new ArrayList<>();

    private String name;
    @JsonIgnore private String password;
    private String rol;
    private Player player;

    public Account(String name, String password, String rol, Player player) {
        this.name = name;
        this.password = password;
        this.rol = rol;
        this.player = player;
        if (!accounts.contains(this)) accounts.add(this);
    }

    public Player getPlayer() {
        return player;
    }



    public static Account getAccountByName(String name) {
        return accounts.stream()
                .filter(a->a.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    public static void setAccounts(ArrayList<Account> accounts) {
        Account.accounts = accounts;
    }

    public static ArrayList<Account> geefAlle() {
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
