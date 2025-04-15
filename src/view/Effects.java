package view;

import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Effects {
	
    private DropShadow shadow;

    public Effects() {
        shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.5)); // Set shadow color with some transparency
        shadow.setRadius(10); // Set the radius of the shadow effect
        shadow.setSpread(0.6); // Set the spread of the shadow effect
    }

    public void addHoverEffect(Button button) {
        button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            button.setEffect(shadow);
            button.setStyle(button.getStyle() + "; -fx-border-color: white; -fx-border-width: 2px;");
        });
        button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            button.setEffect(null);
            button.setStyle(button.getStyle().replaceAll("-fx-border-color: white; -fx-border-width: 2px;", ""));
        });
    }

    public void removeHoverEffect(Button button) {
        button.removeEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
            button.setEffect(shadow);
            button.setStyle(button.getStyle() + "; -fx-border-color: white; -fx-border-width: 2px;");
        });
        button.removeEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            button.setEffect(null);
            button.setStyle(button.getStyle().replaceAll("-fx-border-color: white; -fx-border-width: 2px;", ""));
        });
    }
}
