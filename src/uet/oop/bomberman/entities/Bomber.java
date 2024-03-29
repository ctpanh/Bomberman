package uet.oop.bomberman.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import uet.oop.bomberman.Map;
import uet.oop.bomberman.entities.enemy.Enemy;
import uet.oop.bomberman.entities.items.BombItem;
import uet.oop.bomberman.entities.items.FlameItem;
import uet.oop.bomberman.entities.items.SpeedItem;
import uet.oop.bomberman.graphics.Sprite;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static uet.oop.bomberman.BombermanGame.*;
import static uet.oop.bomberman.Map.nextLevel;
import static uet.oop.bomberman.menu.Menu.hpCount;
import static uet.oop.bomberman.menu.Menu.time_number;

public class Bomber extends Character {
    private int STEP = 4;
    private int maxBomb = 1; //
    protected int num = 0;
    public static List<Bomb> bombs = new ArrayList<>();
    private int flameLvOfbbm = 1;
    private boolean upPressed = false;
    private boolean downPressed = false;
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean spacePressed = false;
    private int deadTime = 120;

    boolean dead = true;
    public Bomber(int x, int y, Image img) {

        super(x, y, img);
//        this.keyHandler = keyHandler;
        this.velocity = 16;
        left = x;
        right = x + Sprite.player_up.get_realWidth() * 2;
        top = y;
        bottom = y + Sprite.player_up.get_realHeight() * 2;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public void update() {
        //Remove bomb
        for (int i = 0; i < bombs.size(); i++) {
            if (bombs.get(i).getIsExplode()) {
                bombs.remove(bombs.get(i));
            }
        }
        seriesOfBomb();
        if (dead(m) || time_number == 0) {
            try {
                if (hpCount > 0) {
                    nextLevel--;
                    soundEffect("res/Sound/die.wav");
                    Image image = new Image(new FileInputStream("res/img/youDied.png"), Sprite.SCALED_SIZE * m.width, Sprite.SCALED_SIZE * m.height, false, false);
                    author_view.setImage(image);
                } else {
                    soundEffect("res/Sound/lose.wav");
                    Image image = new Image(new FileInputStream("res/img/gameOver.png"), Sprite.SCALED_SIZE * m.width, Sprite.SCALED_SIZE * m.height, false, false);
                    author_view.setImage(image);
                }
            } catch (FileNotFoundException e) {
                e.getStackTrace();
            }
            if (deadTime == 0) {
                appear = false;
            } else {
                deadTime--;
            }
        }
        if (upPressed) {
            moveUp();
        }
        if (downPressed) {
            moveDown();
        }
        if (leftPressed) {
            moveLeft();
        }
        if (rightPressed) {
            moveRight();
        }

    }

    public void seriesOfBomb() {
        for (int i = 0; i < bombs.size(); i++) {
            for (int j = i + 1; j < bombs.size(); j++) {
                if (Math.abs(bombs.get(i).x - bombs.get(j).x) <= 32 && (Math.abs(bombs.get(i).y - bombs.get(j).y)) <= 32) {
                    int time = Math.min(bombs.get(i).getBeforeExplodeTime(), bombs.get(j).getBeforeExplodeTime());
                    bombs.get(i).setBeforeExplodeTime(time);
                    bombs.get(j).setBeforeExplodeTime(time);
                }
                for (int k = 0; k < bombs.get(i).flames.size(); k++) {
                    if (m.checkCollision(bombs.get(i).flames.get(k), bombs.get(j))) {
                        int time = Math.min(bombs.get(i).getBeforeExplodeTime(), bombs.get(j).getBeforeExplodeTime());
                        bombs.get(i).setBeforeExplodeTime(time);
                        bombs.get(j).setBeforeExplodeTime(time);
                    }
                }
            }
        }
    }

    public void moveUp() {
        Entity aboveLeft = m.getEntity(this.getX() / 32, (this.getY() - 1) / 32);
        Entity aboveRight = m.getEntity((this.getX() + Sprite.DEFAULT_SIZE) / 32, (this.getY() - 1) / 32);
        int count = 0;
        while (aboveLeft.goThrough && aboveRight.goThrough && (count < STEP)) {
            y--;
            count++;
            aboveLeft = m.getEntity(this.getX() / 32, (this.getY() - 1) / 32);
            aboveRight = m.getEntity((this.getX() + Sprite.DEFAULT_SIZE) / 32, (this.getY() - 1) / 32);
        }
        top = y;
        bottom = y + Sprite.player_up.get_realHeight() * 2;
        left = x;
        right = x + Sprite.player_up.get_realWidth() * 2;
        setImg();
    }

    public void moveDown() {
        Entity underLeft = m.getEntity(this.getX() / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2 + 1) / 32);
        Entity underRight = m.getEntity((this.getX() + Sprite.DEFAULT_SIZE) / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2 + 1) / 32);
        int count = 0;
        while (underLeft.goThrough && underRight.goThrough && (count < STEP)) {
            y++;
            count++;
            underLeft = m.getEntity(this.getX() / 32, (this.getY() + Sprite.SCALED_SIZE + 1) / 32);
            underRight = m.getEntity((this.getX() + Sprite.DEFAULT_SIZE) / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2 + 1) / 32);

        }
        top = y;
        bottom = y + Sprite.player_down.get_realHeight() * 2;
        left = x;
        right = x + Sprite.player_down.get_realWidth() * 2;
        setImg();
    }

    public void moveLeft() {

        Entity above = m.getEntity((this.getX() - 1) / 32, this.getY() / 32);
        Entity under = m.getEntity((this.getX() - 1) / 32, (this.getY() + Sprite.player_left.get_realHeight() * 2 - 1) / 32);
        int count = 0;
        int cnt1 = 0;
        int cnt2 = 0;
        while ((!above.goThrough) && ((above.y + 32 - y) <= 15) && cnt1 < 15) {
            y++;
            cnt1++;
            above = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + 1) / 32);
        }
        while ((!under.goThrough) && ((y + 32 - under.y) <= 15) && (cnt2 < 10)) {
            y--;
            cnt2++;
            under = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + Sprite.player_right.get_realHeight() * 2 - 1) / 32);
        }

        while (above.goThrough && under.goThrough && (count < STEP)) {
            x--;
            count++;
            above = m.getEntity((this.getX() - 1) / 32, this.getY() / 32);
            under = m.getEntity((this.getX() - 1) / 32, (this.getY() + Sprite.player_left.get_realHeight() * 2 - STEP) / 32);
        }
        top = y;
        bottom = y + Sprite.player_left.get_realHeight() * 2;
        left = x;
        right = x + Sprite.player_left.get_realWidth() * 2;
        setImg();
    }

    public void moveRight() {
        Entity above = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + 1) / 32);
        Entity under = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + Sprite.player_right.get_realHeight() * 2 - 1) / 32);
        int count = 0;
        int cnt1 = 0;
        int cnt2 = 0;
        while ((!above.goThrough) && ((above.y + 32 - y) <= 15) && cnt1 < 15) {
            y++;
            cnt1++;
            above = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + 1) / 32);
        }
        while ((!under.goThrough) && ((y + 32 - under.y) <= 15) && (cnt2 < 10)) {
            y--;
            cnt2++;
            under = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + Sprite.player_right.get_realHeight() * 2 - 1) / 32);
        }
        while (above.goThrough && under.goThrough && (count < STEP)) {
            x++;
            count++;
            above = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + 1) / 32);
            under = m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2 + 1) / 32, (this.getY() + Sprite.player_right.get_realHeight() * 2 - 1) / 32);
        }

        top = y;
        bottom = y + Sprite.player_right.get_realHeight() * 2;
        left = x;
        right = x + Sprite.player_right.get_realWidth() * 2;
        setImg();
    }

    public boolean checkItem(Entity entity) {
        if (entity instanceof SpeedItem) {
            STEP += 2;
            return true;
        }
        if (entity instanceof BombItem) {
            maxBomb += 1;
            return true;
        }
        if (entity instanceof FlameItem) {
            flameLvOfbbm++;
            for (Bomb b : bombs) {
                if (!b.isActive()) {
                    b.setFlameLv(flameLvOfbbm);
                }
            }
            return true;
        }
        return false;
    }

    public void handleEvent(Map m, List<Entity> entities, GraphicsContext gc) {
        if (upPressed) {
            Entity grass1 = new Grass(this.getX() / 32, (this.getY()) / 32, Sprite.grass.getFxImage());
            if (checkItem(m.getEntity(this.getX() / 32, this.getY() / 32))) {
                soundEffect("res/Sound/item.wav");
                m.changeEntity(this.getX() / 32, this.getY() / 32, grass1);
            }
        } else if (downPressed) {
            Entity grass2 = new Grass(this.getX() / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2) / 32, Sprite.grass.getFxImage());
            if (checkItem(m.getEntity(this.getX() / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2) / 32))) {
                soundEffect("res/Sound/item.wav");
                m.changeEntity(this.getX() / 32, (this.getY() + Sprite.player_down.get_realHeight() * 2) / 32, grass2);
            }
        } else if (leftPressed) {
            Entity grass3 = new Grass(this.getX() / 32, (this.getY()) / 32, Sprite.grass.getFxImage());
            if (checkItem(m.getEntity(this.getX() / 32, this.getY() / 32))) {
                soundEffect("res/Sound/item.wav");
                m.changeEntity(this.getX() / 32, this.getY() / 32, grass3);
            }
        } else if (rightPressed) {
            Entity grass4 = new Grass((this.getX() + Sprite.player_right.get_realWidth() * 2) / 32, (this.getY()) / 32, Sprite.grass.getFxImage());
            if (checkItem(m.getEntity((this.getX() + Sprite.player_right.get_realWidth() * 2) / 32, this.getY() / 32))) {
                soundEffect("res/Sound/item.wav");
                m.changeEntity((this.getX() + Sprite.player_right.get_realWidth() * 2) / 32, this.getY() / 32, grass4);
            }
        } else if (spacePressed) {
            if (bombs.size() < maxBomb) {
                Bomb bomb = new Bomb(x / 32, (y + 16) / 32, Sprite.bomb.getFxImage(), flameLvOfbbm); // 16 = height of bomber / 2
                setImg();
                bomb.setBomb(entities, bomb);
                bombs.add(bomb);
                bomb.isPlaySoundExplosion = false;
                entities.add(bomb);
            }
        }

    }

    public void handleKeyPressed(javafx.scene.input.KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.UP) {
            upPressed = true;
        }
        if (code == KeyCode.DOWN) {
            downPressed = true;
        }
        if (code == KeyCode.LEFT) {
            leftPressed = true;
        }
        if (code == KeyCode.RIGHT) {
            rightPressed = true;
        }
        if (code == KeyCode.SPACE) {
            spacePressed = true;
        }
    }

    public void handleKeyReleased(javafx.scene.input.KeyEvent e) {
        KeyCode code = e.getCode();
        if (code == KeyCode.UP) {
            upPressed = false;
        }
        if (code == KeyCode.DOWN) {
            downPressed = false;
        }
        if (code == KeyCode.LEFT) {
            leftPressed = false;
        }
        if (code == KeyCode.RIGHT) {
            rightPressed = false;
        }
        if (code == KeyCode.SPACE) {
            spacePressed = false;
        }
    }

    public boolean dead(Map m) {
        for (Entity entity : entities) {
            if (m.checkCollision(entity, this)) {
                if (entity instanceof Enemy) {
                    hpCount--;
                    dead = true;
                    running = false;
                    setImg();
                }
            }
        }
        for (Bomb bomb : bombs) {
            for (int j = 0; j < bomb.flames.size(); j++) {
                if (m.checkCollision(bomb.flames.get(j), this)) {
                    hpCount--;
                    dead = true;
                    running = false;
                    setImg();
                }
                for (int k = 0; k < m.getObjects().size(); k++) {
                    if (m.getObjects().get(k) instanceof Enemy) {
                        if (m.checkCollision(bomb.flames.get(j), m.getObjects().get(k))) {
                            ((Enemy) m.getObjects().get(k)).setState(Enemy.STATE.DEAD);
                        }
                    }
                }
            }
        }
        return dead;
    }

    public boolean collisionWith(Entity e) {
        int left = e.getX();
        int right = e.getX() + Sprite.SCALED_SIZE;
        int top = e.getY();
        int bottom = e.getY() + Sprite.SCALED_SIZE;

        if (this.getBottom() < top) {
            return false;
        }

        if (this.getTop() > bottom) {
            return false;
        }

        if (this.getRight() < left) {
            return false;
        }

        if (this.getLeft() > right) {
            return false;
        }
        return true;
    }

    public void setImg() {
        if (dead) {
            this.img = Sprite.movingSprite(Sprite.player_dead1, Sprite.player_dead2, Sprite.player_dead3, num, 20).getFxImage();
        } else {

            if (upPressed) {
                if (num < 7500) {
                    num++;
                } else num = 0;
                this.img = Sprite.movingSprite(Sprite.player_up, Sprite.player_up_1, Sprite.player_up_2, num, 20).getFxImage();
            }
            if (downPressed) {
                if (num < 7500) {
                    num++;
                } else num = 0;
                this.img = Sprite.movingSprite(Sprite.player_down, Sprite.player_down_1, Sprite.player_down_2, num, 20).getFxImage();
            }
            if (leftPressed) {
                if (num < 7500) {
                    num++;
                } else num = 0;
                this.img = Sprite.movingSprite(Sprite.player_left, Sprite.player_left_1, Sprite.player_left_2, num, 20).getFxImage();
            }
            if (rightPressed) {
                if (num < 7500) {
                    num++;
                } else num = 0;
                this.img = Sprite.movingSprite(Sprite.player_right, Sprite.player_right_1, Sprite.player_right_2, num, 20).getFxImage();
            }
        }
    }
}