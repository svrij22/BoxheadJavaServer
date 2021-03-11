package sep.api.webservices;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

@RestController
@RequestMapping("/locations")
public class LocationController {

    public LocationController() {
        System.out.println("TEST TEST TEST");
    }

    @GetMapping("/markets")
    public String getMarkets(@RequestParam String lat, @RequestParam String lon) throws IOException {

        try{
            /*Getting from url*/
            URL url = new URL(String.format("https://api.geoapify.com/v2/places?categories=commercial.supermarket&filter=circle:%s,%s,1000&bias=proximity:%s,%s&limit=20&apiKey=dc7d307677ac46fb897d60032983c238", lon, lat, lon, lat));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return this.getCoordinatesJsonFromString(content.toString(), "market");

        }catch (Exception e){
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    @GetMapping("/dungeons")
    public String getDungeons(@RequestParam String lat, @RequestParam String lon) throws IOException {

        try{
            /*Getting from url*/
            URL url = new URL(String.format("https://api.geoapify.com/v2/places?categories=entertainment&filter=circle:%s,%s,1000&bias=proximity:%s,%s&limit=20&apiKey=dc7d307677ac46fb897d60032983c238", lon, lat, lon, lat));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return this.getCoordinatesJsonFromString(content.toString(), "dungeon");

        }catch (Exception e){
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    @GetMapping("/places")
    public String getPlaces(@RequestParam String lat, @RequestParam String lon) throws IOException {

        try{
            /*Getting from url*/
            URL url = new URL(String.format("https://api.geoapify.com/v2/places?categories=natural,tourism&filter=circle:%s,%s,1000&bias=proximity:%s,%s&limit=30&apiKey=dc7d307677ac46fb897d60032983c238", lon, lat, lon, lat));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            return this.getCoordinatesJsonFromString(content.toString(), "place");

        }catch (Exception e){
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    @GetMapping("/test")
    public String getMarkets2() {
        return "test";
    }

    public String getCoordinatesJsonFromString(String content, String type) throws ParseException {
        /*Parsing json*/
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(content);
        JSONArray arr = (JSONArray) obj.get("features");

        //Start
        JSONArray newJsonList = new JSONArray();

        arr.forEach( a -> {
            var tempObj = (JSONObject) a;
            var coords = ((JSONObject) tempObj.get("geometry")).get("coordinates");
            String strCoords = coords.toString();
            strCoords = strCoords.replace("[", "");
            strCoords = strCoords.replace("]", "");

            JSONObject newObj = new JSONObject();
            newObj.put("lon", strCoords.split(",")[0]);
            newObj.put("lat", strCoords.split(",")[1]);
            newObj.put("type", type);

            newJsonList.add(newObj);
        });

        return newJsonList.toJSONString();
    }

}

