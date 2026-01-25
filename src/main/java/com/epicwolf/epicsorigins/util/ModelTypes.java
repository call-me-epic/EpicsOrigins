package com.epicwolf.epicsorigins.util;

import com.epicwolf.epicsorigins.Epicsorigins;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class ModelTypes {

    public static ArrayList<Pair<Identifier, List<String>>> IDENTIFIERS = new ArrayList<>();

    public static final Identifier WINGS = register("look/wings", List.of("body", "wings"));
    public static final Identifier FOX_TAIL = register("look/fox_tail", List.of("body", "fox_tail"));
    public static final Identifier MERMAID_TAIL = register("look/mermaid_tail", List.of("mermaid_tail"));
    public static final Identifier DEMON_TAIL = register("look/demon_tail", List.of("body", "demon_tail"));
    public static final Identifier FOX_EARS = register("look/fox_ears", List.of("head", "fox_ears"));
    public static final Identifier DEMON_HORNS = register("look/demon_horns", List.of("head", "demon_horns"));

    public static Identifier register(String identifier, List<String> root) {
        Identifier i = Epicsorigins.identifier(identifier);
        IDENTIFIERS.add(new Pair<>(i, root));
        return i;
    }
    public static Identifier register(Identifier identifier, List<String> root) {
        IDENTIFIERS.add(new Pair<>(identifier, root));
        return identifier;
    }
}
