# The Last of Us: Legacy

A fully object-oriented, Java-based survival game inspired by *The Last of Us*. This single-player game is developed as part of a university course to demonstrate strong understanding of OOP principles and graphical UI development using JavaFX.

---

## 📖 Game Overview

In *The Last of Us: Legacy*, the player starts with a single hero navigating a zombie-infested 15x15 map. The objective is to collect all vaccines and use them to cure zombies, gradually building a team of heroes to survive the apocalypse.

The game follows a **turn-based system**, where each hero has a specific number of **action points** to move, attack, cure zombies, or use special abilities.

---

## 🎮 Game Features

### 🎭 Characters

#### Heroes (Player-Controlled)
- **Explorer**: Temporarily reveals the entire map when a supply is used.
- **Medic**: Can heal any adjacent hero or themselves using a supply.
- **Fighter**: Can attack multiple times in a single turn without using action points (requires supply).

#### Zombies
- Automatically attack adjacent heroes at the end of each turn.
- Can be cured into heroes using vaccines.
- New zombies spawn after turns or when a zombie dies.

---

### 🧰 Collectibles
- **Vaccines**: Required to cure zombies and win the game.
- **Supplies**: Enable heroes to activate their special abilities.

---

### 🗺️ Map & Visibility
- 15x15 grid.
- Only cells adjacent to a hero are visible.
- Fog-of-war mechanic encourages strategic movement and exploration.

---

### 🧠 Game Logic
- Heroes must collect and use all vaccines and maintain at least 5 heroes alive to win.
- The game ends in failure if all heroes are killed.
- Hero actions: move, attack, cure, use special ability (depending on class).

---

## 🧩 Project Structure

```
src/
├── engine/              # Main game logic & controller
├── exceptions/          # Custom exception handling (e.g., invalid move, not enough actions)
├── model/
│   ├── characters/      # Hero, Zombie, and their subclasses
│   ├── collectibles/    # Vaccine and Supply
│   └── world/           # Cell hierarchy: TrapCell, CharacterCell, CollectibleCell
├── tests/               # JUnit test classes
└── views/               # JavaFX-based GUI implementation
```

---

## 🛠️ Technologies Used

- **Java 11+**
- **JavaFX** for UI
- **Object-Oriented Programming**
- **Custom Exceptions & Game Engine**
- **File I/O (CSV-based Hero Loading)**

---

## 🚀 Getting Started

### Prerequisites:
- Java Development Kit (JDK 11+)
- JavaFX SDK (must be configured with your IDE)
- Any Java IDE (Eclipse, IntelliJ IDEA recommended)

### Running the Game:
1. Clone the repository.
2. Import the project into your IDE.
3. Ensure JavaFX is correctly linked.
4. Run the `Main` class located in the `views` package.

---

## 📌 Key Concepts Demonstrated

- Inheritance and polymorphism
- Abstract classes and interfaces
- Encapsulation with proper getters/setters
- Custom exception hierarchy
- JavaFX event-driven programming
- Turn-based game logic and state management

---

## 📷 Screenshots

*(Add screenshots of the GUI here once available)*

---

## 🏁 Game End Conditions

- **Victory**: Player collects & uses all vaccines and has 5 or more heroes alive.
- **Defeat**: All heroes are killed by zombies or traps.

---

## 📄 License

This project was developed as part of the Computer Programming Lab (CSEN 401) at the German University in Cairo (Spring 2023). For educational purposes only.

---
