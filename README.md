<h1>🎮 GameEngine</h1>

A lightweight, easy-to-use 2D Java Game Engine
Includes a built-in Sprite Editor, Animation System, and intuitive GameObject System for fast development.

<h2>✨ Features </h2>


🎮 Simple GameObject System – Object-oriented approach to create games

🎨 Built-in Sprite Editor – Draw and edit sprites directly in the engine

🎬 Animation System – Frame-by-frame sprite animations

📦 Sprite & Animation Manager – Automatic caching and loading

🎯 Collision Detection – Box and Circle colliders with automatic handling

📷 Camera System – Object follow, screen shake, smooth movement

⌨️ Input System – Keyboard & mouse input handling

🎨 Console Logging – Colorful, tagged debugging output

🔧 Fully Extensible – Override methods to customize behavior

<h2>📦 Installation </h2>

<h3>1️⃣ Create a Maven Project </h3>

Project structure:
```java
MyGame/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── yourgame/
                    └── Main.java
```
<h3>2️⃣ Configure pom.xml </h3>

```java
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.yourgame</groupId>
    <artifactId>MyGame</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>com.github.zeeesea</groupId>
            <artifactId>GameEngine</artifactId>
            <version>VERSION</version>
        </dependency>
    </dependencies>
</project>
```
Replace Version with Commit Hash, or Release Tag (e.g. v1.0);


<h3>3️⃣ Reload Maven</h3>

IntelliJ: Click Maven Reload or Ctrl+Shift+O

Eclipse: Right-click → Maven → Update Project

CLI: mvn clean install

<h2>🚀 Quick Start</h2>

```java
Main.java
package com.yourgame;

import GameEngine.Core.GameEngine;
import GameEngine.Core.GameEngineFrame;

import java.awt.*;

public class Main extends GameEngine {

    public static void main(String[] args) {
        GameEngine.launch(new Main());
    }

    @Override
    protected void init() {
        // Called once on startup
    }

    @Override
    protected void update() {
        // Called every frame for logic updates
    }

    @Override
    protected void draw(Graphics2D g) {
        // Called every frame for drawing
    }
}
```

<h2>📚 Core Concepts</h2>
<h3>1️⃣ Game Loop</h3>

```java
init() – runs once at startup

update() – runs every frame (60 FPS)

draw(Graphics2D g) – renders every frame

Useful variables:

deltaTime       // Time since last frame
objectManager   // Manage all GameObjects
getScreenWidth()
getScreenHeight()
getFPS()
```

<h3>2️⃣ GameObjects</h3>

```java
public class Player extends GameObject {

    private float speed = 200f;

    @Override
    public void init() {
        transform.position = new Vector2(100, 100);
        transform.scale = new Vector2(50, 50);
        tag = "Player";
    }

    @Override
    public void update(double deltaTime) {
        if (Input.getKey(Input.KeyCode.UP)) transform.position.y -= speed * deltaTime;
        if (Input.getKey(Input.KeyCode.DOWN)) transform.position.y += speed * deltaTime;
        if (Input.getKey(Input.KeyCode.LEFT)) transform.position.x -= speed * deltaTime;
        if (Input.getKey(Input.KeyCode.RIGHT)) transform.position.x += speed * deltaTime;
    }

    @Override
    public void draw(Graphics2D g) {
        drawGOasFilledRect(Color.BLUE);
    }

    @Override
    public void onCollision(GameObject other) {
        if (other.tag.equals("Enemy")) System.out.println("Hit by enemy!");
    }
}
```

Add to game (in the "... extends GameEngine" file:
```java
objectManager.add(new Player());
```

<h3>3️⃣ Transform System</h3>

Every object has position, scale, and rotation.

```java
transform.position = new Vector2(100, 200);
transform.scale = new Vector2(64, 64);
transform.rotation = 45f;
```

<h3>4️⃣ Input System</h3>

```java
if (Input.getKey(Input.KeyCode.W)) moveY -= 1;
if (Input.getKey(Input.KeyCode.S)) moveY += 1;
if (Input.getKey(Input.KeyCode.A)) moveX -= 1;
if (Input.getKey(Input.KeyCode.D)) moveX += 1;

transform.position.x += moveX * 200 * deltaTime;
transform.position.y += moveY * 200 * deltaTime;
```

<h3>5️⃣ Collision System</h3>

```java
collider = new BoxCollider2D(this); // or CircleCollider2D

@Override
public void onCollision(GameObject other) {
    if (other.tag.equals("Coin")) {
        other.destroy();
        System.out.println("Collected coin!");
    }
}
```


Manual check:

```java
if (collidesWith(otherObject)) { ... }
if (collidesWith(mousePosition)) { ... }
```

<h3>6️⃣ Vector2 Math</h3>

```java
Vector2 pos = new Vector2(10, 20);
Vector2 dir = Vector2.up();
float distance = pos.distance(other);
Vector2 normalized = pos.normalize();
```

<h2>🎨 Sprites & Animations</h2>

Load sprites:
```java
loadSprite("player_idle");
preloadSprite("player_walk");
```

Draw sprite:

```java
drawGOasSprite();
```


Animations:

```java
loadAnimation("walk", "player_walk", 10, true);
updateAnimation((float)deltaTime);
playAnimation("walk");
```

<h2>📷 Camera System</h2>

```java
player.setCameraFollowTarget();
shakeCamera(10f, 0.3f);
Camera cam = getCamera();
```

<h2>🛠️ Advanced Features</h2>

UI Elements
```java
Button btn = new Button.Builder()
                .rect(new Rectangle(getScreenWidth()/2, getScreenHeight()/2, 50,50))
                .color(new Color(100, 150, 255))
                .text("1")
                .onClick(this::clicked)
                .onHover(this::onHoverChange)
                .tag("btn")
                .smoothHover(10,150)
                .font(new Font("Arial", Font.BOLD, 14))
                .textColor(Color.WHITE)
                .build();
objectManager.add(btn);
```


Render Order
```java
renderOrder = -10; // Draw first (background)
renderOrder = 10;  // Draw last (player)
```

Find GameObjects
```java
Player player = objectManager.get(Player.class);
List<Enemy> enemies = objectManager.getAll(Enemy.class);
```

Helpers
```java
int width = getScreenWidth();
Vector2 center = getCenterPosition();
clampPositionToScreen();
isOutOfScreen();
```
<h2>🎨 Engine Configuration</h2>

```java
setWindowSize(1280, 720);
setWindowTitle("My Awesome Game");
setResizable(true);
setFullScreen(true);
setFPS(120);
```


<h2>📁 Project Structure</h2>

```java
MyGame/
├── pom.xml
├── src/main/java/com/yourgame/
│   ├── Main.java
│   ├── Player.java
│   └── Enemy.java
└── sprites/
    ├── single/
    └── animations/
```
<h2>🐛 Debugging</h2>

Console logging:
```java
Console.log("Hello World");
Console.log(ConsoleTag.ERROR, "Something went wrong!");
Console.log("Custom", "Message", ConsoleColor.CYAN);
```

Draw debug info:
```java
drawCollider();
```


---- Repositiory created on Oct 26, 2025 by zeeesea ----
