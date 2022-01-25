package com.suneo.flag.handler;

import java.util.Map;

public interface Handler {
    Map<String, Object> handle(Map<String, Object> params);
    boolean valid(Map<String, Object> params);
}
