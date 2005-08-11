package com.opensymphony.sitemesh;

import java.io.PrintWriter;

public interface Content {

    /**
     * Write out the original unprocessed content.
     */
    void writeOriginal(PrintWriter writer);

    /**
     * Length of the original unprocessed content.
     */
    int originalLength();
}
