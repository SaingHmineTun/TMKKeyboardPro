# TMK Keyboard Pro (Shan/Myanmar/English IME)

![TMK Keyboard Pro Banner](file:///D:/AndroidProjects/TMKKeyboardPro/app/src/main/res/drawable/img_logo.jpg)

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

## 📦 Installation

1. Download the latest APK from the [releases](releases) section
2. Install the APK on your Android device
3. Go to Settings > System > Languages & input > Virtual keyboard
4. Enable TMK Keyboard Pro
5. Set it as your default keyboard

## 📸 Screenshots

| Keyboard Layout | Theme Selection | Settings |
|---------------|----------------|----------|
| ![Shan Keyboard](file:///D:/AndroidProjects/TMKKeyboardPro/screenshots/shan_keyboard.png) | ![Theme Picker](file:///D:/AndroidProjects/TMKKeyboardPro/screenshots/theme_picker.png) | ![Settings Screen](file:///D:/AndroidProjects/TMKKeyboardPro/screenshots/settings_screen.png) |

## 🛠 Tech Stack
* **Language:** Kotlin
* **Core API:** `InputMethodService`
* **Layout Engine:** `ConstraintLayout` (Chains and Weights for precise scaling)
* **Interactions:** `TouchListeners`, `GestureDetectors`, and `PopupWindow`
* **Persistence:** `SharedPreferences` for user settings and themes
* **Build System:** Gradle with AndroidX libraries

## 🤝 Contributing
We welcome contributions from the community! To contribute:

1. Fork the repository
2. Create a new branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a new Pull Request

## 📄 License

This project is licensed under the GNU General Public License v3.0 - see the [LICENSE](LICENSE) file for details.

---
**Developed by Sai Mao (TMK Group)** *Empowering the Shan Community through Digital Innovation.*