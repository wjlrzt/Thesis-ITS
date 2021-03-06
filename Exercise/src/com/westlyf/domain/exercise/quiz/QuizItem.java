package com.westlyf.domain.exercise.quiz;

import com.westlyf.utils.Convert;
import com.westlyf.utils.array.ArrayUtil;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by robertoguazon on 13/06/2016.
 */
public class QuizItem implements Serializable {

    private StringProperty question = new SimpleStringProperty();
    //private IntegerProperty questionId = new SimpleIntegerProperty(-1);
    private ArrayList<StringProperty> choices = new ArrayList<>();
    private ArrayList<StringProperty> validAnswers = new ArrayList<>();
    private IntegerProperty points = new SimpleIntegerProperty();
    private IntegerProperty pointsPerCorrect = new SimpleIntegerProperty(1);
    private ArrayList<StringProperty> answers = new ArrayList<>();
    private StringProperty explanation = new SimpleStringProperty();

    private StringProperty hint = new SimpleStringProperty();

    //radio button by default
    private QuizType type = QuizType.RADIOBUTTON;

    //if one answer only is allowed use radio button
    //true on default
    //B not sure if needed
    private BooleanProperty oneAnswer = new SimpleBooleanProperty(true);

    public QuizItem() {}

    public String getHint() {
        return hint.get();
    }

