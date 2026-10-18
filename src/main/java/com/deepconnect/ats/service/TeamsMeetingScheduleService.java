package com.deepconnect.ats.service;

import com.deepconnect.ats.common.microsoft.GraphClient;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Port of teams_meeting_service.get_meeting_schedule_rsp using Microsoft Graph instead of exchangelib. */
@Service
@RequiredArgsConstructor
@Slf4j
public class TeamsMeetingScheduleService {

    private static final String MSG_CLASS_PROP = "String 0x001A";
    private static final String CONVERSATION_TOPIC_PROP = "String 0x0070";
    private static final Map<String, String> RESPONSE_CLASSES = Map.of(
            "IPM.Schedule.Meeting.Resp.Pos", "Accept",
            "IPM.Schedule.Meeting.Resp.Neg", "Decline",
            "IPM.Schedule.Meeting.Resp.Tent", "Tentative");

    /** One meeting response mail; eventId is the Graph event id (url-safe form). */
    public record MeetingResponse(Instant received, String eventId, String senderName, String senderAddress,
                                  String responseType, String subject) {
    }

    private final GraphClient graphClient;

    @Value("${ats.mail.username:}")
    private String mailbox;

    /** Meeting responses received after timeFrom (Asia/Shanghai local time, may be null), newest first. */
    public List<MeetingResponse> getMeetingScheduleRsp(LocalDateTime timeFrom) {
        List<MeetingResponse> result = new ArrayList<>();
        StringBuilder path = new StringBuilder("/users/").append(mailbox).append("/mailFolders/inbox/messages");
        path.append("?$orderby=receivedDateTime desc&$top=50");
        if (timeFrom != null) {
            String utc = DateTimeFormatter.ISO_INSTANT.format(
                    timeFrom.atZone(com.deepconnect.ats.common.utils.TimeUtil.DEFAULT_ZONE).toInstant());
            path.insert(path.indexOf("?") + 1, "$filter=receivedDateTime gt " + utc + "&");
        }
        path.append("&$select=id,subject,sender,receivedDateTime,@odata.type");
        path.append("&$expand=singleValueExtendedProperties($filter=id eq '").append(MSG_CLASS_PROP)
                .append("' or id eq '").append(CONVERSATION_TOPIC_PROP).append("')");
        try {
            String next = path.toString();
            while (next != null) {
                JsonNode page = graphClient.get(next);
                for (JsonNode msg : page.path("value")) {
                    MeetingResponse rsp = toResponse(msg);
                    if (rsp != null) {
                        result.add(rsp);
                    }
                }
                next = page.hasNonNull("@odata.nextLink") ? page.get("@odata.nextLink").asText() : null;
            }
        } catch (Exception e) {
            log.error("get mails from {} failed, error: {}", mailbox, e.getMessage());
            return new ArrayList<>();
        }
        return result;
    }

    private MeetingResponse toResponse(JsonNode msg) {
        String messageClass = null;
        String topic = null;
        for (JsonNode p : msg.path("singleValueExtendedProperties")) {
            String id = p.path("id").asText();
            if (MSG_CLASS_PROP.equalsIgnoreCase(id)) {
                messageClass = p.path("value").asText(null);
            } else if (CONVERSATION_TOPIC_PROP.equalsIgnoreCase(id)) {
                topic = p.path("value").asText(null);
            }
        }
        String responseType = messageClass == null ? null : RESPONSE_CLASSES.get(messageClass);
        if (responseType == null) {
            return null;
        }
        String eventId = fetchEventId(msg.path("id").asText());
        if (eventId == null) {
            return null;
        }
        JsonNode sender = msg.path("sender").path("emailAddress");
        String subject = topic != null ? topic : msg.path("subject").asText(null);
        return new MeetingResponse(Instant.parse(msg.path("receivedDateTime").asText()), eventId,
                sender.path("name").asText(null), sender.path("address").asText(null), responseType, subject);
    }

    private String fetchEventId(String messageId) {
        try {
            JsonNode event = graphClient.get("/users/" + mailbox + "/messages/" + messageId
                    + "/microsoft.graph.eventMessage/event?$select=id");
            return event.hasNonNull("id") ? event.get("id").asText() : null;
        } catch (Exception e) {
            log.debug("no associated event for message {}: {}", messageId, e.getMessage());
            return null;
        }
    }

    /** Port of meeting_id_convert: Outlook/EWS id (+ /) <-> Graph id (_ -). */
    public static String meetingIdConvert(String meetingId, boolean fromOutlook) {
        if (meetingId == null) {
            return null;
        }
        return fromOutlook ? meetingId.replace("/", "-").replace("+", "_")
                : meetingId.replace("-", "/").replace("_", "+");
    }

    public static Set<String> responseClasses() {
        return RESPONSE_CLASSES.keySet();
    }
}
