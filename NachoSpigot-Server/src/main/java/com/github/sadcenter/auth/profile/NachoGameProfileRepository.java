package com.github.sadcenter.auth.profile;

import com.github.sadcenter.auth.NachoAuthenticationService;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;

public class NachoGameProfileRepository implements GameProfileRepository {

    private final NachoAuthenticationService authenticator;

    public NachoGameProfileRepository(NachoAuthenticationService authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback) {
        for (String name : names) {
            if (name == null || name.isEmpty()) {
                continue;
            }

            GameProfile gameProfile = new GameProfile(null, name);
            this.authenticator.getUuidFromApi(name)
                    .thenApply(uuid -> new GameProfile(uuid, name))
                    .thenAccept(callback::onProfileLookupSucceeded)
                    .exceptionally(throwable -> {
                        callback.onProfileLookupFailed(gameProfile, (Exception) throwable);
                        return null;
                    });
        }
    }
}
