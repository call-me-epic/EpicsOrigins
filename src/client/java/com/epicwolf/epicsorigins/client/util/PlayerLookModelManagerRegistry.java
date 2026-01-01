package com.epicwolf.epicsorigins.client.util;

import java.util.ArrayList;

public class PlayerLookModelManagerRegistry {

    public static ArrayList<PlayerLookModelManager> modelManagers = new ArrayList<>();

    public void register(PlayerLookModelManager manager) {
        modelManagers.add(manager);
    }

}
