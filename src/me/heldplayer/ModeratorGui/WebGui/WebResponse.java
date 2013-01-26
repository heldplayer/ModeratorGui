
package me.heldplayer.ModeratorGui.WebGui;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.SocketException;
import java.util.logging.Level;

import me.heldplayer.ModeratorGui.ModeratorGui;

public abstract class WebResponse {
    protected PipedInputStream pip;
    protected PipedOutputStream pop;
    protected DataOutputStream dop;

    public WebResponse() throws IOException {
        pop = new PipedOutputStream();
        pip = new PipedInputStream(pop, 16384);
        dop = new DataOutputStream(pop);
    }

    public abstract WebResponse writeResponse(RequestFlags flags) throws IOException;

    public void flush(DataOutputStream stream) throws IOException {
        try {
            while (pip.available() > 0) {
                int bits = pip.read();
                stream.write(bits);
            }
        }
        catch (SocketException ex) {
            ModeratorGui.log.log(Level.WARNING, "Tried displaying page to a client, but the client closed the connection!", ex);
        }
        catch (IOException ex) {
            ModeratorGui.log.log(Level.WARNING, "Tried displaying page to a client, but an error occoured", ex);
        }
        finally {
            try {
                dop.close();
                pop.close();
                pip.close();
            }
            catch (IOException ex) {}
        }
    }
}
