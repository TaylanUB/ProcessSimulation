package de.tkammer.procsim.ui;

import de.tkammer.procsim.Config;
import de.tkammer.procsim.Dispatcher;
import de.tkammer.procsim.Runner;
import de.tkammer.procsim.dispatchers.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class Controller {
    @FXML private TextField outputDirTextField;
    @FXML private Button outputDirButton;
    @FXML private ComboBox<Class<? extends Dispatcher>> dispatcherComboBox;
    @FXML private Button startButton;
    @FXML private TextArea resultsTextArea;

    private Stage stage;

    public void initialize() {
        outputDirButton.setOnAction((x) -> clickedOutputDirButton());
        startButton.setOnAction((x) -> clickedStartButton());

        // noinspection unchecked
        dispatcherComboBox.getItems().addAll(
                BasicDispatcher.class,
                SmartDispatcher.class,
                StupidDispatcher.class,
                RandomDispatcher.class
        );

        dispatcherComboBox.setValue(BasicDispatcher.class);

        File desktopDir = new File(System.getProperty("user.home"), "Desktop");
        if (desktopDir.isDirectory()) {
            outputDirTextField.setText(desktopDir.getAbsolutePath());
        } else {
            outputDirTextField.setText(System.getProperty("user.home"));
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void clickedOutputDirButton() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("CSV Output Directory");
        directoryChooser.setInitialDirectory(new File(outputDirTextField.getText()));
        File dir = directoryChooser.showDialog(stage);
        outputDirTextField.setText(dir.getAbsolutePath());
    }

    private void clickedStartButton() {
        Config config = new Config();
        config.setResultsDir(outputDirTextField.getText());
        config.setDispatcherClass(dispatcherComboBox.getValue());
        config.setPrintWriter(new PrintWriter(new Writer() {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException {
                resultsTextArea.appendText(new String(cbuf, off, len));
            }

            @Override
            public void flush() throws IOException { }

            @Override
            public void close() throws IOException { }
        }));
        Runner runner = new Runner(config);
        new Thread(runner::run).start();
    }
}
