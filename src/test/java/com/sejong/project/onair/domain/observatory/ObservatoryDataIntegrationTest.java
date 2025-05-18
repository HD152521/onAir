package com.sejong.project.onair.domain.observatory;

import com.sejong.project.onair.domain.observatory.model.Observatory;
import com.sejong.project.onair.domain.observatory.model.ObservatoryData;
import com.sejong.project.onair.domain.observatory.repository.ObservatoryDataRepository;
import com.sejong.project.onair.domain.observatory.service.ObservatoryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ObservatoryDataIntegrationTest {

    @Autowired
    private ObservatoryDataRepository observatoryDataRepository;
    @Autowired
    private ObservatoryService observatoryService;

    @Autowired
    private DataSource dataSource;
    @Test
    void printDataSource() throws Exception {
        System.out.println(">> DataSource = " + dataSource);
        try (Connection conn = dataSource.getConnection()) {
            System.out.println(">> JDBC URL = " + conn.getMetaData().getURL());
        }
    }

    @Test
    @Transactional
    @Rollback(false)
    void saveTestDataToMySQL() {
        List<ObservatoryData> dataList = new ArrayList<>();
        // 시작 날짜: 2025-05-10 00:00
        LocalDateTime start = LocalDateTime.of(2025, 5, 10, 0, 0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (int i = 0; i < 20; i++) {
            String dateString = start.plusHours(i).format(formatter);

            ObservatoryData data = ObservatoryData.builder()
                    .so2Grade(1)
                    .coFlag(null)
                    .khaiValue(50)
                    .so2Value(0.005)
                    .coValue(0.4)
                    .pm25Flag(null)
                    .pm10Flag(null)
                    .pm10Value(30)
                    .o3Grade(2)
                    .khaiGrade(3)
                    .pm25Value(20)
                    .no2Flag(null)
                    .no2Grade(1)
                    .o3Flag(null)
                    .pm25Grade(2)
                    .so2Flag(null)
                    .dataTimeString(dateString)
                    .coGrade(1)
                    .no2Value(0.02)
                    .pm10Grade(2)
                    .o3Value(0.03)
                    .stationName("종로구")
                    .build();

            // PrePersist 로직으로 dataTime 필드 설정
            data.changeDate();
            dataList.add(data);
        }

        // MySQL에 저장
        observatoryDataRepository.saveAll(dataList);
    }

    @Test
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Transactional
    @Commit
    void saveTestAllOfObservatoryDataToMySQL() {
        List<Observatory> observatories = observatoryService.getAllObservatory();
        List<ObservatoryData> dataList = new ArrayList<>();
        // 시작 날짜: 2025-05-16 00:00
        LocalDateTime start = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Observatory ob : observatories) {
            String dateString = start.format(formatter);

            ObservatoryData data = ObservatoryData.builder()
                    .so2Grade(1)
                    .coFlag(null)
                    .khaiValue(50)
                    .so2Value(0.005)
                    .coValue(0.4)
                    .pm25Flag(null)
                    .pm10Flag(null)
                    .pm10Value(30)
                    .o3Grade(2)
                    .khaiGrade(3)
                    .pm25Value(20)
                    .no2Flag(null)
                    .no2Grade(1)
                    .o3Flag(null)
                    .pm25Grade(2)
                    .so2Flag(null)
                    .dataTimeString(dateString)
                    .coGrade(1)
                    .no2Value(0.02)
                    .pm10Grade(2)
                    .o3Value(0.03)
                    .stationName(ob.getStationName())
                    .build();

            // PrePersist 로직으로 dataTime 필드 설정
            data.changeDate();
            dataList.add(data);
        }
        System.out.println("observatories.size = " + observatories.size());
        System.out.println("dataList.size = " + dataList.size());
        // MySQL에 저장
        observatoryDataRepository.saveAll(dataList);
    }
}
