package com.gd.api;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
    public static void main(String[] args) {
        LevelDAO levelDAO = new LevelDAO();
        LevelController levelController = new LevelController(levelDAO);

        Javalin app = Javalin.create(config -> {
            config.plugins.enableCors(cors -> cors.add(it -> {
                it.anyHost();
            }));
        }).start(8080);

        app.routes(() -> {
            path("api", () -> {
                path("levels", () -> {
                    get(levelController::getAllLevels);
                    post(levelController::createLevel);
                    path("{id}", () -> {
                        get(levelController::getLevel);
                        put(levelController::updateLevel);
                        delete(levelController::deleteLevel);
                    });
                });
            });
        });
    }
}
