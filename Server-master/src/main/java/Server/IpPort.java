package Server;

import java.net.InetAddress;

public class IpPort {
    public boolean inRooGame;
    public String code;
    public InetAddress address;
    public int port;

    public IpPort(InetAddress address, int port) {
        this.address = address;
        this.port = port;
        inRooGame = false;
        code = null;
    }

    public void updateStateInRoomGame(String code){
        this.inRooGame = true;
        this.code = code;
    }

    public void updateStateLeaveRoomGame(){
        this.inRooGame = false;
        this.code = null;
    }

}
