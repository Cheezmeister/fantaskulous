package com.luchenlabs.fantaskulous;

import java.io.InputStream;
import java.io.OutputStream;

public interface NookOrCranny {

    InputStream fetchMeAnInputStream();

    OutputStream fetchMeAnOutputStream();

}