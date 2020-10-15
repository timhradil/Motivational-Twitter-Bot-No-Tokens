/*
* Pulls a quote from an online API and tweets it out using twitter4j
*
* @author Tim Hradil
*/

//This will not run without the required tokens

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class MotivationalTwitter{
    private static String consumerKeyStr = "{consumerKeyStr}";
    private static String consumerSecretStr = "{consumerSecretStr}";
    private static String accessTokenStr = "{accessTokenStr}";
    private static String accessTokenSecretStr = "{accessTokenSecretStr}";
    private static String get_url = "https://quotes.rest/qod";

    public static class tweet implements Runnable {
        @Override
        public void run(){
            try {
                sendTweet(get_url);
            }
            catch (IOException e){
                System.out.println("Failed");
                return;
            }
            catch (TwitterException e){
                System.out.println("Failed");
                return;
            }
            System.out.println("Success");
        }
    }

    public static void main(String[] args) throws IOException, TwitterException{

        String get_url = "https://quotes.rest/qod";


        System.out.println(get_url);

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        Thread t = new Thread(new tweet());

        scheduler.scheduleAtFixedRate(t, 0, 1, TimeUnit.DAYS);

    }

    public static String getQuote(URL url) throws IOException{
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.println(response.toString());

        String[] responseArr = response.toString().split("");

        String status = "\"\""+contentFromAttribute(responseArr,"quote")+"\" - "+contentFromAttribute(responseArr, "author")+"\" - Tim Hradil\'s Motivational Twitter Bot";

        return status;
    }

    public static String contentFromAttribute(String[] arr, String att){
        String str;
        String str2= "";

        for(int i=0;i<arr.length;i++){
            String strTemp = "";
            if(arr[i].equals("\"")){
                strTemp = pullToNextQuote(arr, i);

                if(strTemp.equals(att)){

                    System.out.println("Found");

                    str = pullToNextQuote(arr, i+strTemp.length()+1);
                    
                    str2 = pullToNextQuote(arr, i+strTemp.length()+str.length()+2);

                }
            }
            i+=strTemp.length();
        }

        return str2;
    }

    public static String pullToNextQuote(String[] arr, int loc) {
        String str = "";
        while (true) {
            loc++;
            if (loc == arr.length) {
                break;
            } else if (arr[loc].equals("\"")) {
                break;
            } else {
                str += arr[loc];
            }
        }
        return str;
    }

    public static void sendTweet(String get_url) throws IOException, TwitterException {
        URL url = new URL(get_url);

        String status = getQuote(url);

        System.out.println(status);

       Twitter twitter = new TwitterFactory().getInstance();

       twitter.setOAuthConsumer(consumerKeyStr, consumerSecretStr);
       AccessToken accessToken = new AccessToken(accessTokenStr, accessTokenSecretStr);

       twitter.setOAuthAccessToken(accessToken);

       twitter.updateStatus(status);

    }
}
