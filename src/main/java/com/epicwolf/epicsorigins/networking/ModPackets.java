package com.epicwolf.epicsorigins.networking;

import com.epicwolf.epicsorigins.Epicsorigins;
import io.github.apace100.origins.Origins;
import net.minecraft.util.Identifier;

public class ModPackets {

    public static final Identifier OPEN_VIEW_ORIGIN_SCREEN = Origins.identifier("open_view_origin_screen");
    public static final Identifier OPEN_PLAYER_LOOK_SCREEN = Epicsorigins.identifier("open_player_look_screen");
    public static final Identifier TOGGLE_PLAYER_LOOK_POWER = Epicsorigins.identifier("toggle_player_look_power");
    public static final Identifier UPDATE_PLAYER_LOOK_TEXTURE = Epicsorigins.identifier("update_player_look_texture");
    public static final Identifier SEND_PLAYER_LOOK_TEXTURE = Epicsorigins.identifier("send_player_look_texture");
}
