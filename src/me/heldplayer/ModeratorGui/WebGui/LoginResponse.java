
package me.heldplayer.ModeratorGui.WebGui;

import java.io.IOException;

import me.heldplayer.ModeratorGui.ModeratorGui;

public class LoginResponse extends WebResponse {
    private String username;
    private String password;

    public LoginResponse(String username, String password) throws IOException {
        super();
        this.username = username;
        this.password = password;
    }

    @Override
    public WebResponse writeResponse(RequestFlags flags) throws IOException {
        dop.writeBytes("HTTP/1.0 200 Ok\r\n");
        dop.writeBytes("Connection: close\r\n");
        dop.writeBytes("Server: ModeratorGui\r\n");
        dop.writeBytes("Content-Type: text/plain\r\n");
        dop.writeBytes("\r\n");

        String password = ModeratorGui.getPasswordForUsername(username);

        if (password != null && password.equals(this.password)) {
            String session = ThreadWebserver.instance.createSession(username);

            dop.writeBytes(session);
            
            for(int i = 0; i < ModeratorGui.instance.ranks.size(); i++){
                dop.writeBytes("/" + ModeratorGui.instance.ranks.get(i));
            }
        }
        else {
            dop.writeBytes("invalid");
        }

        return this;
    }
}
