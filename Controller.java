import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


public class Controller {
    public static void main(String[] args) throws Exception{
        String crawlStorageFolder="/data/crawl";
        int noOfCrawlers=7;

        final int maxDepthofCrawlng=16;
        final int maxPagesFetch=20000;


        CrawlConfig config=new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxDepthOfCrawling(maxDepthofCrawlng);
        config.setMaxPagesToFetch(maxPagesFetch);
        config.setIncludeHttpsPages(true);
        config.setIncludeBinaryContentInCrawling(true);
        config.setMaxDownloadSize(52428800);
        //config.setProcessBinaryContentInCrawling(true);
        //config.setResumableCrawling(true);
        config.setPolitenessDelay(500);
        
        /*
        * Instantiate the controller for this crawl.
        */

        PageFetcher pageFetcher=new PageFetcher(config);
        RobotstxtConfig robotstxtConfig=new RobotstxtConfig();
        RobotstxtServer robotstxtServer=new RobotstxtServer(robotstxtConfig,pageFetcher);
        CrawlController crawlController=new CrawlController(config,pageFetcher,robotstxtServer);

        /*
        * For each crawl, you need to add some seed urls. These are the first
        * URLs that are fetched and then the crawler starts following links
        * which are found in these pages
         */
        crawlController.addSeed("https://www.washingtonpost.com/");

        /*
        * Start the crawl. This is a blocking operation, meaning that your code
        * will reach the line after this only when crawling is finished.
        */
        crawlController.start(Crawler.class, noOfCrawlers);

        //get all crawlers and get thier data and add to csv file.

        List<Object> crawlersLocalData = crawlController.getCrawlersLocalData();

        PrintWriter csv1File = new PrintWriter(new File("fetch_Washington_Post.csv"));
        PrintWriter csv2File = new PrintWriter(new File("visit_Washington_Post.csv"));
        PrintWriter csv3File= new PrintWriter(new File("urls_Washington_Post.csv"));

        StringBuilder csv1String = new StringBuilder();
        StringBuilder csv2String = new StringBuilder();
        StringBuilder csv3String = new StringBuilder();

        HashMap<String,Integer> statusHashMap=new HashMap<String, Integer>();
        HashMap<String,Integer> contentTypeHashMap=new HashMap<String, Integer>();

        HashSet<String> uniqueURLS=new HashSet<String>();
        HashSet<String> uniqueURLSWithinSite=new HashSet<String>();
        HashSet<String> uniqueURLSOutsideSite=new HashSet<String>();
        HashMap<String, Integer> sizeHashMap=new HashMap<String, Integer>();
        sizeHashMap.put("<1KB",0);
        sizeHashMap.put("1KB-10KB",0);
        sizeHashMap.put("10KB-100KB",0);
        sizeHashMap.put("100KB-1MB",0);
        sizeHashMap.put(">1MB",0);


        for(Object localData:crawlersLocalData){

            Statistics stat = (Statistics) localData;
            ArrayList<String> csv1List= stat.getCSV1();
            ArrayList<String> csv2List= stat.getCSV2();
            ArrayList<String> csv3List= stat.getCSV3();

            for(int i=0;i<csv1List.size();i++){
                String[] ary=csv1List.get(i).split(",");

                if(statusHashMap.containsKey(ary[1])){
                    statusHashMap.put(ary[1],statusHashMap.get(ary[1])+1);

                }else{
                    statusHashMap.put(ary[1],1);
                }

                csv1String.append(csv1List.get(i));
            }

            for(int i=0;i<csv2List.size();i++){

                String[] ary=csv2List.get(i).split(",");

                if(contentTypeHashMap.containsKey(ary[3])){
                    contentTypeHashMap.put(ary[3],contentTypeHashMap.get(ary[3])+1);

                }else{
                    contentTypeHashMap.put(ary[3],1);
                }

                double sizeInKB=Double.parseDouble(ary[1])/1024;



                if(sizeInKB<1){

                    sizeHashMap.put("<1KB",sizeHashMap.get("<1KB")+1);
                }else if (sizeInKB>=1 && sizeInKB<10){

                    sizeHashMap.put("1KB-10KB",sizeHashMap.get("1KB-10KB")+1);
                }else if (sizeInKB>=10 && sizeInKB<100){
                    sizeHashMap.put("10KB-100KB",sizeHashMap.get("10KB-100KB")+1);
                }else if (sizeInKB>=100 && sizeInKB<1000){
                    sizeHashMap.put("100KB-1MB",sizeHashMap.get("100KB-1MB")+1);
                }else {

                    sizeHashMap.put(">1MB",sizeHashMap.get(">1MB")+1);
                }


                csv2String.append(csv2List.get(i));
            }

            for(int i=0;i<csv3List.size();i++){

                String[] ary=csv3List.get(i).split(",");
                uniqueURLS.add(ary[0]);

                if(ary[1].equals("OK\n")){
                    uniqueURLSWithinSite.add(ary[0]);
                }else if(ary[1].equals("N_OK\n")){
                    uniqueURLSOutsideSite.add(ary[0]);
                }

                csv3String.append(csv3List.get(i));
            }



        }

        csv1File.write(csv1String.toString());
        csv1File.close();

        csv2File.write(csv2String.toString());
        csv2File.close();

        csv3File.write(csv3String.toString());
        csv3File.close();


        System.out.println("Unique URLS: "+uniqueURLS.size());
        System.out.println("Unique URLS within website: "+uniqueURLSWithinSite.size());
        System.out.println("Unique URLS ouside site: "+uniqueURLSOutsideSite.size());

        for(String key:statusHashMap.keySet()){
            System.out.println(key+": "+statusHashMap.get(key));
        }

        for(String key:contentTypeHashMap.keySet()){
            System.out.println(key+": "+contentTypeHashMap.get(key));
        }


        for(String key:sizeHashMap.keySet()){
            System.out.println(key+": "+sizeHashMap.get(key));
        }




    }

}
