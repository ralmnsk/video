package ralmnsk.video.model;

import ralmnsk.video.dto.UserDto;

public class Msg {
    private String event;
    private UserDto data;

    public Msg() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public UserDto getData() {
        return data;
    }

    public void setData(UserDto data) {
        this.data = data;
    }
}
