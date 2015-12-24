package io.github.antijava.marjio.Scene;

import io.github.antijava.marjio.common.*;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase {

    private IGraphics graphics;
    private IInput input;

    private final String[] MENU_TEXT = {"Host Game", "Join Game", "Exit"};
    private final int HOST_GAME = 0;
    private final int JOIN_GAME = 1;
    private final int EXIT = 2;
    private int currentChoice;

    public MainScene(IApplication application){

        super(application);

    }

    @Override
    public void update() {

        // draw menu text. select choice mark as other color.

    }

    @Override
    public void initialize(){
        currentChoice = 0;
        graphics = application.getGraphics();
        input = application.getInput();
    }

    private void select(){
        switch (currentChoice){
            case HOST_GAME: {
                //application.getSceneManager().setScene();
                break;
            }
            case JOIN_GAME:{
                break;
            }
            case EXIT:{
                System.exit(0);
                break;
            }
        }
    }

    @Override
    public void pressed(){
        /*
        switch (key){
            case 0: {   // UP
                if(--currentChoice < 0)
                    currentChoice = 0;
                break;
            }
            case 1: {   // DOWN
                if(++currentChoice >= MENU_TEXT.length)
                    currentChoice = MENU_TEXT.length - 1;
                break;
            }
            case 2: {   // ENTER
                select();
                break;
            }
        }
        */
    }

    @Override
    public void pressing() {

    }

    @Override
    public void realeased() {

    }

}
