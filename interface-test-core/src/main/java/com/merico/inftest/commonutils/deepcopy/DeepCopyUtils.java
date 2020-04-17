package com.merico.inftest.commonutils.deepcopy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DeepCopyUtils {
    private static Logger logger = LoggerFactory.getLogger(DeepCopyUtils.class);

    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            FastByteArrayOutputStream fbos =
                    new FastByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(fbos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in =
                    new ObjectInputStream(fbos.getInputStream());
            obj = in.readObject();
        } catch (IOException e) {
            logger.error("deep copy error {}", e.getMessage(), e);
        } catch (ClassNotFoundException cnfe) {
            logger.error("{}", cnfe.getMessage(), cnfe);
        }
        return obj;
    }

}
