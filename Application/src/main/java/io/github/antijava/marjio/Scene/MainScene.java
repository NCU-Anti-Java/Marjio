package io.github.antijava.marjio.Scene;

import io.github.antijava.marjio.common.*;

/**
 * Created by Zheng-Yuan on 12/24/2015.
 */
public class MainScene extends SceneBase {

    private IGraphics graphics;
    private IInput input;
    private ISceneManager sceneManager;

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

        checkKeyState();
        // draw menu background and text. select choice mark as other color.

    }

    @Override
    public void initialize(){
        currentChoice = 0;
        graphics = application.getGraphics();
        input = application.getInput();
        sceneManager = application.getSceneManager();
    }

    private void select(){
        switch (currentChoice){
            case HOST_GAME: {
                // Host scene
                //sceneManager.setScene();
                break;
            }
            case JOIN_GAME:{
                // Join scene
                //sceneManager.setScene();
                break;
            }
            case EXIT:{
                System.exit(0);
                break;
            }
        }
    }

    private void checkKeyState(){
        if(input.isPressed() || input.isPressing()){
            // UP
            if(--currentChoice < 0)
                currentChoice = 0;
        }
        else if(input.isPressed() || input.isPressing()){
            // DOWN
            if(++currentChoice >= MENU_TEXT.length)
                currentChoice = MENU_TEXT.length - 1;
        }
        else if(input.isPressed() || input.isPressing()){
            // CONFIRM
            select();
        }
    }

}
