package com.github.sadcenter.impl.utils;

import com.mojang.authlib.GameProfile;

import java.util.Objects;

public final class ProfileUtil {

    private ProfileUtil() {
    }

    public static boolean equals(GameProfile first, GameProfile second) {
        return
                Objects.equals(first.getName(), second.getName()) &&
                        Objects.equals(first.getId(), second.getId()) &&
                        Objects.equals(first.getProperties(), second.getProperties());
    }

}
