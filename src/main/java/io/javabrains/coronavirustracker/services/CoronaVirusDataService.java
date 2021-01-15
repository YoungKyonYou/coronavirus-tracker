package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    private List<LocationStats> allStats=new ArrayList<>();

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    //의존성이 이루어지고 초기화를 수행하는 메서드 즉
    //객체가 생성된 후 별도의 초기화 작업을 위해 실행하는 메서드
     @PostConstruct
     @Scheduled(cron="* * 1 * * *")//매초 이 메소드를 실행한다 regular basis  * 초 * 분 *시간 그니까 여기서는 매일 한 번씩 이 메소드를 실행함
    public void fetchVirusData() throws IOException, InterruptedException {
         List<LocationStats> newStats=new ArrayList<>();

        HttpClient client= HttpClient.newHttpClient();
        HttpRequest request=HttpRequest.newBuilder()//String을 uri로 변환해야 함
                .uri(URI.create(VIRUS_DATA_URL))
                .build();//builder pattern을 이용해 생성
        HttpResponse<String> httpResponse=client.send(request, HttpResponse.BodyHandlers.ofString());//send 하고 응답 받음
        //즉 raw 데이터를 받을 것임 String으로
      //  System.out.println(httpResponse.body()); //그리고 그 받은 데이터를 print함
         StringReader csvBodyReader=new StringReader(httpResponse.body());
         Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader); //parsing 하는 것 csv를 파싱함

         for (CSVRecord record : records) {//컬럼을 추출
             LocationStats locationStat=new LocationStats();
             locationStat.setState(record.get("Province/State"));
             locationStat.setCountry(record.get("Country/Region"));
             int latestCases=Integer.parseInt(record.get(record.size()-1));
             int prevDayCases=Integer.parseInt(record.get(record.size()-2));
             locationStat.setLatestTotalCases(latestCases);
             locationStat.setDiffFromPrevDay(latestCases-prevDayCases);
            // System.out.println(locationStat);
             newStats.add(locationStat);
         }
         this.allStats=newStats;
    }

}
