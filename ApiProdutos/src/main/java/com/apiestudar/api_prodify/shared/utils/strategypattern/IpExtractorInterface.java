package com.apiestudar.api_prodify.shared.utils.strategypattern;

import jakarta.servlet.http.HttpServletRequest;

public interface IpExtractorInterface {
    String extractIp(HttpServletRequest request);
}