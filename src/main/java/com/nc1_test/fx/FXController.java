package com.nc1_test.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

@Log4j2
//@Component
@Controller
@FxmlView("main-scene.fxml")
public class FXController {

    private final RestTemplate restTemplate = new RestTemplate();
    @FXML
    TextArea textResponse;
    @FXML
    Label mainLabel;
    @FXML
    TextField timeInput;
    @FXML
    AnchorPane mainPane;


    @FXML
    public void setFXMLCreateWebsiteAnchorPane(AnchorPane fxmlCreateWebsite) {
        mainPane = fxmlCreateWebsite;
    }

    @FXML
    public AnchorPane getFXMLCreateWebsiteAnchorPane() {
        return mainPane;
    }

    @FXML
    private void createWebsiteAction(ActionEvent event) throws IOException {
        mainLabel.setText("Write time period");
        mainLabel.setLayoutX(150);
        mainLabel.setLayoutY(15);

        String response = executeParseNewsEndpoint();

        if (response != null)
            if (response.equals("Please, write only 'Day', 'Morning' or 'Evening'"))
                mainLabel.setText("Please, write only 'Day', 'Morning' or 'Evening'");
            else
                writeResponse(response);
    }

    @FXML
    private void cancelWebsitePersonAction(ActionEvent event) {
        timeInput.clear();
    }

    private String executeParseNewsEndpoint() throws IOException {
        String uri = loadProperties().getProperty("uri");
        URI targetUrl = UriComponentsBuilder.fromUriString(uri + "/news")
                .queryParam("time", timeInput.getText())
                .build()
                .encode()
                .toUri();
        try {
            String response = restTemplate.getForObject(targetUrl, String.class);
            return getStringResponse(response);
        } catch (HttpClientErrorException clientErrorException) {
            log.error("Bad input for '{}'.", timeInput, clientErrorException);
            return "Please, write only 'Day', 'Morning' or 'Evening'";
        } catch (Exception e) {
            log.error("Error executing get news endpoint for time period '{}'.", timeInput, e);
            return null;
        }
    }

    private String getStringResponse(String response) {
//        JSONObject jsonObject= new JSONObject(response);
        return response;
    }

    private void writeResponse(String response) {
        Stage primaryStage = new Stage();

        textResponse.setPrefSize(1000, 1000);
        textResponse.setText(response);
        textResponse.setWrapText(true);
        textResponse.setFont(new Font(20));

        ScrollPane scroll = new ScrollPane();
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setContent(textResponse);

        StackPane root = new StackPane();
        root.getChildren().add(scroll);
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static Properties loadProperties() throws IOException {
        Properties configuration = new Properties();
        InputStream inputStream = FXController.class.getClassLoader().getResourceAsStream("application.properties");
        configuration.load(inputStream);
        assert inputStream != null;
        inputStream.close();
        return configuration;
    }

}