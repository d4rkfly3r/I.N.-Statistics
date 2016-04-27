package net.d4rkfly3r.plugins.in_statistics.src;

public interface StatisticService {

    <T extends StatisticType> void registerStatisticType(Class<T> statisticType);

    <T extends StatisticType> void fireStatistic(Class<T> statisticType, Object instance);
}
