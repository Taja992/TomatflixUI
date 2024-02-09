package dk.easv.presentation.controller;

import dk.easv.entities.*;
import dk.easv.presentation.model.AppModel;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.io.File;
import java.net.URL;
import java.util.*;

public class AppController implements Initializable {

    @FXML
    private ScrollPane spTopSimilarUsers;
    @FXML
    private HBox listTopSimilarUsers;
    @FXML
    private ScrollPane spUsers;
    @FXML
    private HBox listUsers;
    @FXML
    private ScrollPane spTopForUser;
    @FXML
    private HBox listTopForUser;
    @FXML
    private ScrollPane spTopFromSimilar;
    @FXML
    private HBox listTopFromSimilar;
    @FXML
    private ScrollPane spTopAvgNotSeen;
    @FXML
    private HBox listTopAvgNotSeen;
    private int currentIndex = 0;
    private static final int howManyLoaded = 20;
    private static final int howManyUsersLoaded = 13;


    private AppModel model;
    private long timerStartMillis = 0;
    private String timerMsg = "";

    private void startTimer(String message){
        timerStartMillis = System.currentTimeMillis();
        timerMsg = message;
    }

    private void stopTimer(){
        System.out.println(timerMsg + " took : " + (System.currentTimeMillis() - timerStartMillis) + "ms");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scrollPaneListenerTopAvgNotSeen();
        scrollPaneListenerTopForUser();
        scrollPaneListenerTopFromSimilar();
        scrollPaneListenerUsers();
        spScroll();
    }

