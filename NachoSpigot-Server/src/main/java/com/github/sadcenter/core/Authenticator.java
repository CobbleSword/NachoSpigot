package com.github.sadcenter.core;

import java.io.IOException;
import java.net.URL;

public interface Authenticator {

    String fetchGet(URL url) throws IOException;

    String fetchPost(URL url, String content) throws IOException;

}
