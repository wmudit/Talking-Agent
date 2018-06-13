package co.pucho.pucho;

public class DataModel {

    private int _id;
    private String speaker;
    private String speech;

    public DataModel(String speaker, String speech) {
        this.speaker = speaker;
        this.speech = speech;
    }

    public DataModel() {

    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public void setSpeech(String speech) {
        this.speech = speech;
    }

    public int get_id() {

        return _id;
    }

    public String getSpeaker() {
        return speaker;
    }

    public String getSpeech() {
        return speech;
    }
}
