package utility;

import javafx.event.ActionEvent;

import java.io.IOException;

public interface SceneInterface {
    void loadScene(ActionEvent event, String location) throws IOException;
}
