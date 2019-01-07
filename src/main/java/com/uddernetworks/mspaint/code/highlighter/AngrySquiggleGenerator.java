package com.uddernetworks.mspaint.code.highlighter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;

public class AngrySquiggleGenerator {

    private BufferedImage generatedPNG;

    public AngrySquiggleGenerator(int fontSize) throws TranscoderException {
        double squiggleHeight = fontSize / 3D;
        squiggleHeight /= 2;

        AngrySquiggleTranscoder transcoder = new AngrySquiggleTranscoder();
        transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) (int) Math.floor(squiggleHeight));

        TranscoderInput input = new TranscoderInput(getClass().getClassLoader().getResourceAsStream("angry_squiggle.svg"));
        transcoder.transcode(input, new AngrySquiggleTranscoderOutput());
        this.generatedPNG = transcoder.getImage();

        System.out.println("Generated angry squiggle from SVG. Dimensions: " + this.generatedPNG.getWidth() + " x " + this.generatedPNG.getHeight());
    }

    public BufferedImage getGeneratedPNG() {
        return generatedPNG;
    }
}
