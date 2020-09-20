package svrij.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ZorgLocatie implements Serializable {
    static ArrayList<ZorgLocatie> alleLocaties = new ArrayList<>();

    public static ArrayList<ZorgLocatie> geefAlle() {
        return alleLocaties;
    }
}
