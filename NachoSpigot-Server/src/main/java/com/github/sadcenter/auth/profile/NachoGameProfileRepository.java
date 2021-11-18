package com.github.sadcenter.auth.profile;

import com.github.sadcenter.auth.NachoAuthenticatorService;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;

public class NachoGameProfileRepository implements GameProfileRepository {

    private final NachoAuthenticatorService authenticator;

    public NachoGameProfileRepository(NachoAuthenticatorService authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback profileLookupCallback) {
        for (String name : names) {
            GameProfile gameProfile = new GameProfile(null, name);

            if (name == null || name.isEmpty()) {
                profileLookupCallback.onProfileLookupFailed(gameProfile, new Exception("Name isn't provided in the proper way."));
                continue;
            }

            this.authenticator.getUuid(name)
                    .thenApply(uuid -> new GameProfile(uuid, name))
                    .thenAccept(profileLookupCallback::onProfileLookupSucceeded)
                    .exceptionally(throwable -> {
                        profileLookupCallback.onProfileLookupFailed(gameProfile, (Exception) throwable);
                        return null;
                    });
        }
    }
}
