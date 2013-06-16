
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
        out.writeBytes("HTTP/1.0 200 Ok\r\n");
        out.writeBytes("Connection: close\r\n");
        out.writeBytes("Server: ModeratorGui\r\n");
        out.writeBytes("Content-Type: text/plain\r\n");
        out.writeBytes("\r\n");

        String password = ModeratorGui.getPasswordForUsername(username);

        if (password != null && password.equals(this.password)) {
            String session = ThreadWebserver.instance.createSession(username);

            out.writeBytes(session);
            
            for(int i = 0; i < ModeratorGui.instance.ranks.size(); i++){
                out.writeBytes("/" + ModeratorGui.instance.ranks.get(i));
            }
        }
        else {
            out.writeBytes("invalid");
        }

        return this;
    }
}
