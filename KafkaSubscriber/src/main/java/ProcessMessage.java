import com.slack.api.methods.SlackApiException;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessMessage {
    private static ProcessMessage processMessageInstance = null;

    public JSONArray JsonLookUpMap = new JSONArray();

    private static long totalIncomingMessages;

    private static long flaggedMessages;

    private static double stats;

    private ProcessMessage() throws IOException, SlackApiException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("lookUpMap.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        JsonLookUpMap = new JSONArray(jsonTxt);

        LocalDateTime twoSecondsLater = LocalDateTime.now().plusMinutes(2);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(new sendMessageToSlack(), twoSecondsLaterAsDate);
    }

    class sendMessageToSlack extends TimerTask {
        public void run() {
            String statsMessageString = "STATS: You have had " + new DecimalFormat("#.##").format(stats) + "% flagged messages in this channel today.";
            try {
                SlackEventReceiverApplication.publishMessage("XXXXXXXXXXX", statsMessageString);
            } catch (SlackApiException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static ProcessMessage getInstance() throws IOException, SlackApiException {
        if (processMessageInstance == null)
            processMessageInstance = new ProcessMessage();
        return processMessageInstance;
    }

    public void lookUpMessage(String message) {
        totalIncomingMessages += 1;
        stats = ((double) flaggedMessages/totalIncomingMessages) * 100;
        for(int i = 0; i < JsonLookUpMap.length(); i++) {
            JSONObject jsonobject = JsonLookUpMap.getJSONObject(i);
            if(message.contains(jsonobject.getString("word"))) {
                System.out.println("Total: " + totalIncomingMessages);
                String recommendation = jsonobject.getString("recommendation");
                String messageString = "Uh-oh! You have used '" + jsonobject.getString("word") + "' which is not inclusive. Try using '" + recommendation + "' instead";
                System.out.println(messageString);

                try {
                    SlackEventReceiverApplication.publishMessage("XXXXXXXXXXX", messageString);
                    flaggedMessages += 1;
                    stats = ((double) flaggedMessages/totalIncomingMessages) * 100;
                } catch (SlackApiException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

}
