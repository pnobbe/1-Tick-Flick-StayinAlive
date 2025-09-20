package com.onetickflick.sound;

import lombok.Getter;

import java.util.Random;

public enum Sound {
    STAYIN_ALIVE("BeeGeesStayinAliveOSRS.mid");

    @Getter
    private final String resourceName;

    Sound(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String toString() {
        return resourceName;
    }
}