    public void setModel(AppModel model) {
        this.model = model;
        startTimer("Load users");
        model.loadUsers();
        stopTimer();

        // Populate users directly
        populateUsers(model);

        // Select the logged-in user in the listview, automagically trigger the listener above
        User loggedInUser = model.getObsLoggedInUser();
        for (Node node : listUsers.getChildren()) {
            VBox userBox = (VBox) node;
            Label label = (Label) userBox.getChildren().get(0);
            if (label.getText().equals(loggedInUser.getName())) {
                userBox.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0,
                        0, 0, 0, MouseButton.PRIMARY, 1, true, true,
                        true, true, true, true, true, true, true, true, null));
                break;
            }
        }
    }

    public void populateMovieNotSeen(AppModel model) {
        List<Movie> movies = model.getObsTopMovieNotSeen();
        listTopAvgNotSeen.getChildren().clear();
        currentIndex = 0;
        loadMovieNotSeen(movies);
        spTopAvgNotSeen.setHvalue(0);
    }

    public void populateTopForUser(AppModel model) {
        List<Movie> movies = model.getObsTopMovieSeen();
        listTopForUser.getChildren().clear();
        currentIndex = 0;
        loadListTopForUser(movies);
        spTopForUser.setHvalue(0);
    }

    public void populateTopFromSimilar(AppModel model) {
        List<TopMovie> movies = model.getObsTopMoviesSimilarUsers();
        listTopFromSimilar.getChildren().clear();
        currentIndex = 0;
        loadTopFromSimilar(movies);
        spTopFromSimilar.setHvalue(0);
    }

    private void loadMovieNotSeen(List<Movie> movies) {
        File imagesDir = new File("images");
        File[] imageFiles = imagesDir.listFiles();

        int endIndex = Math.min(currentIndex + howManyLoaded, movies.size());
        for (int i = currentIndex; i < endIndex; i++) {
            Movie movie = movies.get(i);

            Image image = null;
            if (imageFiles != null && imageFiles.length > 0) {
                image = new Image(imageFiles[i % imageFiles.length].toURI().toString());
            }

           // Image image = new Image("https://picsum.photos/100?random=" + i);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Label label = new Label(movie.getTitle());
            VBox movieBox = new VBox(imageView, label);
            movieBox.getStyleClass().add("movie-box");
            movieBox.setPrefHeight(100);
            movieBox.setPrefWidth(100);
            movieBox.setPadding(new Insets(10));
            listTopAvgNotSeen.getChildren().add(movieBox);
        }
        currentIndex = endIndex;
    }

    private void loadListTopForUser(List<Movie> movies) {
        File imagesDir = new File("images");
        File[] imageFiles = imagesDir.listFiles();

        int endIndex = Math.min(currentIndex + howManyLoaded, movies.size());
        for (int i = currentIndex; i < endIndex; i++) {
            Movie movie = movies.get(i);


            Image image = null;
            if (imageFiles != null && imageFiles.length > 0) {
                image = new Image(imageFiles[i % imageFiles.length].toURI().toString());
            }

           // Image image = new Image("https://picsum.photos/100?random=" + i);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Label label = new Label(movie.getTitle());
            VBox movieBox = new VBox(imageView, label);
            movieBox.getStyleClass().add("movie-box");
            movieBox.setPrefHeight(100);
            movieBox.setPrefWidth(100);
            movieBox.setPadding(new Insets(10));
            listTopForUser.getChildren().add(movieBox);
        }
        currentIndex = endIndex;
    }

    private void loadTopFromSimilar(List<TopMovie> movies) {
        File imagesDir = new File("images"); // Directory where your images are stored
        File[] imageFiles = imagesDir.listFiles(); // List of all image files in the directory

        int endIndex = Math.min(currentIndex + howManyLoaded, movies.size());
        for (int i = currentIndex; i < endIndex; i++) {
            Movie movie = movies.get(i).getMovie();

            Image image = null;
            if (imageFiles != null && imageFiles.length > 0) {
                image = new Image(imageFiles[i % imageFiles.length].toURI().toString());
            }

          //  Image image = new Image("https://picsum.photos/100?random=" + i);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setCache(true);
            Label label = new Label(movie.getTitle());
            VBox movieBox = new VBox(imageView, label);
            movieBox.getStyleClass().add("movie-box");
            movieBox.setPrefHeight(100);
            movieBox.setPrefWidth(100);
            movieBox.setPadding(new Insets(10));
            listTopFromSimilar.getChildren().add(movieBox);
        }
        currentIndex = endIndex;
    }

    private void scrollPaneListenerTopAvgNotSeen(){
        spTopAvgNotSeen.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == spTopAvgNotSeen.getHmax()) {
                loadMovieNotSeen(model.getObsTopMovieNotSeen());
            }
        });
    }

    private void scrollPaneListenerTopForUser(){
        spTopForUser.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == spTopForUser.getHmax()) {
                loadListTopForUser(model.getObsTopMovieSeen());
            }
        });
    }

    private void scrollPaneListenerTopFromSimilar(){
        spTopFromSimilar.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == spTopFromSimilar.getHmax()) {
                loadTopFromSimilar(model.getObsTopMoviesSimilarUsers());
            }
        });
    }

    /////////////////////////users///////////////////

    public void populateUsers(AppModel model) {
        List<User> users = model.getObsUsers();
        listUsers.getChildren().clear();
        currentIndex = 0;
        loadUsers(users);
        spUsers.setHvalue(0); // Reset the scroll pane to the start
    }

    private void loadUsers(List<User> users) {
        int endIndex = Math.min(currentIndex + howManyLoaded, users.size());
        for (int i = currentIndex; i < endIndex; i++) {
            User user = users.get(i);
            Label label = new Label(user.getName());
            VBox userBox = new VBox(label);
            userBox.getStyleClass().add("user-box");
            userBox.setPadding(new Insets(10));
            userBox.setOnMouseClicked(event -> {
                highlightUser(userBox);
                Thread t = new Thread(() -> {
                    model.loadData(user);
                    Platform.runLater(() -> {
                        populateMovieNotSeen(model);
                        populateTopForUser(model);
                        populateTopFromSimilar(model);
                        populateSimilarUsers(model);
                    });
                });
                t.start();
            });
            listUsers.getChildren().add(userBox);
        }
        currentIndex = endIndex;
    }


    private void highlightUser(VBox userBox) {
        // Reset the style of all user boxes
        for (Node node : listUsers.getChildren()) {
            node.setStyle("");
        }
        // Highlight the selected user box
        userBox.setStyle("-fx-background-color: #4b4646;");
    }

    private void scrollPaneListenerUsers() {
        spUsers.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() == spUsers.getHmax()) {
                loadUsers(model.getObsUsers());
            }
        });
    }

    private void loadListSimilarUsers(List<UserSimilarity> userSimilarities) {
        int endIndex = Math.min(currentIndex + howManyLoaded, userSimilarities.size());
        for (int i = currentIndex; i < endIndex; i++) {
            UserSimilarity userSimilarity = userSimilarities.get(i);
            Label label = new Label(userSimilarity.getName() + " - Similarity: " + userSimilarity.getSimilarityPercent());
            Circle circle = new Circle(20);
            circle.setFill(Color.TEAL);
            VBox userBox = new VBox(circle, label);
            userBox.setAlignment(Pos.CENTER);
            userBox.getStyleClass().add("user-box");
            userBox.setPrefHeight(25);
            userBox.setPrefWidth(100);
            userBox.setPadding(new Insets(10));
            listTopSimilarUsers.getChildren().add(userBox);
        }
        currentIndex = endIndex;
    }

    public void populateSimilarUsers(AppModel model) {
        List<UserSimilarity> userSimilarities = model.getObsSimilarUsers();
        listTopSimilarUsers.getChildren().clear();
        currentIndex = 0;
        loadListSimilarUsers(userSimilarities);
        spTopSimilarUsers.setHvalue(0);
    }

    private void spScroll(){
        addScrollEffect(spTopSimilarUsers);
        addScrollEffect(spUsers);
        addScrollEffect(spTopForUser);
        addScrollEffect(spTopFromSimilar);
        addScrollEffect(spTopAvgNotSeen);
    }

    private void addScrollEffect(ScrollPane sp) {
        sp.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.getDeltaX() == 0 && event.getDeltaY() != 0) {
                // consume the vertical scroll event and create a new horizontal scroll event
                event.consume();
                ScrollEvent horizontalEvent = new ScrollEvent(
                        event.getSource(),
                        event.getTarget(),
                        event.getEventType(),
                        event.getX(),
                        event.getY(),
                        event.getScreenX(),
                        event.getScreenY(),
                        event.isShiftDown(),
                        event.isControlDown(),
                        event.isAltDown(),
                        event.isMetaDown(),
                        event.isDirect(),
                        event.isInertia(),
                        event.getDeltaY(),
                        0, // deltaY is zero
                        event.getDeltaY(),
                        0, // totalDeltaY is zero
                        event.getTextDeltaXUnits(),
                        -event.getDeltaY(), // use negative value to reverse the direction
                        event.getTextDeltaYUnits(),
                        0, // textDeltaY is zero
                        event.getTouchCount(),
                        event.getPickResult()
                );
                Event.fireEvent(event.getTarget(), horizontalEvent);
            }
        });
    }
}