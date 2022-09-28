import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Platform.setImplicitExit(false);

        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        Scene scene = new Scene(root, 600, 474);


        stage.setTitle("体温チェッカー");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("img/icon.png")));
        stage.setResizable(false);



        stage.show();
    }


}



