package com.prodify.produto_service.shared.utils.strategypattern;

import jakarta.servlet.http.HttpServletRequest;

public interface IpExtractorInterface {
    String extractIp(HttpServletRequest request);
}