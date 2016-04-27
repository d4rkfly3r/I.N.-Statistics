package net.d4rkfly3r.plugins.in_statistics.src;

public class DefaultStatisticService implements StatisticService {
    @Override
    public <T extends StatisticType> void registerStatisticType(Class<T> statisticType) {
    }

    @Override
    public <T extends StatisticType> void fireStatistic(Class<T> statisticType, Object instance) {

    }
}
