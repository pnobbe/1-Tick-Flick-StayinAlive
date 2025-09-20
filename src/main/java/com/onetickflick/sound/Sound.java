package com.onetickflick.sound;

import lombok.Getter;

public enum Sound {
    STAYIN_ALIVE("BeeGeesStayinAliveOSRS.wav");

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
