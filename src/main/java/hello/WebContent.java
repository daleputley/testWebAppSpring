package hello;

public class WebContent {

    public int index = 0;

    public String answerStatus = "not yet answered";
    public String rowCss = "display:none;";
    public String bootstrapClass;

    //------------------------------Getters and Setters------------------------------

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(String answerStatus) {
        this.answerStatus = answerStatus;
    }

    public String getRowCss() {
        return rowCss;
    }

    public void setRowCss(String rowCss) {
        this.rowCss = rowCss;
    }

    public String getBootstrapClass() {
        return bootstrapClass;
    }

    public void setBootstrapClass(String bootstrapClass) {
        this.bootstrapClass = bootstrapClass;
    }
}
