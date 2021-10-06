package xyz.ludwicz.townykoth.loot;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Loot {

    @Getter
    private String name;
    @Getter
    private List<String> commands;

    public Loot(String name) {
        this.name = name;

        commands = new ArrayList<>();
    }
}
