package com.uddernetworks.mspaint.code.highlighter;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.awt.image.BufferedImage;

public class AngrySquiggleTranscoder extends PNGTranscoder {

    private BufferedImage image;

    public BufferedImage getImage() {
        return this.image;
    }

    @Override
    public void writeImage(BufferedImage image, TranscoderOutput output) throws TranscoderException {
        super.writeImage(image, output);
        this.image = image;
    }

}
