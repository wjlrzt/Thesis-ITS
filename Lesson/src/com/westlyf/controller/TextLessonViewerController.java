package com.westlyf.controller;

import com.westlyf.agent.Agent;
import com.westlyf.agent.LoadType;
import com.westlyf.domain.exercise.practical.PracticalExercise;
import com.westlyf.domain.exercise.practical.PracticalPrintExercise;
import com.westlyf.domain.exercise.practical.PracticalReturnExercise;
import com.westlyf.domain.lesson.TextLesson;
import com.westlyf.user.Users;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.model.AlertBox;
import sample.model.ConfirmBox;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Created by robertoguazon on 04/09/2016.
 */
public class TextLessonViewerController implements Initializable {

    Stage window;
    @FXML private BorderPane pane;
    @FXML private VBox lessonsVBox;
    @FXML private Label textLessonLabel;
    @FXML private WebView textLessonWebView;
    @FXML private Button back;
    @FXML private Button exerciseButton;
    @FXML private Hyperlink[] lesson;

    private ArrayList<TextLesson> lessonsInModule;
    private VideoLessonViewerController vlc;
    private PracticalPrintExerciseViewerController ppec;
    private PracticalReturnExerciseViewerController prec;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (Agent.getLoggedUser() != null) {
            lessonsInModule = Agent.getLessonsInModule(Agent.getCurrentModule());
            lesson = new Hyperlink[lessonsInModule.size()];
            for (int i = 0; i < lesson.length; i++) {
                lesson[i] = new Hyperlink();
                lesson[i].setText(i==0?"Introduction":"Lesson " + i);
                lesson[i].setFont(Font.font("System", FontWeight.NORMAL, 16));
                final int finalI = i;
                lesson[i].setOnAction(event -> openLesson(finalI));
                if (Agent.getLoggedUser().getCurrentModuleId().compareTo(Agent.getCurrentModule()) == 0) {
                    if (Agent.getLoggedUser().getCurrentLessonId().compareTo("lesson" + finalI) < 0) {
                        lesson[finalI].setDisable(true);
                    }
                }
            }
            lessonsVBox.getChildren().addAll(lesson);
        }
    }

    public void setExerciseButton(int i){
        if (i != 0) {
            exerciseButton.setText("Exercise");
            exerciseButton.setOnAction(event -> {
                try {
                    handleNewWindowAction(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }else {
            final int finalI = ++i;
            exerciseButton.setText("Next Lesson");
            exerciseButton.setOnAction(event -> {
                unlock(finalI);
                setTextLesson(lessonsInModule.get(finalI));
                setExerciseButton(finalI);
            });
        }
    }

    public void setTextLesson(TextLesson textLesson) {
        Agent.setLesson(textLesson);
        Agent.setCurrentLesson(textLesson.getTagsString());
        textLessonLabel.setText(textLesson.getTitle());
        String url = new File(textLesson.getText()).toURI().toString();
        /*String st = textLesson.getText();
        if(st.contains("contenteditable=\"true\"")){
            st=st.replace("contenteditable=\"true\"", "contenteditable=\"false\"");
        }*/
        textLessonWebView.getEngine().load(url);
    }

    public void openExercise() throws IOException {
        String[] lessonTags = Agent.getCurrentLesson().split(",");
        Agent.loadExercise(lessonTags[0], lessonTags[1]);
    }

    public void openLesson(int i){
        setTextLesson(lessonsInModule.get(i));
        setExerciseButton(i);
    }

    public void unlock(int i){
        if (i < lesson.length) {
            lesson[i].setDisable(false);
            Agent.getLoggedUser().setCurrentLessonId("lesson" + i);
            AlertBox.display("Unlocked", "Unlocked new lesson " + i, "Click \"ok\" to close this alert box.");
        }else {
            Users loggedUser = Agent.getLoggedUser();
            if (loggedUser.getCurrentExamId() == null) {
                if (loggedUser.getCurrentModuleId().equals(Agent.getCurrentModule())) {
                    loggedUser.setCurrentExamId(Agent.getCurrentModule());
                    Boolean answer = ConfirmBox.display("Take Exam",
                            "Congratulations!\nYou have completed the entire module.\n" +
                                    "You are now ready to take the exam.",
                            "Do you wish to go back to the main menu to take the exam?");
                    if (answer) {
                        try {
                            Agent.load(LoadType.EXAM);
                            Agent.clearLessonsInModule();
                            Parent root = FXMLLoader.load(getClass().getResource("../../../sample/view/user.fxml"));
                            Scene scene = back.getScene();
                            Stage stage = (Stage) scene.getWindow();
                            scene.setRoot(root);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void handleChangeSceneAction(ActionEvent event) throws IOException {
        Stage stage;
        Scene scene;
        Parent root;
        if (event.getSource() == back){
            Agent.clearLessonsInModule();
            scene = back.getScene();
            stage = (Stage)scene.getWindow();
            root = FXMLLoader.load(getClass().getResource("../../../sample/view/modules.fxml"));
        }
        else {return;}
        scene.setRoot(root);
        stage.setScene(scene);
        stage.show();
    }

    private void handleNewWindowAction(ActionEvent event) throws IOException {
        window = new Stage();
        Parent root;
        if (event.getSource() == exerciseButton){
            openExercise();
            Node vlNode = loadVideoLessonNode();
            Node peNode = loadPracticalExerciseNode();
            root = (Parent)combine(vlNode, peNode);
        }else {return;}

        window.setOnCloseRequest(e -> {
            e.consume();
            closeExercise();
        });
        Scene scene = new Scene(root);
        scene.getStylesheets().addAll(pane.getScene().getStylesheets());
        window.setScene(scene);
        window.initModality(Modality.APPLICATION_MODAL);
        window.initOwner(exerciseButton.getScene().getWindow());
        window.showAndWait();
    }

    private Node loadVideoLessonNode() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/VideoLessonViewer.fxml"));
        Node vlNode = loader.load();
        vlc = loader.getController();
        //System.out.println(Agent.getExercise().getVideoLesson());
        vlc.setVideoLesson(Agent.getExercise().getVideoLesson());
        return vlNode;
    }

    private Node loadPracticalExerciseNode() throws IOException {
        PracticalExercise practicalExercise = Agent.getExercise().getPracticalExercise();
        if (Agent.containsPracticalExercise(practicalExercise)){
            practicalExercise.setCode(Agent.getUserExercise().getCode());
        }
        FXMLLoader loader = new FXMLLoader();
        Node peNode;
        if (practicalExercise instanceof PracticalPrintExercise){
            loader.setLocation(getClass().getResource("../view/PracticalPrintExerciseViewer.fxml"));
            peNode = loader.load();
            ppec = loader.getController();
            //System.out.println(Agent.getExercise().getPracticalExercise());
            ppec.setPracticalPrintExercise((PracticalPrintExercise) practicalExercise);
        }else if (practicalExercise instanceof PracticalReturnExercise){
            loader.setLocation(getClass().getResource("../view/PracticalReturnExerciseViewer.fxml"));
            peNode = loader.load();
            prec = loader.getController();
            //System.out.println(Agent.getExercise().getPracticalExercise());
            prec.setPracticalReturnExercise((PracticalReturnExercise) practicalExercise);
        }else {return null;}
        return peNode;
    }

    private Node combine(Node left, Node right) {
        BorderPane borderPane = new BorderPane();
        SplitPane splitPane = new SplitPane();
        //splitPane.setPrefWidth(1120);
        //splitPane.setPrefHeight(560);
        borderPane.setCenter(splitPane);

        Pane leftPaneContainer = new Pane(left);
        leftPaneContainer.setMinWidth(600);
        leftPaneContainer.setMinHeight(600);
        Pane rightPaneContainer = new Pane(right);
        rightPaneContainer.setMinWidth(600);
        rightPaneContainer.setMinHeight(600);

        if (left instanceof Pane && right instanceof Pane) {
            Pane leftPane = (Pane) left;
            Pane rightPane = (Pane) right;

            leftPane.prefWidthProperty().bind(leftPaneContainer.widthProperty());
            leftPane.prefHeightProperty().bind(leftPaneContainer.heightProperty());
            rightPane.prefWidthProperty().bind(rightPaneContainer.widthProperty());
            rightPane.prefHeightProperty().bind(rightPaneContainer.heightProperty());
        }

        splitPane.getItems().addAll(leftPaneContainer,rightPaneContainer);
        return borderPane;
    }

    private void closeExercise(){
        if (Agent.isCleared()){
            if (vlc instanceof Disposable) {
                String currentLesson = Agent.getCurrentLesson();
                int i = Integer.parseInt(String.valueOf(currentLesson.charAt(currentLesson.length() - 1)));
                Agent.setIsExerciseCleared(false);
                disposeExercise();
                unlock(++i);
            }
        }else {
            Boolean answer = ConfirmBox.display("Confirm Exit",
                    "Are you sure you want to close the exercise?", "All changes will not be saved.");
            if (answer) {
                if (vlc instanceof Disposable) {
                    disposeExercise();
                }
            }
        }
    }

    public void disposeExercise(){
        vlc.dispose();
        window.close();
    }
}
