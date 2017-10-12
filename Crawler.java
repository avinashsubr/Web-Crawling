import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import java.net.URL;
import java.net.URLConnection;
import java.util.Set;
import java.util.regex.Pattern;

public class Crawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|"
            + "json|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml|rss))$");

    private static final Pattern wantedPatterns = Pattern.compile(".*(\\.(bmp|gif|jpe?g|png|tiff?|pdf|html))$");


    Statistics myStat;

    @Override
    public void onStart() {
        myStat=new Statistics();
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {

        String href = url.getURL().toLowerCase();
        String str;

        if (href.startsWith("https://www.washingtonpost.com/") || href.startsWith("http://www.washingtonpost.com/")){
            str=url.getURL().replace(',','-')+",OK"+"\n";
            myStat.addToCSV3(str);
        }else{
            str=url.getURL().replace(',','-')+",N_OK"+"\n";
            myStat.addToCSV3(str);
            return false;

        }

        if(FILTERS.matcher(href).matches()){
            return false;

        }

        if(wantedPatterns.matcher(href).matches()){
            return true;
        }


        try {
            URL urlLink = new URL(url.getURL());
            URLConnection u = urlLink.openConnection();
            String type = u.getHeaderField("Content-Type");
            if(type==null){
                return false;
            }
            if(type.contains("image") || type.contains("pdf")|| type.contains("html")){
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }




        return  false;

    }


    @Override
    public void visit(Page page) {

        String url = page.getWebURL().getURL();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();
            String contentType=page.getContentType().toLowerCase().replace(";charset=utf-8","");
            contentType=contentType.replace("; charset=utf-8","");
            myStat.addToCSV2(url.replace(',','-')+","+page.getContentData().length+","+links.size()+","+contentType+"\n");
        }


        if(page.getParseData() instanceof BinaryParseData){

            BinaryParseData binaryParseData=(BinaryParseData) page.getParseData();
            Set<WebURL> links = binaryParseData.getOutgoingUrls();
            String contentType=page.getContentType().toLowerCase().replace(";charset=utf-8","");
            contentType=contentType.replace("; charset=utf-8","");
            myStat.addToCSV2(url.replace(',','-')+","+page.getContentData().length+","+links.size()+","+contentType+"\n");


        }


    }

    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        myStat.addToCSV1(webUrl.getURL().replace(',','-')+","+statusCode+"\n");
    }




    @Override
    public Object getMyLocalData() {
        return myStat;
    }





}
