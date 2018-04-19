package hello;

public class WebContent {

        public int index=0;
        public String link="<a href='#'>Primul link</a>";
        public String answerStatus="not answered";

        //------------------------------Getters and Setters------------------------------

        public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(String answerStatus) {
        this.answerStatus = answerStatus;
    }
}
