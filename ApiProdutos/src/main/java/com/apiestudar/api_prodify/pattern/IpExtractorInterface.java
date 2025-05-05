package com.apiestudar.api_prodify.pattern;

import javax.servlet.http.HttpServletRequest;

public interface IpExtractorInterface {
    String extractIp(HttpServletRequest request);
}