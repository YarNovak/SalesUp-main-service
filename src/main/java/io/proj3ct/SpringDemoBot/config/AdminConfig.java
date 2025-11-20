package io.proj3ct.SpringDemoBot.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@Data
@PropertySource("application.properties")
public class AdminConfig {
    
    @Value("#{'${bot.admins}'.split(',')}")
    List<Long> adminIds;
}
