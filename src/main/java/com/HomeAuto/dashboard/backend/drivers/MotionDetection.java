package com.HomeAuto.dashboard.backend.drivers;

/**
 * Created by Rich on 06/12/2015.
 */
import java.io.IOException;

import com.HomeAuto.dashboard.backend.drivers.LtiCivil.LtiCivilDriver;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;

public class MotionDetection implements WebcamMotionListener {

    public MotionDetection() {
        Webcam.setDriver(new LtiCivilDriver());
        WebcamMotionDetector detector = new WebcamMotionDetector(Webcam.getDefault());
        detector.setInterval(100); // one check per 100 ms
        detector.addMotionListener(this);
        detector.start();
    }

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
        System.out.println("Detected motion I, alarm turn on you have");
    }

    public static void main() throws IOException {
        new MotionDetection();
        System.in.read(); // keep program open
    }
}
