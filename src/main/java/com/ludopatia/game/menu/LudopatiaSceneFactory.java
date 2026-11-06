package com.ludopatia.game.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.SimpleGameMenu;

public class LudopatiaSceneFactory extends SceneFactory {

    @Override
    public FXGLMenu newMainMenu() {
        return new LudopatiaMainMenu();
    }

    @Override
    public FXGLMenu newGameMenu() {
        return new SimpleGameMenu();
    }
}
