package Server;

import Define.SpriteType;
import Define.WorlSize;
import Model.Dot;
import Model.Snake;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Server {


    private static int SERVER_TCP_PORT;
    private static final long RESHRESH_GAP = 30;
    private UdpConnectionSend udpSend;
    private Vector<Snake> fullCharacter;
    private CopyOnWriteArrayList<IpPort> activeClients;

    private List<CharacterObjInfo> listOnlinePlayer;
    private static long IDs = 0L;
    //private WrapList gameplay;
    private WrapList titles = generateMap();
    public static boolean isMapChanging = false;
    public static boolean isStarting = false;
    private List<RoomGameObject> roomGames;


    public static void main(String[] args) {
        Server server = new Server(1234);
        server.start();
    }

    public WrapList getTitles() {
        return titles;
    }

    public Server(int tcpPort) {
        SERVER_TCP_PORT = tcpPort;
        roomGames = new ArrayList<>();
        listOnlinePlayer = new ArrayList<>();
        activeClients = new CopyOnWriteArrayList<>();
        udpSend = new UdpConnectionSend();
        fullCharacter = new Vector<>();
        //gameplay = new WrapList();


    }


    public void start() {
        gameStateRefresh();
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_TCP_PORT);
            // System.out.println(serverSocke);
            Socket clientSocket;

            while ((clientSocket = serverSocket.accept()) != null) {
                new Thread(new TcpConnection(this, clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void gameStateRefresh() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isMapChanging) {
                    updateGameplay();
                    udpSend.sendGameplay();
                }
            }

            private void updateGameplay() {

                for(RoomGameObject room : roomGames){
                    room.gamePlay.clear();
                    for(Snake mainCharacter : room.fullCharacter){
                        room.gamePlay.addAll(mainCharacter.update(room.map,room.fullCharacter));
                    }
                }
            }
        }, 150, RESHRESH_GAP);
    }

    public List<Dot> update(Snake currentSnake, WrapList titles, List<Snake> fullCharacters){
        List<Dot> dots = new ArrayList<>();

        for(Snake character : fullCharacters){
            if(character.Id == currentSnake.Id){
                continue;
            }


            if(checkIsCollisionWithAnotherSnake(currentSnake,character)){
                for(RoomGameObject room: roomGames) {
                    if(currentSnake.code.equals(room.code)){
                       for(int i = 0; i < room.fullCharacter.size(); i++){
                           if(currentSnake.Id == room.fullCharacter.get(i).Id){
                               room.fullCharacter.get(i).isRemoved = true;
                               break;
                           }
                       }
                       break;
                    }
                }
            }
            if(!character.isRemoved) {
                dots.addAll(character.dots);
            }
        }

        dots.addAll(titles.dots);
        return dots;
    }


    public boolean checkIsCollisionWithAnotherSnake(Snake currentSnake, Snake snake){
        if(currentSnake.dots.size() < 1 || snake.dots.size() < 1){
            return false;
        }

        Dot head = currentSnake.dots.get(0);
        for(int indexDot = 1; indexDot < snake.dots.size(); indexDot++){
            Dot dot = snake.dots.get(indexDot);
            if(distance(head.x, head.y,dot.x,dot.y)  < 16){
                return true;
            }
        }
        return false;
    }

    public float distance(float xFirstPoint, float yFirstPoint, float  xSecondPoint, float ySecondPoint){
        return (float) Math.sqrt(Math.pow(xFirstPoint - xSecondPoint, 2) + Math.pow(yFirstPoint - ySecondPoint, 2));
    }
    public void addressBook(InetAddress address, int port) {
        activeClients.add(new IpPort(address, port));
    }

    public void includeCharacter(Snake character,String roomCode) {
        long specificId = character.Id;
        for(RoomGameObject room : roomGames){
            if( roomCode != null && roomCode.equals(room.code)){
                for(Snake mainCharacter : room.fullCharacter){
                    if(mainCharacter.Id == specificId){
                        mainCharacter.updateState(character.dots);
                        return;
                    }
                }
                fullCharacter.add(character);
                return;
            }
        }
    }

    public long getId() {
        return IDs++;
    }

    public void updateActiveClient(String roomCode,int udpPort){
        for(IpPort dest : activeClients){
            if(dest.port == udpPort){
                dest.updateStateInRoomGame(roomCode);
                return;
            }
        }
    }

    private class UdpConnectionSend {
        DatagramSocket gameplaySocket;
        public UdpConnectionSend() {
            try {
                gameplaySocket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }


        public void sendGameplay() {

             try{

                 Map<String,String> gamePlayDatas = new HashMap<>();
                 for(RoomGameObject room : roomGames){
                     String key = room.code;
                     String value = Helper.getJsonStringFromObject(room.gamePlay);
                     gamePlayDatas.put(key,value);
                 }


                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 DataOutputStream dos = new DataOutputStream(baos);
                 String listCharacterObjInfoData = Helper.getJsonStringFromObject(getListPlayerOnline());

                 for(IpPort dest : activeClients){
                     String data = "";

                     if(dest.inRooGame){
                         data = data + gamePlayDatas.get(dest.code);
                     }
                     data  = data + "#" + listCharacterObjInfoData;
                     dos.writeUTF(data);
                     byte[] bytes = baos.toByteArray();
                     DatagramPacket packet = new DatagramPacket(bytes,bytes.length);

                     packet.setAddress(dest.address);
                     packet.setPort(dest.port);

                     gameplaySocket.send(packet);

                     packet.setData(bytes);
                     packet.setLength(bytes.length);
                 }


             }
             catch (IOException ex){
                 ex.printStackTrace();
             }
        }
    }

    public List<String> getListUserOnlines(int id) {
        List<String> list = new ArrayList<>();
        for (Snake character : fullCharacter) {
            if (character.Id != id) {
                list.add(character.name);
            }
        }
        return list;
    }


    public void setUserNameAtId(int userID, String userName) {

       // System.out.println("number of character : " + fullCharacter.size());
        for (CharacterObjInfo character : listOnlinePlayer) {
            if (character.Id == userID) {
                character.name = userName;
                return;
            }
        }

        CharacterObjInfo character = new CharacterObjInfo(userID, userName);
        listOnlinePlayer.add(character);
    }


    public List<CharacterObjInfo> getListPlayerOnline() {
        return listOnlinePlayer;
    }

    public List<Integer> getListUDPPort() {
        List<Integer> listUPdPort = new ArrayList<>();
        for (IpPort dest : activeClients) {
            listUPdPort.add(dest.port);
        }
        return listUPdPort;
    }

    private WrapList generateMap() {

        WrapList map = new WrapList();
        List<Dot> dots = new ArrayList<>();
        for (int itemIndex = 0; itemIndex < WorlSize.TOTAL_FOODS; itemIndex++) {
            Random random = new Random();
            int x = random.nextInt(900);
            int y = random.nextInt(800);

            while (map.contains(x, y)) {
                x = random.nextInt(900);
                y = random.nextInt(800);
            }

            SpriteType spriteType = SpriteType.getFoodSprite();

            Dot dot = new Dot(x, y, spriteType);
            dots.add(dot);
        }

        map.addAll(dots);
        return map;
    }

    public void createRoomGame(String code, Snake character) {
        RoomGameObject roomGameObject = new RoomGameObject(code);
        roomGameObject.addCharacter(character);
        roomGameObject.map = generateMap();
        this.roomGames.add(roomGameObject);
        updateListPlayerOnlineComeToRoomGame(character,code);


    }

    public void updateListPlayerOnlineComeToRoomGame(Snake character,String code){
        for(int objIndex = 0; objIndex < listOnlinePlayer.size(); objIndex ++){
            if(listOnlinePlayer.get(objIndex).Id == character.Id){
                listOnlinePlayer.get(objIndex).code = code;
                return;
            }
        }
    }

    public List<CharacterObjInfo> getListPlayerInRoomByRoomCode(String roomCode) {

        List<CharacterObjInfo> listPlayerInRoom = new ArrayList<>();
        for (RoomGameObject room : roomGames) {
            if (room.code.equals(roomCode)) {
                for (Snake character : room.fullCharacter) {
                    CharacterObjInfo obj = new CharacterObjInfo((int) character.Id, character.name);
                    listPlayerInRoom.add(obj);
                }
            }
        }
        return listPlayerInRoom;
    }

    public int joinToRoomGame(String code, Snake character) {
        for (RoomGameObject room : roomGames) {
            if (code.equals(room.code)){
               room.addCharacter(character);
               return room.fullCharacter.size() - 1;
            }
        }
        return -1;
    }

    public boolean checkIsRoomGameAreRunningByRoomGameCode(String roomCode){
        for(RoomGameObject room : roomGames){
            if(roomCode.equals(room.code)){
                return room.isRunning;
            }
        }
        return false;
    }

    public void setRoomGameAreRunningByRoomCode(String roomCode){
        for(RoomGameObject room: roomGames){
            if(roomCode.equals(room.code)){
                room.isRunning = true;
            }
        }

    }

    public void updateMapInRoomGameByRoomCode(String roomCode,Dot tile){
        for(RoomGameObject room : roomGames){
            if(roomCode.equals(room.code)){
                room.map.reamoveDot(tile);
                if(room.map.dots.size() == 0){
                    room.map = generateMap();
                }
                return;
            }
        }
    }

    public boolean getCharacterIsRemovedByCharacterID(String roomCode, int id){
        for(RoomGameObject room : roomGames){
            if(roomCode.equals(room.code)){
                for(Snake character : room.fullCharacter){
                    if(character.Id == id){
                        return character.isRemoved;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void playerLeaveRoomGame(String roomCode, int idInRoom){
        for(RoomGameObject room : roomGames){
            if(roomCode.equals(room.code)){
                for(Snake character : room.fullCharacter){
                    if(idInRoom == character.Id){
                        System.out.println("leaving.....");
                        room.removeCharacterById(idInRoom);
                        return;
                    }
                }
                return;
            }
        }

    }

    public void removeAcitveClient(int clientId){
        for(int index = 0; index <listOnlinePlayer.size(); index++){
            if(listOnlinePlayer.get(index).Id == clientId){
                listOnlinePlayer.remove(index);
                return;
            }
        }
    }

    public void setRemovedForClientBy(String roomCode, int idInRoom){
        for(RoomGameObject room : roomGames){
            if(room.code.equals(roomCode)){
                room.setRemovedStaeByID(idInRoom);
            }
        }
    }
}
