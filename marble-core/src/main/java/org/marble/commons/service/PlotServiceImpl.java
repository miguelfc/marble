package org.marble.commons.service;

import java.math.BigInteger;
import java.util.List;

import org.marble.commons.domain.repository.PlotRepository;
import org.marble.commons.domain.model.Plot;
import org.marble.commons.exception.InvalidPlotException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlotServiceImpl implements PlotService {

    // private static final Logger log =
    // LoggerFactory.getLogger(PlotServiceImpl.class);

    @Autowired
    PlotRepository plotRepository;

    @Override
    public Plot findOne(BigInteger id) throws InvalidPlotException {
        Plot plot = plotRepository.findOne(id);
        if (plot == null) {
            throw new InvalidPlotException();
        }
        return plot;
    }

    @Override
    public Plot save(Plot plot) throws InvalidPlotException {
        plot = plotRepository.save(plot);
        if (plot == null) {
            throw new InvalidPlotException();
        }
        return plot;
    }

    @Override
    public Long count() {
        return plotRepository.count();
    }

    @Override
    public Long deleteByTopicName(String name) {
        return plotRepository.deleteByTopic_name(name);
    }

    @Override
    public void delete(BigInteger id) {
        plotRepository.delete(id);
        return;
    }

}
