package uk.tw.energy.assertj;

import java.util.List;

public class User {
    private int id;
    private String name;
    List<UserDetails> detailsList;

    public User(int id, String name, List<UserDetails> detailsList) {
        this.id = id;
        this.name = name;
        this.detailsList = detailsList;
    }
}
