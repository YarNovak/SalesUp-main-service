package io.proj3ct.SpringDemoBot.Cash.Rabiit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheInvalidationMessage {
    String cacheName;
    String key;
}