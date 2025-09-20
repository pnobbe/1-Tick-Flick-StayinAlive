package com.onetickflick.sound;

import com.onetickflick.OneTickFlickConfig;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.audio.AudioPlayer;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Singleton
@Slf4j
public class SoundEngine {

    @Inject
    private OneTickFlickConfig config;

    @Inject
    private AudioPlayer audioPlayer;

    private BufferedInputStream playingAudio;

    public void playClip(Sound sound, Executor executor) {
        executor.execute(() -> playClip(sound));
    }

    public void playClip(Sound sound, ScheduledExecutorService executor, Duration initialDelay) {
        executor.schedule(() -> playClip(sound), initialDelay.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void playClip(Sound sound) {
        float gain = 20f * (float) Math.log10(config.musicVolume() / 100f);

        try {
            audioPlayer.play(SoundFileManager.getSoundFile(sound), gain);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            log.warn("Failed to load One Tick sound {}", sound, e);
        }
    }

    public void stopStream(Executor executor) {
        executor.execute(this::stopStream);
    }

    public void startStream(Sound sound, Executor executor) {
        executor.execute(() -> startStream(sound));
    }

    public boolean isPlayingAudio() {
        return playingAudio != null;
    }

    private void startStream(Sound sound) {
        if (playingAudio != null) {
            return;
        }
        float gain = 20f * (float) Math.log10(config.musicVolume() / 100f);
        try {
            playingAudio = SoundFileManager.getSoundStream(sound);
            playingAudio.mark(0);
            audioPlayer.play(playingAudio, gain);
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            log.warn("Failed to load One Tick sound {}", sound, e);
            playingAudio = null;
        }
    }

    private void stopStream() {
        if (playingAudio == null) {
            return;
        }
        try {
            playingAudio.mark(0);
            playingAudio.reset();
            playingAudio.close();
            playingAudio.skip()
        } catch (IOException e) {
            log.warn("Failed to close One Tick sound stream", e);
        }
        playingAudio = null;
    }
}
