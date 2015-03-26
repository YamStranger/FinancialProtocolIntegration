package com.orders;

import java.util.HashMap;
import java.util.Map;

/**
 * User: YAPI
 * Date: 3/25/15
 * Time: 9:37 AM
 */
public enum SideEnum {
    Buy(1), Sell(-1);
    private Integer key;
    private static Map<Integer, SideEnum> sideEnumValues;

    private SideEnum(Integer key) {
        this.key = key;
        registerNewSideEnum(this);
    }

    public Integer getKey() {
        return key;
    }

    public static SideEnum getByKey(int key) {
        return sideEnumValues.get(key);
    }

    private static void registerNewSideEnum(SideEnum sideEnum) {
        if (sideEnumValues == null) {
            sideEnumValues = new HashMap<Integer, SideEnum>();
        }
        sideEnumValues.put(sideEnum.getKey(), sideEnum);
    }

}
