package Server;

import Model.Dot;
import Model.Snake;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;
import java.util.Random;

public class TcpConnection implements Runnable{

    private Server server;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private boolean connecting;
    public TcpConnection(Server server, Socket socket){
        this.server = server;
        this.socket = socket;
        this.connecting = true;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        try {
            while (connecting){
                String message = dataInputStream.readUTF();
                ServerMessage serverMessage = (ServerMessage) Helper.getObjectFromJsonString(ObjectType.SEVER_MESSAGE_OBJECT,message);
                switch (serverMessage.messageType){
                    case MessageType.GET_ID:{
                        dataOutputStream.writeUTF(server.getId()+"");
                        break;
                    }
                    case MessageType.GET_IP_ID_PORT:{
                        String ipString = socket.getInetAddress().getHostName();
                        InetAddress clientIp = InetAddress.getByName(ipString);
                        server.addressBook(clientIp,serverMessage.port);
                        break;
                    }
                    case MessageType.SEND_MAIN_CHARACTER:{
                        String code = serverMessage.data;
                        Snake snake  = serverMessage.character;
                        server.includeCharacter(snake,code);
                        dataOutputStream.writeBoolean(server.getCharacterIsRemovedByCharacterID(code,(int)snake.Id));
                        break;
                    }
                    case MessageType.CHANGE_MAP_BY_TITLE:{
                        Server.isMapChanging = true;
                        String roomCode = serverMessage.data;
                        //server.getTitles().reamoveDot(serverMessage.character.dots.get(0));D
                        Dot tile = serverMessage.character.dots.get(0);
                        server.updateMapInRoomGameByRoomCode(roomCode,tile);
                        dataOutputStream.writeBoolean(true);
                        Server.isMapChanging = false;
                        break;
                    }
                    case MessageType.SEND_MAIN_CHARACTER_NAME:{
                        String userName = serverMessage.data;
                        int userID = serverMessage.id;
                        server.setUserNameAtId(userID, userName);

                        break;
                    }
                    
                    case MessageType.GET_LIST_UDP_PORT:{
                        List<Integer> listUPDPort = server.getListUDPPort();
                        String listUDPPortString = Helper.getJsonStringFromObject(listUPDPort);
                        dataOutputStream.writeUTF(listUDPPortString);
                        break;
                    }
                    case MessageType.GET_LIST_CONECTING_USER:{
                        List<String> listUser = server.getListUserOnlines(serverMessage.id);
                        String jsonString = Helper.getJsonStringFromObject(listUser);
                        dataOutputStream.writeUTF(jsonString);
                        break;
                    }
                    
                    case MessageType.CREATE_GAME_ROOM:{
                        String code = generateGameRoomCode();
                        Snake character = new Snake();

                        character.Id = serverMessage.id;
                        character.name = serverMessage.data;
                        int udpPort = serverMessage.port;
                        server.updateActiveClient(code,udpPort);
                        server.createRoomGame(code,character);
                        dataOutputStream.writeUTF(code);
                        break;
                    }
                    case MessageType.GET_PLAYER_IN_GAME_ROOM:{
                        List<CharacterObjInfo> characterObjInfo = server.getListPlayerInRoomByRoomCode(serverMessage.data);
                        String data = Helper.getJsonStringFromObject(characterObjInfo);
                        dataOutputStream.writeUTF(data);
                        break;
                    }

                    case MessageType.JOIN_GAME_ROOM:{
                        String[] data = serverMessage.data.split("#");
                        String code = data[0];
                        String name = data[1];
                        int udpPort = serverMessage.port;

                        Snake character = new Snake();
                        character.name  = name;
                        character.code = code;

                        server.updateActiveClient(code,udpPort);
                        int idInRoomgame = server.joinToRoomGame(code,character);
                        server.updateListPlayerOnlineComeToRoomGame(character,code);
                        dataOutputStream.writeInt(idInRoomgame);
                        break;
                    }

                    case MessageType.GET_IS_ROOM_MASTER_START_GAME:{
                        String roomCode = serverMessage.data;
                        boolean isRoomGameAreRunning = server.checkIsRoomGameAreRunningByRoomGameCode(roomCode);
                        dataOutputStream.writeBoolean(isRoomGameAreRunning);
                        break;
                    }

                    case MessageType.SEND_START_ROOM_GAME:{
                        String roomCode = serverMessage.data;
                        server.setRoomGameAreRunningByRoomCode(roomCode);
                        break;
                    }

                    case MessageType.SEND_LEAVE_ROOM:{
                        String roomCode = serverMessage.data;
                        int idInRoomGame = serverMessage.id;
                        server.playerLeaveRoomGame(roomCode,idInRoomGame);
                        break;
                    }
                    case MessageType.SEND_DISCONECTING:{
                        int clientId = serverMessage.id;
                        server.removeAcitveClient(clientId);
                        this.connecting = false;
                        System.out.println("connecting " + connecting);
                        break;
                    }
                    case MessageType.GET_CHARACTER: {
                        break;
                    }
                    case MessageType.GET_MAP:{
                        break;
                    }
                    case MessageType.REMOVE_CHARACTER:{
                        break;
                    }
                    default:{
                        break;
                    }
                }

            }

            System.out.println("leave");
            //socket.close();
            dataInputStream.close();
            dataOutputStream.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateGameRoomCode() {
        String code = "";
        Random rand = new Random();
        
        char firsChar = (char)(rand.nextInt(26) + (int)'a');
        char secondChar = (char)(rand.nextInt(26) + (int)'a');
        char thirdChar = (char)(rand.nextInt(26) + (int)'a');
        char fourthChar = (char)(rand.nextInt(26) + (int)'a');
        
        code += firsChar;
        code += secondChar;
        code += thirdChar;
        code+= fourthChar;
        return  code;
    }


}
