package com.akqa.aem.training.aem201.core.listeners;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.observation.ResourceChangeListener;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

@Component(immediate = true, service = EventHandler.class, property = {
            Constants.SERVICE_DESCRIPTION + "= This event handler listens the events on page activation",
            EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/ADDED",
            EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/CHANGED",
            EventConstants.EVENT_TOPIC + "=org/apache/sling/api/resource/Resource/REMOVED",
             EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC,
             EventConstants.EVENT_FILTER + "(&" + "(path=/content/aem201/us/en/*/jcr:content) (|("
                + SlingConstants.PROPERTY_CHANGED_ATTRIBUTES + "=*jcr:title) " + "(" + ResourceChangeListener.CHANGES
                + "=*jcr:title)))" })

public class SlingEventHandler implements EventHandler {
    private static final Logger log = LoggerFactory.getLogger(SlingEventHandler.class);

    @Override
    public void handleEvent(Event event) {
        JSONObject jsonObject = new JSONObject();
        JSONObject appendJson = new JSONObject();

        Iterator<PageModification> pageInfo = PageEvent.fromEvent(event).getModifications();
        while (pageInfo.hasNext()) {
            PageModification pageModification = pageInfo.next();
            log.info("\n Type :  {},  Page : {}", pageModification.getType(), pageModification.getPath());

            try {
                jsonObject.put("Page URL", "http://localhost:4502" +pageModification.getPath() + ".html");
                jsonObject.put("Event is", pageModification.getType());
                jsonObject.put("Last Modified", pageModification.getModificationDate());
                jsonObject.put("WHo triggered the event",pageModification.getUserId());

                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);

                appendJson.put("Event", jsonArray);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                FileWriter file = new FileWriter("/Users/riya.grover/Desktop/aem2/output.json");
                file.write(appendJson.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}




