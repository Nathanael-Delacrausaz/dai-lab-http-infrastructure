package com.gd.api;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class LevelController {
    private final LevelDAO levelDAO;

    public LevelController(LevelDAO levelDAO) {
        this.levelDAO = levelDAO;
    }

    public void getAllLevels(Context ctx) {
        ctx.json(levelDAO.getAllLevels());
    }

    public void getLevel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        levelDAO.getLevel(id)
                .ifPresentOrElse(
                    level -> ctx.json(level),
                    () -> ctx.status(HttpStatus.NOT_FOUND)
                );
    }

    public void createLevel(Context ctx) {
        Level level = ctx.bodyAsClass(Level.class);
        Level createdLevel = levelDAO.createLevel(level);
        ctx.status(HttpStatus.CREATED).json(createdLevel);
    }

    public void updateLevel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Level level = ctx.bodyAsClass(Level.class);
        levelDAO.updateLevel(id, level)
                .ifPresentOrElse(
                    updatedLevel -> ctx.json(updatedLevel),
                    () -> ctx.status(HttpStatus.NOT_FOUND)
                );
    }

    public void deleteLevel(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        boolean deleted = levelDAO.deleteLevel(id);
        if (deleted) {
            ctx.status(HttpStatus.NO_CONTENT);
        } else {
            ctx.status(HttpStatus.NOT_FOUND);
        }
    }
}
