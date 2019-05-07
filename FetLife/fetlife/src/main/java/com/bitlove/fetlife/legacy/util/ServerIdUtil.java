package com.bitlove.fetlife.legacy.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ServerIdUtil {

    //Note: Temporarily duplicated
    private static final String SERVER_ID_PREFIX = "SERVER_ID_PREFIX:";

    private static Map<String,String> idCache = Collections.synchronizedMap(new HashMap<String,String>());

    public static boolean isServerId(String serverId) {
        return serverId.startsWith(SERVER_ID_PREFIX);
    }

    public static void setLocalId(String serverId, String localId) {
        if (!serverId.startsWith(SERVER_ID_PREFIX)) {
            serverId = SERVER_ID_PREFIX + serverId;
        }
        idCache.put(serverId,localId);
    }

    public static String getLocalId(String serverId) {
        return idCache.get(serverId);
    }

    public static boolean containsServerId(String serverId) {
        return idCache.containsKey(serverId);
    }

    public static String prefixServerId(String serverId) {
        return SERVER_ID_PREFIX + serverId;
    }

    public static String removePrefix(String serverId) {
        return serverId.substring(SERVER_ID_PREFIX.length());
    }
}
