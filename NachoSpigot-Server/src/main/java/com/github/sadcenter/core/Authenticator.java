package com.github.sadcenter.core;

import com.mojang.authlib.AuthenticationService;

import java.io.IOException;
import java.net.URL;

public interface Authenticator extends AuthenticationService {

    String fetchGet(URL url) throws IOException;

    String fetchPost(URL url, String content) throws IOException;

}
