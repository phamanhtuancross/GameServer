package Handler;

import Define.SpriteType;
import Define.StartSnakeLocation;
import Define.WorlSize;
import Model.Dot;
import Model.Snake;
import Point.Point;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Handler.ClientHandler class
public class ClientHandler
{
    static  SpriteType [][] world = null;
    private int  index ;
    final DataInputStream dis;
    final DataOutputStream dos;
    public  Socket s;
    boolean isloggedin;
    boolean isLogout;
    boolean isWordChange;
    int totalItems = WorlSize.TOTAL_FOODS;
    // constructor
    public ClientHandler(Socket s, int index,
                         DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.index = index;
        this.s = s;
        this.isloggedin = true;
        this.isLogout = false;
        isWordChange = false;
        this.index = index;
    }


    private SpriteType[][] generateWorldSpace(){
        world = new SpriteType[WorlSize.WIDTH][WorlSize.HEIGHT];
        for(int rowIndex = 0; rowIndex < WorlSize.WIDTH; rowIndex++){
            for(int colIndex = 0; colIndex < WorlSize.HEIGHT; colIndex++){
                world[rowIndex][colIndex] = SpriteType.BRICK_WALL;
            }
        }

        for(int foodIndex = 0; foodIndex < WorlSize.TOTAL_FOODS; foodIndex++){
            SpriteType spriteType = generateFoodSprite();
            Point point = genertateFoodLocation();
            world[point.getX()][point.getY()] = spriteType;
        }

        return world;
    }

    private SpriteType generateFoodSprite(){
        SpriteType spriteType  = null;
        spriteType = SpriteType.getFoodSprite();
        return spriteType;
    }

    private Point genertateFoodLocation(){

        Random random = new Random();
        int x = random.nextInt(WorlSize.WIDTH);
        int y = random.nextInt(WorlSize.HEIGHT);

        if(world[x][y] == SpriteType.BRICK_WALL){
            return new Point(x,y);
        }
        while(world[x][y] != SpriteType.BRICK_WALL){
            x = random.nextInt(WorlSize.WIDTH);
            y = random.nextInt(WorlSize.HEIGHT);
        }

        Point point = new Point(x,y);
        return point;
    }


    private void implementCollisionBetweenSnakeAndWordSpace(List<Snake> snakes){
        for(Snake snake : snakes) {
            Dot head = snake.dots.get(0);
            if (isConllisionBetweenSnakeHeadAndWorldSpace(head)) {
                world[head.x][head.y] = SpriteType.BRICK_WALL;
            }
        }
    }

    private boolean isConllisionBetweenSnakeHeadAndWorldSpace(Dot head){
        boolean isConllision = false;
        int x = (int)Math.round((double)head.x/32);
        int y = (int)Math.round((double)head.y/32);

        if(x < 0 ||  y < 0 || x >= WorlSize.WIDTH || y >= WorlSize.HEIGHT){
            return false;
        }

        if(world[x][y] != SpriteType.BRICK_WALL){
            isConllision = true;
        }
        return isConllision;
    }

    public Snake generateSanke(int number){
      Snake snake = null;
      switch (number){
          case 0:{
              snake = generateSnakeAtTopLeftBoard();
              break;
          }
          case 1:{
              snake = generateSnakeAtTopRightBoard();
              break;
          }
          case 2:{
              snake = generateSnakeAtBottonLeftBoard();
              break;
          }
          case 3:{
              snake = generateSnakeAtBottonRightBoard();
              break;
          }
          default:{
              break;
          }
      }
      return snake;
    }

    private Snake generateSnakeAtTopLeftBoard(){
        System.out.println("Calling function generateSnakeAtTopLeftBoard()....");
        List<Dot> dots = new ArrayList<Dot>();

        int x = StartSnakeLocation.SNAKE_TOP_LEFT_X_POSITION;
        int y = StartSnakeLocation.SNAKE_TOP_LEFT_Y_POSITION;

        Dot heaDot = new Dot(x,y,SpriteType.SNAKE_DOT_RED);
        dots.add(heaDot);

        for(int dotIndex = 1; dotIndex < WorlSize.START_SNAKE_SIZE; dotIndex++){
            x -= 16;
            Dot dot = new Dot(x,y,SpriteType.SNAKE_DOT_YELLOW);
            dots.add(dot);
        }
        Snake snake = new Snake(dots);
        return snake;
    }

    private Snake generateSnakeAtTopRightBoard(){
        System.out.println("Calling function generateSnakeAtTopRightBoard()....");
        List<Dot> dots = new ArrayList<Dot>();

        int x = StartSnakeLocation.SNAKE_TOP_RIGHT_X_POSITION;
        int y = StartSnakeLocation.SNAKE_TOP_RIGHT_Y_POSITION;

        Dot heaDot = new Dot(x,y,SpriteType.SNAKE_DOT_BLACK);
        dots.add(heaDot);

        for(int dotIndex = 1; dotIndex < WorlSize.START_SNAKE_SIZE; dotIndex++){
            x += 16;
            Dot dot = new Dot(x,y,SpriteType.SNAKE_DOT_YELLOW);
            dots.add(dot);
        }
        Snake snake = new Snake(dots);
        return snake;
    }

    private Snake generateSnakeAtBottonLeftBoard(){
        List<Dot> dots = new ArrayList<Dot>();

        int x = StartSnakeLocation.SNAKE_BOTTON_LEFT_X_POSITION;
        int y = StartSnakeLocation.SNAKE_BOTTON_LEFT_Y_POSITION;

        Dot heaDot = new Dot(x,y,SpriteType.SNAKE_DOT_BLUE);
        dots.add(heaDot);

        for(int dotIndex = 1; dotIndex < WorlSize.START_SNAKE_SIZE; dotIndex++){
            x -= 16;
            Dot dot = new Dot(x,y,SpriteType.SNAKE_DOT_YELLOW);
            dots.add(dot);
        }
        Snake snake = new Snake(dots);
        return snake;
    }

    private Snake generateSnakeAtBottonRightBoard(){
        System.out.println("Calling function generateSnakeAtBottonRightBoard()...");
        List<Dot> dots = new ArrayList<Dot>();

        int x = StartSnakeLocation.SNAKE_BOTTON_RIGHT_X_POSITION;
        int y = StartSnakeLocation.SNAME_BOTTON_RIGHT_Y_POSITION;

        Dot heaDot = new Dot(x,y,SpriteType.SNAKE_DOT_GREEN);
        dots.add(heaDot);

        for(int dotIndex = 1; dotIndex < WorlSize.START_SNAKE_SIZE; dotIndex++){
            x += 16;
            Dot dot = new Dot(x,y,SpriteType.SNAKE_DOT_YELLOW);
            dots.add(dot);
        }
        Snake snake = new Snake(dots);
        return snake;
    }

    private Snake convertJsonValueToSnake(String jsonVlue){
        Type collectionType = new TypeToken<Snake>(){}.getType();
        Gson gson = new Gson();
        Snake snake = gson.fromJson(jsonVlue,collectionType);
        return snake;
    }

    private List<Snake> getListSnake(){
        return null;
    }
}