package API_Tutorial;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.Gson;

public final class App {
    public static void main(String[] args) throws Exception {
        // Create a new Transcript object
        Transcript transcript = new Transcript();
        
        // Set the audio URL for the transcript
        transcript.setAudio_url("https://github.com/johnmarty3/JavaAPITutorial/blob/main/Thirsty.mp4?raw=true");
        
        // Create a Gson object for JSON serialization and deserialization
        Gson gson = new Gson();
        
        // Convert the transcript object to JSON string
        String jsonRequest = gson.toJson(transcript);

        // Create a POST request with the JSON payload
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", "ef4e2fff95c84f3e8f9a4ad9523d175b")
                .POST(BodyPublishers.ofString(jsonRequest))
                .build();

        // Create a new HttpClient
        HttpClient httpClient = HttpClient.newHttpClient();

        // Send the POST request and get the response
        HttpResponse<String> postResponse = httpClient.send(postRequest, BodyHandlers.ofString());

        // Deserialize the response JSON into a Transcript object
        transcript = gson.fromJson(postResponse.body(), Transcript.class);
        
        // Create a GET request to check the status of the transcript
        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", "ef4e2fff95c84f3e8f9a4ad9523d175b")
                .build();

        // Poll the API until the transcript status is "completed" or "error"
        while(true) {
            // Send the GET request and get the response
            HttpResponse<String> getResponse = httpClient.send(getRequest, BodyHandlers.ofString());
            
            // Deserialize the response JSON into a Transcript object
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            // Print the current status of the transcript
            System.out.println(transcript.getStatus());

            // Break the loop if the transcript status is "completed" or "error"
            if("completed".equals(transcript.getStatus()) || "error".equals(transcript.getStatus())) {
                break;
            }
            
            // Wait for 1 second before sending the next GET request
            Thread.sleep(1000);
        }
    
        // Print the completed transcript
        System.out.println("Transcript Completed:");
        System.out.println(transcript.getText());
    }
}
