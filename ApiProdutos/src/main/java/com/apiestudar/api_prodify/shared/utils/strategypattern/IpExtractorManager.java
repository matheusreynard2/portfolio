package com.apiestudar.api_prodify.shared.utils.strategypattern;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class IpExtractorManager {

    private final List<IpExtractorInterface> listaStrategies;

    public IpExtractorManager(List<IpExtractorInterface> listaStrategies) {
        this.listaStrategies = listaStrategies;
    }

    public String extractIp(HttpServletRequest request) {
        for (IpExtractorInterface strategy : listaStrategies) {
            String ip = strategy.extractIp(request);
            if (ip != null) {
                return ip.contains(",") ? ip.split(",")[0].trim() : ip;
            }
        }
        return request.getRemoteAddr();
    }
}