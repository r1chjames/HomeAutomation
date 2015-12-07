package com.HomeAuto.dashboard.backend.drivers;

import com.HomeAuto.dashboard.backend.drivers.speech.microphone.Microphone;
import com.HomeAuto.dashboard.backend.drivers.speech.recognizer.Recognizer;
import com.HomeAuto.dashboard.backend.drivers.speech.recognizer.GoogleResponse;
import net.sourceforge.javaflacencoder.FLACFileWriter;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import java.io.File;

public class Speech {

    public static Integer Listen() throws Exception{
        Integer retVal = 0;

//        AudioFileFormat.Type[] typeArray = AudioSystem.getAudioFileTypes();
//        for(AudioFileFormat.Type type : typeArray) {
//            System.out.println("type: " + type.toString());
//        }

        Microphone mic = new Microphone(FLACFileWriter.FLAC);

        File file = new File("micrec.flac");
        mic.open();
        try {
            mic.captureAudioToFile (file);
        } catch (Exception ex) {
            //microphone not available or some other error.
            System.out.println ("ERROR: microphone is not available.");
            ex.printStackTrace ();
        }
        try {
            System.out.println ("Recording...");
            Thread.sleep (5000);	//In our case, we'll just wait 5 seconds.
            mic.close ();
        } catch (InterruptedException ex) {
            ex.printStackTrace ();
        }
        mic.close();
        System.out.println ("Recording stopped.");

        Recognizer recognizer = new Recognizer (Recognizer.Languages.ENGLISH_UK, "AIzaSyBrRHPtlYyMYa11rBVWnqhsHXSzpCqUZZ8"); // System.getProperty("google-api-key"));
        try {
            int maxNumOfResponses = 4;
            System.out.println("Sample rate is: " + (int) mic.getAudioFormat().getSampleRate());
            GoogleResponse response = recognizer.getRecognizedDataForFlac (file, maxNumOfResponses, (int) mic.getAudioFormat().getSampleRate ());
            System.out.println ("Google Response: " + response.getResponse ());
            System.out.println ("Google is " + Double.parseDouble (response.getConfidence ()) * 100 + "% confident in" + " the reply");
            System.out.println ("Other Possible responses are: ");
            for (String s:response.getOtherPossibleResponses ()) {
                System.out.println ("\t" + s);
            }
            retVal = 1;
        }
        catch (Exception ex) {
            // TODO Handle how to respond if Google cannot be contacted
            System.out.println ("ERROR: Google cannot be contacted");
            ex.printStackTrace ();
            retVal = 2;
        }

        file.deleteOnExit ();	//Deletes the file as it is no longer necessary.
        return retVal;
    }
}
