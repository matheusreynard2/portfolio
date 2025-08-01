package com.apiestudar.api_prodify.shared.utils.strategypattern;

import jakarta.servlet.http.HttpServletRequest;

public class HeaderIpExtractor implements IpExtractorInterface {
	
    private final String headerName;

    public HeaderIpExtractor(String headerName) {
        this.headerName = headerName;
    }

    @Override
    public String extractIp(HttpServletRequest request) {
        String ip = request.getHeader(headerName);
        return (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) ? ip : null;
    }
}