    public StringProperty hintProperty() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint.set(hint);
    }

    //TODO check if the evaluation of answers are efficient
    public boolean isCorrect() {
        return isCorrect(this.answers);
    }

    //TODO - check if efficient
    public boolean isCorrect(ArrayList<StringProperty> answers) {
        ArrayList<String> answersStrings = Convert.convertToString(answers);
        ArrayList<String> validAnswersStrings = Convert.convertToString(validAnswers);

        switch (type) {
            case TEXTFIELD:
                String answer = answersStrings.get(0).trim().toLowerCase();
                for (int i = 0; i < validAnswers.size(); i++) {
                    if (answer.equals(validAnswersStrings.get(i).trim().toLowerCase())) {
                        return true;
                    }
                }
                return false;

            case CHECKBOX:
            case RADIOBUTTON:
                if (answersStrings.size() != validAnswersStrings.size()) {
                    return false;
                }
                for (int i = 0; i < validAnswersStrings.size(); i++) {
                    if (!answersStrings.contains(validAnswersStrings.get(i))) {
                        return false;
                    }
                }
                return true;

            default:
                return false;
        }
    }

    public QuizItem(QuizItemSerializable quizItemsSerializable) {
        this.question.set(quizItemsSerializable.getQuestion());
        this.points.set(quizItemsSerializable.getPoints());
        this.pointsPerCorrect.set(quizItemsSerializable.getPointsPerCorrect());
        this.type = quizItemsSerializable.getType();

        this.choices = Convert.convertToStringProperty(quizItemsSerializable.getChoices());
        this.validAnswers = Convert.convertToStringProperty(quizItemsSerializable.getValidAnswers());
        this.answers = Convert.convertToStringProperty(quizItemsSerializable.getAnswers());
        this.explanation.set(quizItemsSerializable.getExplanation());
        this.hint.set(quizItemsSerializable.getHint());
    }

    public boolean isValidMaker() {
        if (question == null || question.get().equals("")) return false;
        if (choices == null || choices.isEmpty()) return false;
        if (validAnswers == null || validAnswers.isEmpty())  return false;
        if (points == null) return false;
        if (pointsPerCorrect == null) return false;

        //TODO - fix points

        return true;
    }

    public boolean isValidAnsweredFormat() {
        if (question == null || question.get().equals("")) return false;
        if (choices == null || choices.isEmpty()) return false;
        if (validAnswers == null || validAnswers.isEmpty())  return false;
        if (points == null) return false;
        if (pointsPerCorrect == null) return false;
        if (answers == null || answers.isEmpty()) return false;

        //TODO - for points system
        return true;
    }

    public String check() {
        return "\n\tItem:\n" +
                "\tquestion: " + ((question == null || question.get().equals("")) ? "empty" : question.get()) + "\n" +
                "\tchoices: " + ((choices == null || choices.isEmpty()) ? "empty" : choices.toString()) + "\n" +
                "\tvalidAnswers: " + ((validAnswers == null || validAnswers.isEmpty()) ? "empty" : validAnswers.size())  + "\n" +
                "\tpoints per correct: " + ((pointsPerCorrect == null || pointsPerCorrect.get() == 0) ?
                    "empty" : pointsPerCorrect.get()) + "\n" +
                "\tanswers: " + ((answers == null || answers.isEmpty()) ? "empty" : answers.size()) + "\n" +
                "\tpoints: " + ((points == null) ? "empty" : points.get());
    }

    public String getExplanation() {
        return explanation.get();
    }

    public void setExplanation(String explanation) {
        this.explanation.set(explanation);
    }

    public StringProperty explanationProperty() {
        return explanation;
    }

    public String getQuestion() {
        return question.get();
    }

    public StringProperty questionProperty() {
        return question;
    }

    public void setQuestion(String question) {
        this.question.set(question);
    }

    /* TODO - delete, not sure if oneAnswer is needed - just in case
    public boolean getOneAnswer() {
        return oneAnswer.get();
    }

    public BooleanProperty oneAnswerProperty() {
        return oneAnswer;
    }

    public void setOneAnswer(boolean oneAnswer) {
        this.oneAnswer.set(oneAnswer);
        if (oneAnswer) {
            this.type = QuizType.RADIOBUTTON;
        } else {
            this.type = QuizType.CHECKBOX;
        }
    }
    */

    public void setPointsPerCorrect (int value) {
        this.pointsPerCorrect.set(value);
    }

    public int getPointsPerCorrect() {
        return this.pointsPerCorrect.get();
    }

    public void setChoices(ArrayList<StringProperty> choices) {
        this.choices = choices;
    }

    public void setValidAnswers(ArrayList<StringProperty> validAnswers) {
        this.validAnswers = validAnswers;
    }

    public void setType(QuizType type) {
        this.type = type;
    }

    public QuizType getType() {
        return this.type;
    }

    public ArrayList<StringProperty> getChoices() {
        return choices;
    }

    public ArrayList<StringProperty> getValidAnswers() {
        return validAnswers;
    }

    public ArrayList<StringProperty> getAnswers() {
        return answers;
    }

    public int getPoints() {
        return points.get();
    }

    public VBox getItemBox() {
        VBox box = new VBox();

        Label questionLabel = new Label();
        questionLabel.setVisible(true);
        questionLabel.setWrapText(true);
        questionLabel.textProperty().bind(question);

        box.getChildren().add(questionLabel);

        switch (this.type) {

            case RADIOBUTTON:
                ArrayList<RadioButton> buttons = getChoicesRadioButtons();
                ToggleGroup choicesToggleGroup = getToggleGroup(buttons);

                choicesToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                    @Override
                    public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                        answers.clear();
                        answers.add((StringProperty) newValue.getUserData());
                    }

                });

                for (int i = 0; i < buttons.size(); i++) {
                    box.getChildren().add(buttons.get(i));
                }

                break;
            case CHECKBOX:
                ArrayList<CheckBox> choicesCheckBoxes = getChoicesCheckBoxes();
                for (int i = 0; i < choicesCheckBoxes.size(); i++) {

                    CheckBox choice = choicesCheckBoxes.get(i);
                    choice.selectedProperty().addListener(new ChangeListener<Boolean>() {
                        @Override
                        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                            String c = choice.getText();
                            if (newValue.booleanValue()) {
                                if (!answers.contains(c)) {
                                    answers.add(new SimpleStringProperty(c));
                                }
                            } else {
                                int index = Convert.indexOfEqualsString(answers,c);
                                if (index >= 0) {
                                    answers.remove(index);
                                }
                            }
                        }
                    });

                    box.getChildren().add(choice);

                }
                break;
            case TEXTFIELD:
                TextField blankTextField = new TextField();
                blankTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                    answers.clear();
                    answers.add(new SimpleStringProperty(newValue));
                });
                box.getChildren().add(blankTextField);
                break;

            default:
                break;
        }



        Separator line = new Separator();
        line.prefWidthProperty().bind(box.widthProperty());
        box.getChildren().add(line);

        return box;
    }

    public ArrayList<CheckBox> getChoicesCheckBoxes() {
        ArrayList<CheckBox> choicesArrayList = new ArrayList<>();
        ArrayList<StringProperty> randomizedChoices = ArrayUtil.randomizeArrayList(this.choices);

        for (int i = 0; i < randomizedChoices.size(); i++) {
            CheckBox choice = new CheckBox();
            choice.setVisible(true);
            choice.setText(randomizedChoices.get(i).get());
            choice.setIndeterminate(false);
            choicesArrayList.add(choice);
        }

        return choicesArrayList;
    }

    public ArrayList<RadioButton> getChoicesRadioButtons() {
        ArrayList<RadioButton> choicesArrayList = new ArrayList<>();
        ArrayList<StringProperty> randomizedChoices = this.choices;
        //ArrayList<String> randomizedChoices = ArrayUtil.randomizeArrayList(this.choices);
        for (int i = 0; i < randomizedChoices.size(); i++) {
            RadioButton choice = new RadioButton();
            choice.setText(randomizedChoices.get(i).get());
            choice.setUserData(randomizedChoices.get(i));
            choice.setVisible(true);
            choice.setSelected(false);
            choice.setWrapText(true);
            choice.setAlignment(Pos.CENTER);
            choice.setTextAlignment(TextAlignment.JUSTIFY);
            choice.setFont(Font.font("System", FontWeight.NORMAL, 12));
            choice.getStyleClass().remove("radio-button");
            choice.getStyleClass().add("toggle-button");

            choicesArrayList.add(choice);
        }

        return choicesArrayList;
    }

    public ToggleGroup getToggleGroup(ArrayList<RadioButton> buttons) {
        ToggleGroup group = new ToggleGroup();

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton choice = buttons.get(i);
            choice.setToggleGroup(group);
        }

        return group;
    }

    public void addChoice(StringProperty choice) {
        this.choices.add(choice);
    }

    public void addChoice(String choice) {this.choices.add(new SimpleStringProperty(choice));}

    public void addValidAnswer(String validAnswer) {
        this.validAnswers.add(new SimpleStringProperty(validAnswer));
    }

    public void addValidAnswer(StringProperty validAnswer) {
        this.validAnswers.add(validAnswer);
    }

    public void printItem() {
        System.out.println("Item:");
        System.out.println("question: " + question.getValue());

        System.out.println("choices: ");
        for (StringProperty choice: choices) {
            System.out.println("-" + choice.get());
        }

        System.out.println("answers: ");
        for (StringProperty validAnswer: validAnswers) {
            System.out.println("-" + validAnswer.get());
        }

    }

    @Override
    public String toString() {
        String choicesString = "\tChoices:\n";
        for (int i = 0; i < choices.size(); i++) {
            choicesString += "\t\t- " + choices.get(i) + "\n";
        }

        String validAnswersString = "\tvalid answers:\n";
        for (int i = 0; i < validAnswers.size(); i++) {
            validAnswersString += "\t\t* " + validAnswers.get(i) + "\n";
        }

        String answersString = "\tanswers:\n";
        for (int i = 0; i < answers.size(); i++) {
            answersString += "\t\tO " + answers.get(i) + "\n";
        }

        return  "\nitem: " + "\n" +
                "\tquestion: " + question.get() + "\n" +
                choicesString +
                validAnswersString +
                answersString +
                "\tpoints per correct: " + pointsPerCorrect.get() + "\n" +
                "\tpoints: " + points.get() + "\n" +
                "\texplanation: " + explanation.get() + "\n" +
                "\thint: " + hint.get();
    }

    public HBox getExamChoicesOnlyBox() {
        return getExamChoicesOnlyBox(0,0,false);
    }

    public HBox getExamChoicesOnlyBox(float prefWidth, float prefHeight) {
        return getExamChoicesOnlyBox(prefWidth,prefHeight,true);
    }

    public HBox getExamChoicesOnlyBox(float prefWidth, float prefHeight, boolean setPref) {
        HBox box = new HBox();
        box.setSpacing(8);

        ArrayList<RadioButton> buttons = getChoicesRadioButtons();

        for (int i = 0; i < buttons.size(); i++) {
            RadioButton radioButton = buttons.get(i);
            StringProperty choice = (StringProperty) radioButton.getUserData();
            if (answers.contains(choice)) {
                radioButton.setSelected(true);
            }

            if (setPref) {
                radioButton.setPrefWidth(prefWidth);
                radioButton.setPrefHeight(prefHeight);
                radioButton.setMaxWidth(Double.MAX_VALUE);
                radioButton.setMaxHeight(Double.MAX_VALUE);
            }
        }

        ToggleGroup choicesToggleGroup = getToggleGroup(buttons);

        choicesToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                answers.clear();
                answers.add((StringProperty) newValue.getUserData());
            }

        });

        for (int i = 0; i < buttons.size(); i++) {
            box.getChildren().add(buttons.get(i));
        }

        return box;
    }

    public void clearAnswers() {
        if (answers != null) {
            answers.clear();
        }
    }

    public void clearValidAnswers() {
        this.validAnswers.clear();
    }

    public void removeValidAnswer(StringProperty stringProperty) {
        this.validAnswers.remove(stringProperty);
    }
}
