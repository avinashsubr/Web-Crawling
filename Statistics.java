import java.util.ArrayList;

public class Statistics {

    private ArrayList<String> csv1;
    private ArrayList<String> csv2;
    private ArrayList<String> csv3;

    Statistics(){
        csv1=new ArrayList<String>();
        csv2=new ArrayList<String>();
        csv3=new ArrayList<String>();
    }


    public void addToCSV1(String str){

        csv1.add(str);

    }

    public void addToCSV2(String str){

        csv2.add(str);

    }

    public void addToCSV3(String str){

        csv3.add(str);

    }

    public ArrayList<String> getCSV1(){
        return csv1;
    }

    public ArrayList<String> getCSV2(){
        return csv2;
    }

    public ArrayList<String> getCSV3(){
        return csv3;
    }


}
