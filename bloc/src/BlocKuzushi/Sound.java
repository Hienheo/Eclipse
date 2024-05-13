package BlocKuzushi;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.io.*;

public class Sound {
	void playMusic(String musicLoc){
        try {
                File musicPath = new File(musicLoc);
                 if(musicPath.exists()){ 
                         AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                         Clip clip = AudioSystem.getClip();
                         clip.open(audioInput);
                         clip.start();
                        JOptionPane.showMessageDialog(null,"Press ok to stop playing");

                  }
                 else{
                          System.out.println("Couldn't find Music file");
                       }
       }
       catch (Exception ex){
                  ex.printStackTrace();
            }
  }

}
