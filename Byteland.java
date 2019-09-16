package com.cem.byteland;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;

public class Byteland {

    public static void main(String[] args) throws NumberFormatException, IOException {

        int count = 0;
        int city_count;
        String line;
        List<City> cities = new ArrayList<City>();
        Scanner reader = new Scanner(System.in);
        List<Integer> results = new ArrayList<Integer>();

        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        int testCases = Integer.parseInt(in.readLine());

        /*if (testCases < 1000) {*/
        City city = new City(0);
        cities.add(city);

        for (int i = 0; i < testCases; i++) {

            line = in.readLine();
            city_count = Integer.valueOf(line);
            line = in.readLine();

            String[] tokens = line.split(" ");

            List<Integer> cityList = new ArrayList<Integer>();

            for (String t : tokens) {
                int num = Integer.valueOf(t);
                cityList.add(num);

            }
            if (2 <= cityList.size() /*&& cityList.size() <= 600*/) {
                int j = 1;
                for (String t : tokens) {
                    city = new City(j);
                    int roadto = cityList.get(j - 1);
                    city.next = cities.get(roadto);
                    cities.get(roadto).road_num++;
                    cities.add(city);

                    j++;
                }


                while (cities.size() > 1) {
                    Collections.sort(cities);
                    List<City> newList = new ArrayList<City>();

                    for (int k = 0; k < cities.size(); k++) {
                        mergeCities(cities, k);
                    }

                    for (City org_city : cities) {
                        if (!org_city.consumed) {
                            org_city.merged = false;
                            if (org_city.next != null && org_city.next.isConsumed()) {
                                org_city.next = org_city.next.next;
                            }
                            newList.add(org_city);
                        }

                    }
                    cities = newList;
                    count++;
                    // System.out.println("next step");
                }

                results.add(count);
                count = 0;

            }
            else{
                results.add(count);

            }

        }

        for(int res: results)
        {
            System.out.println(res);
        }

		/*}
		else{*/

        /*}*/
    }

    public static void mergeCities(List<City> cities, int num) {
        City city1 = cities.get(num);

        if (city1.next != null && !city1.isMerged() && !city1.next.isMerged()) {
            City city2 = cities.get(cities.indexOf(city1.next));
            // System.out.println(city1.city_num + " and " + city2.city_num + "
            // are connected");

            city1.consumed = true;
            city1.merged = true;
            city2.merged = true;
            city2.road_num = city2.road_num + city1.road_num - 1;

        }

    }

    public static class City implements Comparable<City> {
        public int city_num;
        public int road_num;
        public boolean consumed;
        public boolean merged;
        public City next;

        public City() {
            road_num = 0;
            consumed = false;
            merged = false;
            next = null;
        }

        public City(int num) {
            city_num = num;
            road_num = 0;
            consumed = false;
            merged = false;
            next = null;
        }

        public City(int num, int road_num, City next) {
            city_num = num;
            this.road_num = road_num;
            consumed = false;
            merged = false;
            this.next = next;
        }

        public int getCity_num() {
            return city_num;
        }

        public void setCity_num(int city_num) {
            this.city_num = city_num;
        }

        public int getroad_num() {
            return road_num;
        }

        public void setroad_num(int road_num) {
            this.road_num = road_num;
        }

        public boolean isConsumed() {
            return consumed;
        }

        public void setConsumed(boolean consumed) {
            this.consumed = consumed;
        }

        public City getNext() {
            return next;
        }

        public void setNext(City next) {
            this.next = next;
        }

        public boolean isMerged() {
            return merged;
        }

        public void setMerged(boolean merged) {
            this.merged = merged;
        }

        @Override
        public int compareTo(City compareroad) {
            int compareroad_num = ((City) compareroad).getroad_num();

            return this.road_num - compareroad_num;

        }

    }
}