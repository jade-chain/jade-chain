package com.itranswarp.crypto.manage.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateCheckout {
    private List<Checkout> item;

    public List<Checkout> getItem() {
        return this.item;
    }

    public void setItem(List<Checkout> item) {
        this.item = item;
    }

    public class Checkout {
        private Map<String, String> secondsMap;
        private Map<String, String> minutesMap;
        private Map<String, String> hoursMap;
        private Map<String, String> daysOfMonthMap;
        private Map<String, String> monthsMap;
        private Map<String, String> daysOfWeekMap;
        private Map<String, String> lastdayOfWeekMap;
        private Map<String, String> nearestWeekdayMap;
        private Map<String, String> NthDayOfWeekMap;
        private Map<String, String> lastdayOfMonthMap;
        private Map<String, String> yearsMap;

        /**
         * @param ExpressionSummary
         *        cron表达式解析出的字符串
         */
        public Checkout(String ExpressionSummary) {
            this.secondsMap = new HashMap<>();
            this.minutesMap = new HashMap<>();
            this.hoursMap = new HashMap<>();
            this.daysOfMonthMap = new HashMap<>();
            this.monthsMap = new HashMap<>();
            this.daysOfWeekMap = new HashMap<>();
            this.lastdayOfWeekMap = new HashMap<>();
            this.nearestWeekdayMap = new HashMap<>();
            this.NthDayOfWeekMap = new HashMap<>();
            this.lastdayOfMonthMap = new HashMap<>();
            this.yearsMap = new HashMap<>();
            this.analysis(ExpressionSummary);
        }

        public void analysis(String analysis) {
            String[] split = analysis.split("\n");
            String[] items = null;
            for (String string : split) {
                items = string.split(":");
                switch (items[0]) {
                    case "seconds":
                        this.putMap(this.secondsMap, items[1]);
                        break;
                    case "minutes":
                        this.putMap(this.minutesMap, items[1]);
                        break;
                    case "hours":
                        this.putMap(this.hoursMap, items[1]);
                        break;
                    case "daysOfMonth":
                        this.putMap(this.daysOfMonthMap, items[1]);
                        break;
                    case "months":
                        this.putMap(this.monthsMap, items[1]);
                        break;
                    case "daysOfWeek":
                        this.putMap(this.daysOfWeekMap, items[1]);
                        break;
                    case "lastdayOfWeek":
                        this.putMap(this.lastdayOfWeekMap, items[1]);
                        break;
                    case "nearestWeekday":
                        this.putMap(this.nearestWeekdayMap, items[1]);
                        break;
                    case "NthDayOfWeek":
                        this.putMap(this.NthDayOfWeekMap, items[1]);
                        break;
                    case "lastdayOfMonth":
                        this.putMap(this.lastdayOfMonthMap, items[1]);
                        break;
                    case "years":
                        this.putMap(this.yearsMap, items[1]);
                        break;
                    default:
                        break;
                }
            }

        }

        private void putMap(Map<String, String> map, String items) {
            String[] value = items.split(",");
            for (String element : value) {
                map.put(element.trim(), element.trim());
            }
        }

        public Map<String, String> getSecondsMap() {
            return this.secondsMap;
        }

        public void setSecondsMap(Map<String, String> secondsMap) {
            this.secondsMap = secondsMap;
        }

        public Map<String, String> getMinutesMap() {
            return this.minutesMap;
        }

        public void setMinutesMap(Map<String, String> minutesMap) {
            this.minutesMap = minutesMap;
        }

        public Map<String, String> getHoursMap() {
            return this.hoursMap;
        }

        public void setHoursMap(Map<String, String> hoursMap) {
            this.hoursMap = hoursMap;
        }

        public Map<String, String> getDaysOfMonthMap() {
            return this.daysOfMonthMap;
        }

        public void setDaysOfMonthMap(Map<String, String> daysOfMonthMap) {
            this.daysOfMonthMap = daysOfMonthMap;
        }

        public Map<String, String> getMonthsMap() {
            return this.monthsMap;
        }

        public void setMonthsMap(Map<String, String> monthsMap) {
            this.monthsMap = monthsMap;
        }

        public Map<String, String> getDaysOfWeekMap() {
            return this.daysOfWeekMap;
        }

        public void setDaysOfWeekMap(Map<String, String> daysOfWeekMap) {
            this.daysOfWeekMap = daysOfWeekMap;
        }

        public Map<String, String> getLastdayOfWeekMap() {
            return this.lastdayOfWeekMap;
        }

        public void setLastdayOfWeekMap(Map<String, String> lastdayOfWeekMap) {
            this.lastdayOfWeekMap = lastdayOfWeekMap;
        }

        public Map<String, String> getNearestWeekdayMap() {
            return this.nearestWeekdayMap;
        }

        public void setNearestWeekdayMap(Map<String, String> nearestWeekdayMap) {
            this.nearestWeekdayMap = nearestWeekdayMap;
        }

        public Map<String, String> getNthDayOfWeekMap() {
            return this.NthDayOfWeekMap;
        }

        public void setNthDayOfWeekMap(Map<String, String> nthDayOfWeekMap) {
            this.NthDayOfWeekMap = nthDayOfWeekMap;
        }

        public Map<String, String> getLastdayOfMonthMap() {
            return this.lastdayOfMonthMap;
        }

        public void setLastdayOfMonthMap(Map<String, String> lastdayOfMonthMap) {
            this.lastdayOfMonthMap = lastdayOfMonthMap;
        }

        public Map<String, String> getYearsMap() {
            return this.yearsMap;
        }

        public void setYearsMap(Map<String, String> yearsMap) {
            this.yearsMap = yearsMap;
        }
    }
}
