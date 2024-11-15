package com.nap_blog.utils;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import org.json.JSONObject;

import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class IpUtil {
   private static final String IPV4_DB_PATH = "src/main/resources/data/IP2LOCATION-LITE-DB3.BIN";
   private static final String IPV6_DB_PATH = "src/main/resources/data/IP2LOCATION-LITE-DB3.IPV6.BIN";
   private static final String CITIES_JSON_PATH = "src/main/resources/data/cities.json";

   private static IP2Location ip2locationV4;
   private static IP2Location ip2locationV6;
   private static final Map<String, String> cityMap = new HashMap<>();

   static {
      try {
         ip2locationV4 = new IP2Location();
         ip2locationV4.Open(IPV4_DB_PATH);
         ip2locationV6 = new IP2Location();
         ip2locationV6.Open(IPV6_DB_PATH);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public static String getIpRegion(String ip) {
      try {
         String ipType = getIpType(ip);
         loadCityData();
         IPResult result;

         if ("IPv4".equals(ipType)) {
            result = ip2locationV4.IPQuery(ip);
            ip2locationV4.notify();

         } else if ("IPv6".equals(ipType)) {
            result = ip2locationV6.IPQuery(ip);
         } else {
            return "无效的 IP 地址";
         }
         String city = result.getCity();
         return (city!=null&&!city.isEmpty()?translateCity(result.getCity()):"未知") ;
      } catch (Exception e) {
         e.printStackTrace();
         return "";
      }
   }

   private static String getIpType(String ip) {
      try {
         InetAddress address = InetAddress.getByName(ip);
         return (address instanceof java.net.Inet4Address) ? "IPv4" : "IPv6";
      } catch (Exception e) {
         return "无效的 IP 地址";
      }
   }

   private static void loadCityData() {
      if (cityMap.isEmpty()) {
         try {
            String content = new String(Files.readAllBytes(Paths.get(CITIES_JSON_PATH)));
            JSONObject jsonObject = new JSONObject(content);
            jsonObject.keys().forEachRemaining(key -> cityMap.put(key, jsonObject.getString(key)));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static String translateCity(String cityName) {
      return cityMap.getOrDefault(cityName, cityName);
   }
}
