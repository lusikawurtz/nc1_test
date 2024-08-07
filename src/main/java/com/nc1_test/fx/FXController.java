package com.nc1_test.fx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nc1_test.entities.News;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Log4j2
@Component
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
    private void cancelWebsitePersonAction(ActionEvent event) {
        timeInput.clear();
    }

    @FXML
    private void createWebsiteAction(ActionEvent event) throws IOException {
        writeTitleLabel("Write time period");
        List<String> response = executeParseNewsEndpointAndGetResponse();
        if (response != null)
            writeResponse(response);
        else
            writeTitleLabel("Please, write only 'Day', 'Morning' or 'Evening'");

    }

    private void writeTitleLabel(String message) {
        mainLabel.setText(message);
        mainLabel.setLayoutX(50);
        mainLabel.setLayoutY(15);
    }

    private List<String> executeParseNewsEndpointAndGetResponse() throws IOException {
        String uri = loadProperties().getProperty("uri");
        URI targetUrl = UriComponentsBuilder.fromUriString(uri + "/news")
                .queryParam("time", timeInput.getText())
                .build()
                .encode()
                .toUri();
        try {
            String response = restTemplate.getForObject(targetUrl, String.class);
            return setJSONToListString(response);
        } catch (HttpClientErrorException clientErrorException) {
            log.error("Bad input for '{}'.", timeInput, clientErrorException);
            return null;
        } catch (Exception e) {
            log.error("Error executing get news endpoint for time period '{}'.", timeInput, e);
            return null;
        }
    }

    private void writeResponse(List<String> response) {
        Stage primaryStage = new Stage();

        addRepsonseToTextArea(response);
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

    private void addRepsonseToTextArea(List<String> response) {
        textResponse.setPrefSize(1000, 1000);
        textResponse.setWrapText(true);
        textResponse.setText("");
        List<String> finalResponse = IntStream.range(0, response.size()).boxed()
                .flatMap(i -> (i + 1) % 3 == 0 ? Stream.of(response.get(i), "\n") : Stream.of(response.get(i)))
                .toList();
        finalResponse.forEach(r -> {
            textResponse.appendText("\n");
            textResponse.appendText(r);
        });
    }

    private List<String> setJSONToListString(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        List<News> articles = objectMapper.readValue(response, new TypeReference<>() {
        });
        List<String> details = new ArrayList<>();

        for (News article : articles) {
            details.add(article.getHeadline());
            details.add(article.getDescription());
            if (article.getPublicationTime() != null)
                details.add(article.getPublicationTime().toString());
            else
                details.add("");
        }
        return details;
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