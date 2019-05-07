package com.ruubel.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Email;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailingService {

    @Value("${mailing.enabled}")
    private boolean mailingEnabled;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Free service, who cares...
    private String apiKey = "406970c11818cb958bea6a9b6bd6b2e0";
    private String otherApiKey = "7d9a8dbb758d0da19c1321bdf37f2f20";

    public void notifyQualifiedProperty(String propertyUrl, int points) {
        send("Real estate, pts: " + points, "Points: " + points + ", url: " + propertyUrl);
    }

    public void notifyCrash() {
        send("Crash", "Check the logs");
    }

    private void send(String subject, String content) {
        if (!mailingEnabled) {
            return;
        }
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient(apiKey, otherApiKey);
        request = new MailjetRequest(Email.resource)
                .property(Email.FROMEMAIL, "jutowapab@poly-swarm.com")
                .property(Email.FROMNAME, "Scraper")
                .property(Email.SUBJECT, subject)
                .property(Email.TEXTPART, content)
                .property(Email.RECIPIENTS, new JSONArray()
                        .put(new JSONObject()
                                .put("Email", "indrekruubel@gmail.com")));
        try {
            response = client.post(request);
        } catch (MailjetException e) {
            e.printStackTrace();
            return;
        } catch (MailjetSocketTimeoutException e) {
            e.printStackTrace();
            return;
        }
        log.info(response.getData().toString());
    }
}
