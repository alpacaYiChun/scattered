package com.suneo.flag.handler;

import java.util.Map;

public interface Handler {
    public Map<String, Object> handle(Map<String, Object> params);
}
