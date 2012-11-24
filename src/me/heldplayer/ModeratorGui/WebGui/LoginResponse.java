
package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.heldplayer.ModeratorGui.ModeratorGui;

public class LoginResponse extends WebResponse {
    private String request;

    public LoginResponse(String request) throws IOException {
        super();
        this.request = request;
    }

    @Override
    public WebResponse writeResponse(RequestFlags flags) throws IOException {
        dop.writeBytes("HTTP/1.0 200 Ok\r\n");
        dop.writeBytes("Connection: close\r\n");
        dop.writeBytes("Server: ModeratorGui\r\n");
        dop.writeBytes("Content-Type: text/plain\r\n");
        dop.writeBytes("\r\n");

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        }
        catch (NoSuchAlgorithmException e) {
            throw new InternalServerException(e);
        }
        String text = ModeratorGui.instance.getConfig().getString("web-password");

        md.update(text.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        byte[] digest = md.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashtext = bigInt.toString(16);
        // Now we need to zero pad it if you actually want the full 32 chars.
        while (hashtext.length() < 32) {
            hashtext = "0" + hashtext;
        }

        if (request.equals(hashtext)) {
            String session = ThreadWebserver.instance.createSession();

            dop.writeBytes(session);
        }
        else {
            dop.writeBytes("invalid");
        }

        return this;
    }
}
