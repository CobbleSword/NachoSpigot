package com.github.sadcenter.impl.utils;

import com.mojang.authlib.GameProfile;

public final class ProfileUtil {

    private ProfileUtil() {
    }

    public static boolean equals(GameProfile first, GameProfile second) {
        return
                first.getName().equalsIgnoreCase(second.getName()) &&
                        first.getId().equals(second.getId()) &&
                        first.getProperties().equals(second.getProperties());

    }

}
