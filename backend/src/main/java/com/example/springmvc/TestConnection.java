package com.example.springmvc;

import com.example.springmvc.config.AppConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("Đang thử kết nối tới TiDB Cloud...");

        try {
            // Nạp cấu hình từ AppConfig
            ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

            // Lấy bean DataSource
            DataSource dataSource = context.getBean(DataSource.class);

            // Thử mở kết nối
            Connection connection = dataSource.getConnection();

            if (connection != null) {
                System.out.println("✅ KẾT NỐI THÀNH CÔNG!");
                System.out.println("Database Product: " + connection.getMetaData().getDatabaseProductName());
                System.out.println("URL: " + connection.getMetaData().getURL());
                connection.close();
            }
        } catch (Exception e) {
            System.err.println("❌ KẾT NỐI THẤT BẠI!");
            e.printStackTrace();
        }
    }
}