package sep.api.webservices;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
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
@RequestMapping("/markets")
public class MarketController {

    public MarketController() {
        System.out.println("TEST TEST TEST");
    }

    @GetMapping()
    public static String getMarkets(@RequestParam String lat, @RequestParam String lon) throws IOException {

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

            /*Parsing json*/
            JSONParser jsonParser = new JSONParser();
            JSONObject obj = (JSONObject) jsonParser.parse(content.toString());
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

                newJsonList.add(newObj);
            });

            return newJsonList.toJSONString();

        }catch (Exception e){
            e.printStackTrace();
            return Arrays.toString(e.getStackTrace());
        }
    }

    @GetMapping("/test")
    public String getMarkets2() {
        return "test";
    }

}

