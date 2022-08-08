import com.slack.api.methods.SlackApiException;
import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ProcessMessage {
    private static ProcessMessage processMessageInstance = null;

    public JSONArray JsonLookUpMap = new JSONArray();

    private long totalIncomingMessages = 0;

    private long flaggedMessages = 0;

    private long stats = 0;

    private ProcessMessage() throws IOException, SlackApiException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("lookUpMap.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        JsonLookUpMap = new JSONArray(jsonTxt);

        LocalDateTime twoSecondsLater = LocalDateTime.now().plusSeconds(2);
        Date twoSecondsLaterAsDate = Date.from(twoSecondsLater.atZone(ZoneId.systemDefault()).toInstant());
        new Timer().schedule(new sendMessageToSlack(), twoSecondsLaterAsDate);
    }

    class sendMessageToSlack extends TimerTask {
        public void run() {
            String statsMessageString = "STATS: You have had " + stats + "% flagged messages in this channel today.";
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
        for(int i = 0; i < JsonLookUpMap.length(); i++) {
            JSONObject jsonobject = JsonLookUpMap.getJSONObject(i);
            if(message.contains(jsonobject.getString("word"))) {
                String recommendation = jsonobject.getString("recommendation");
                String messageString = "Uh-oh! You've used '" + jsonobject.getString("word") + "' which is not inclusive. Try using '" + recommendation + "' instead";
                System.out.println(messageString);

                try {
                    SlackEventReceiverApplication.publishMessage("XXXXXXXXXXX", messageString);
                    flaggedMessages += 1;
                } catch (SlackApiException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        stats = (flaggedMessages/totalIncomingMessages) * 100;
    }

}
