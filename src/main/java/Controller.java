import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class Controller {
    @FXML
    private Pane parentPane;

    @FXML
    private Text txtFieldToLoad;

    @FXML
    private Text txtFieldToSave;

    @FXML
    private DatePicker startDateChooser;

    @FXML
    private DatePicker endDateChooser;

    @FXML
    private ImageView imageForLoading;

    @FXML
    private Text fieldForLoadSaveChooser;

    @FXML
    private ToggleGroup sessionConfig;

    private Path pathForLoadDirectory;
    private Path pathForSaveDirectory;

    @FXML
    public void onFileToSaveChosen() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("保存先のディレクトリを選択してください");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Stage stage = (Stage) parentPane.getScene().getWindow();

        File f4 = directoryChooser.showDialog(stage);
        try {
            this.txtFieldToSave.setText(f4.getAbsolutePath());
            this.txtFieldToSave.setTextAlignment(TextAlignment.LEFT);
            this.pathForSaveDirectory = Paths.get(f4.getAbsolutePath());
        } catch (NullPointerException nullPointerException) {
        }
    }

    @FXML
    public void onFileToLoadChosen() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("読み込むディレクトリを選択してください");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        Stage stage = (Stage) parentPane.getScene().getWindow();

        File f4 = directoryChooser.showDialog(stage);

        try {
            this.txtFieldToLoad.setText(f4.getAbsolutePath());
            this.txtFieldToLoad.setTextAlignment(TextAlignment.LEFT);
            this.pathForLoadDirectory = Paths.get(f4.getAbsolutePath());
        } catch (NullPointerException nullPointerException) {
        }
    }

    @FXML
    public void onStartDateChosen() {
        LocalDate getLocalDate = this.startDateChooser.getValue();
        if (getLocalDate != null) {
            LocalDate setValue = getLocalDate.plusDays(14);
            if (this.endDateChooser == null || setValue != this.endDateChooser.getValue()) {
                this.endDateChooser.setValue(setValue);
            }
        }
    }

    @FXML
    public void onEndDateChosen() {
        LocalDate getLocalDate = this.endDateChooser.getValue();
        if (getLocalDate != null) {
            LocalDate setValue = getLocalDate.minusDays(14);
            if (this.startDateChooser == null || setValue != this.startDateChooser.getValue()) {
                this.startDateChooser.setValue(setValue);
            }
        }
    }

    public void onProcessBtnClicked() {
        if (this.pathForLoadDirectory == null || startDateChooser.getValue() == null || endDateChooser.getValue() == null || pathForSaveDirectory == null) {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setHeaderText(null);
            dialog.setContentText("全ての欄を埋めてください。");

            dialog.showAndWait();

            return;
        }

        Date startDate = Date.from(this.startDateChooser.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(this.endDateChooser.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());


        new Main(startDate, endDate, this.pathForLoadDirectory, this.pathForSaveDirectory).run();
    }


}
