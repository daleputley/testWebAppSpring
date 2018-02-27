package hello;

import org.springframework.stereotype.Service;

@Service
public class Logic {


    public String replaceCharAt(String s, int pos, String c) {

        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public String evaluate(String results)
    {
        String mark="0";
        int hits=0;
        int  percentage;

        for (int i = 0; i < hello.Config.answerList.size(); i++) {
            if (results.substring(i,i+1).equals(hello.Config.answerList.get(i))){
                hits++;
            }
        }
        percentage=(int)(long)(hits*100/results.length());
        mark=" "+Integer.toString(hits)+" out of "+results.length()+" - "+Integer.toString(percentage)+"%";
        return mark;
    }

}
