package com.greengrowapps.remoteopenfile.server.Messages;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonAutoDetect(fieldVisibility= JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "msgT")
@JsonSubTypes({
        @JsonSubTypes.Type(value = HelloFromClientMsg.class, name = BaseMsg.HELLO_FROM_CLIENT),
        @JsonSubTypes.Type(value = HelloFromServerMsg.class, name = BaseMsg.HELLO_FROM_SERVER),
        @JsonSubTypes.Type(value = RemoteOpenFileMsg.class, name = BaseMsg.REMOTE_OPEN_FILE),
        @JsonSubTypes.Type(value = EchoMsg.class, name = BaseMsg.ECHO)
})
public class BaseMsg {
    public static final String HELLO_FROM_CLIENT = "1";
    public static final String HELLO_FROM_SERVER = "2";
    public static final String REMOTE_OPEN_FILE = "3";
    public static final String ECHO = "4";
}
