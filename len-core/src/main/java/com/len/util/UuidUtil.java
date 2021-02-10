package com.len.util;

import java.util.UUID;

/**
 * @author zxm
 * @date 2019-11-15.
 */
public class UuidUtil {

    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }
}
