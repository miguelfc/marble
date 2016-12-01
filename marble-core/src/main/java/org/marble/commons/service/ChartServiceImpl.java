package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.domain.repository.ChartRepository;
import org.marble.commons.exception.InvalidChartException;
import org.marble.model.domain.model.Chart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChartServiceImpl implements ChartService {

    // private static final Logger log =
    // LoggerFactory.getLogger(PlotServiceImpl.class);

    @Autowired
    ChartRepository chartRepository;

    @Override
    public Chart findOne(BigInteger id) throws InvalidChartException {
        Chart plot = chartRepository.findOne(id);
        if (plot == null) {
            throw new InvalidChartException();
        }
        return plot;
    }

    @Override
    public Chart save(Chart plot) throws InvalidChartException {
        plot = chartRepository.save(plot);
        if (plot == null) {
            throw new InvalidChartException();
        }
        return plot;
    }

    @Override
    public Long count() {
        return chartRepository.count();
    }

    @Override
    public Long deleteByTopicName(String name) {
        return chartRepository.deleteByTopic_name(name);
    }

    @Override
    public void delete(BigInteger id) {
        chartRepository.delete(id);
        return;
    }

}
