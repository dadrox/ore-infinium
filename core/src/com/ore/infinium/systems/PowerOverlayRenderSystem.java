package com.ore.infinium.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.ore.infinium.Mappers;
import com.ore.infinium.World;
import com.ore.infinium.components.ItemComponent;
import com.ore.infinium.components.SpriteComponent;

/**
 * ***************************************************************************
 * Copyright (C) 2015 by Shaun Reich <sreich@kde.org>                    *
 * *
 * This program is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU General Public License as           *
 * published by the Free Software Foundation; either version 2 of           *
 * the License, or (at your option) any later version.                      *
 * *
 * This program is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the            *
 * GNU General Public License for more details.                             *
 * *
 * You should have received a copy of the GNU General Public License        *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.    *
 * ***************************************************************************
 */
public class PowerOverlayRenderSystem extends EntitySystem {
    private World m_world;
    private SpriteBatch m_batch;
    //   public TextureAtlas m_atlas;

    public boolean overlayVisible = true;
    public static int spriteCount;

    private boolean m_leftClicked;
    private boolean m_dragInProgress;

    public PowerOverlayRenderSystem(World world) {
        m_world = world;
    }

    public void addedToEngine(Engine engine) {
        m_batch = new SpriteBatch();
    }

    public void removedFromEngine(Engine engine) {
        m_batch.dispose();
    }

    public void leftMouseClicked() {
        m_leftClicked = true;
    }

    public void leftMouseReleased() {
        m_leftClicked = false;

        if (m_dragInProgress) {

        }

        m_dragInProgress = false;
    }

    public void update(float delta) {
        if (!overlayVisible) {
            return;
        }

//        m_batch.setProjectionMatrix(m_world.m_camera.combined);
        m_batch.setProjectionMatrix(m_world.m_camera.combined);
        m_batch.begin();

        if (m_leftClicked) {
            m_batch.setColor(1, 0, 0, 0.5f);
        }
        renderEntities(delta);

        if (!m_leftClicked) {
            m_batch.setColor(1, 1, 1, 1);
        }

        m_batch.end();

        //screen space rendering
        m_batch.setProjectionMatrix(m_world.m_client.viewport.getCamera().combined);
        m_batch.begin();

        m_world.m_client.bitmapFont_8pt.setColor(1, 0, 0, 1);

        float fontY = 150;
        float fontX = m_world.m_client.viewport.getRightGutterX() - 180;

        m_world.m_client.bitmapFont_8pt.draw(m_batch, "Energy overlay visible (E)", fontX, fontY);
        fontY -= 15;

        m_world.m_client.bitmapFont_8pt.draw(m_batch, "Input: N/A Output: N/A", fontX, fontY);

        m_world.m_client.bitmapFont_8pt.setColor(1, 1, 1, 1);

        m_batch.end();
    }

    private void renderEntities(float delta) {
        //todo need to exclude blocks?
        ImmutableArray<Entity> entities = m_world.engine.getEntitiesFor(Family.all(SpriteComponent.class, ItemComponent.class).get());

        ItemComponent itemComponent;
        for (int i = 0; i < entities.size(); ++i) {
            itemComponent = Mappers.item.get(entities.get(i));
            assert itemComponent != null;
            if (itemComponent.state != ItemComponent.State.InWorldState) {
                continue;
            }

            SpriteComponent spriteComponent = Mappers.sprite.get(entities.get(i));

            float powerNodeWidth = 30.0f / World.PIXELS_PER_METER;
            float powerNodeHeight = 30.0f / World.PIXELS_PER_METER;

            float powerNodeOffsetX = 30.0f / World.PIXELS_PER_METER;
            float powerNodeOffsetY = 30.0f / World.PIXELS_PER_METER;

            m_batch.draw(m_world.m_atlas.findRegion("power-node-circle"),
                    spriteComponent.sprite.getX(),
                    spriteComponent.sprite.getY(),
                    powerNodeWidth, powerNodeHeight);


            Vector2 spritePos = new Vector2(spriteComponent.sprite.getX(), spriteComponent.sprite.getY());
            float angle = spritePos.angle(new Vector2(Gdx.input.getX(), Gdx.input.getY()));

            float powerLineWidth = 30.0f / World.PIXELS_PER_METER;
            float powerLineHeight = 30.0f / World.PIXELS_PER_METER;

            m_batch.draw(m_world.m_atlas.findRegion("power-node-line"),
                    spriteComponent.sprite.getX(),
                    spriteComponent.sprite.getY(),
                    0, 0,
                    powerLineWidth, powerLineHeight, 1.0f, 1.0f, angle);
        }
    }
}
