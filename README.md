# TMK Keyboard Pro (Shan/Myanmar/English IME)

**TMK Keyboard Pro** is a high-performance, professional-grade Input Method Editor (IME) for Android, specifically optimized for the Shan community. Built from the ground up using Kotlin and Modern Android components, it bridges the gap between traditional typing and modern mobile UX.



## 🌟 Key Features

### ⌨️ Multi-Language Support
* **Shan Layout:** Full Unicode support with `normal` and `shifted` states.
* **Myanmar Layout:** Optimized for speed and character placement.
* **English QWERTY:** Standard professional layout for daily use.

### 🎨 Pro Visuals & Customization
* **Dynamic Theming:** Seamlessly switch between **Sapphire Blue** and **Luxury Gold** palettes without restarting the keyboard.
* **Pro Key Shapes:** Advanced `StateListDrawables` featuring subtle shadows, rounded corners, and tactile visual feedback.
* **Responsive Design:** Optimized for diverse screen sizes, including tablets and foldables, using `Resource Qualifiers`.

### 🚀 Advanced Interaction Logic
* **Slide & Release Popups:** Long-press a key to see hidden characters (e.g., ၷ, ၻ, ၿ) and simply slide your finger and release to commit.
* **Spacebar Cursor Control:** Swipe left or right on the spacebar to move the cursor with precision.
* **Smart Shift Logic:** Supports single-tap Shift and double-tap for **Caps Lock** with a 500ms threshold.
* **Haptic & Audio Feedback:** Real-time tactile vibration and audio "clicks" for a physical keyboard feel.



## 🛠 Tech Stack
* **Language:** Kotlin
* **Core API:** `InputMethodService`
* **Layout Engine:** `ConstraintLayout` (Chains and Weights for precise scaling)
* **Interactions:** `TouchListeners`, `GestureDetectors`, and `PopupWindow`
* **Persistence:** `SharedPreferences` for user settings and themes.

## 📂 Project Structure
```text
it.saimao.tmkkeyboardpro
├── services
│   └── ShanKeyboardService.kt  # The heart of the IME
├── logic
│   ├── ShiftStateManager.kt   # Managing OFF/ON/CAPS_LOCK
│   └── ThemeEngine.kt        # Programmatic UI tinting
├── drawables
│   ├── key_selector.xml      # State-based visuals
│   └── ic_shift_*.xml        # Custom vector icons
└── layouts
    ├── keyboard_root.xml     # Suggestion bar + Keys container
    └── layout_shn_*.xml      # Shan script XML definitions
```

## 📖 Module-Based Development
This project was developed through a structured learning curriculum:
* **Module 1:** Foundation & Root Architecture.
* **Module 2:** Pro UI Design (ConstraintLayout, Theming, Responsive Scaling).
* **Module 3:** Core Input & Event Handling (InputConnection, Swipe Logic, Popups).
* **Module 4:** Shan Language Intelligence (Unicode Encoding & Conversion).

---
**Developed by Sai Mao (TMK Group)** *Empowering the Shan Community through Digital Innovation.*