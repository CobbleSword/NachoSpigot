package com.github.sadcenter.impl.repo;

import com.github.sadcenter.impl.NachoAuthenticatorService;
import com.mojang.authlib.*;
import com.mojang.authlib.exceptions.AuthenticationException;

import java.io.IOException;
import java.net.URL;
import java.util.UUID;
import java.util.function.Function;


public class NachoGameProfileRepository implements GameProfileRepository {

    private final NachoAuthenticatorService authenticator;

    public NachoGameProfileRepository(NachoAuthenticatorService authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback profileLookupCallback) {
        // TODO 2 attempts.
        for (String name : names) {
            GameProfile gameProfile = new GameProfile(null, name);

            if (name == null || name.isEmpty()) {
                profileLookupCallback.onProfileLookupFailed(gameProfile, new Exception("Name isn't provided in the proper way."));
                continue;
            }

            authenticator.getUuid(name)
                    .thenApply(uuid -> new GameProfile(uuid, name))
                    .thenAccept(profileLookupCallback::onProfileLookupSucceeded)
                    .exceptionally(throwable -> {
                        profileLookupCallback.onProfileLookupFailed(gameProfile, (Exception) throwable);
                        return null;
                    });
        }
    }
}
