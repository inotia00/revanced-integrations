package app.revanced.integrations.youtube.whitelist;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import app.revanced.integrations.shared.utils.Logger;

public class ObjectSerializer {

    public static String serialize(Serializable obj) throws IOException {
        if (obj == null)
            return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
            ObjectOutputStream objStream = new ObjectOutputStream(new DeflaterOutputStream(
                    serialObj, def));
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
            Logger.printException(() -> "Serialization error: " + e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public static Object deserialize(String str) throws IOException {
        if (str == null || str.length() == 0)
            return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(new InflaterInputStream(
                    serialObj));
            return objStream.readObject();
        } catch (Exception e) {
            Logger.printException(() -> "Deserialization error: " + e.getMessage(), e);
            throw new IOException(e);
        }
    }

    public static String encodeBytes(byte[] bytes) {
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decodeBytes(String str) throws UnsupportedEncodingException {
        return Base64.getDecoder().decode(str.getBytes(Charset.forName("UTF-8")));
    }
}
