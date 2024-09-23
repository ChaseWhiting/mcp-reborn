package net.minecraft.client.audio;

import java.util.Random;

public class StaticSoundFilter {

    public static byte[] addStatic(byte[] audioData, float staticAmount) {
        Random random = new Random();
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = (byte) ((audioData[i] * (1 - staticAmount)) + (random.nextFloat() * 2 - 1) * staticAmount * 127);
        }
        return audioData;
    }

    public static byte[] processAudio(byte[] audioData, float radiationLevel) {
        float staticAmount = 7; // Adjust this calculation based on your needs
        return addStatic(audioData, staticAmount);
    }
}
