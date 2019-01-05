package com.uddernetworks.mspaint.code.highlighter;

import org.apache.batik.transcoder.TranscoderOutput;

import java.io.OutputStream;

public class AngrySquiggleTranscoderOutput extends TranscoderOutput {

    @Override
    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override
            public void write(int b) {

            }
        };
    }
}
