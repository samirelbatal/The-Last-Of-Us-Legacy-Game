package view;

import javax.sound.sampled.*;
import java.io.*;

public class soundPlayer {
    private static Clip continuousClip; // Class-level variable to keep track of the continuous sound
    private static Clip clip;
    public static void playSound(String fileName) {
        try {
            File file = new File(fileName);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
    
    public static void stopSound() {
        if (continuousClip != null && continuousClip.isRunning()) {            
            continuousClip.stop();
        }
        if (clip != null && clip.isRunning()) {            
        	clip.stop();
        }
    }
    
    public static void gameSoundTrack() {
        try {
            File file = new File("backgroundTrack.wav");
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
            continuousClip = AudioSystem.getClip(); // Assign to class-level variable
            continuousClip.open(audioInputStream);
            
            // Get the gain control
            FloatControl gainControl = (FloatControl) continuousClip.getControl(FloatControl.Type.MASTER_GAIN);
            
            // Lower the volume by 30 decibels (you can adjust this value)
            gainControl.setValue(-30.0f);
            
            continuousClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error playing sound: " + e.getMessage());
        }
    }
}
