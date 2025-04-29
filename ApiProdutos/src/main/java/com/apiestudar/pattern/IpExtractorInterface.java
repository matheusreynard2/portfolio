package com.apiestudar.pattern;

import javax.servlet.http.HttpServletRequest;

public interface IpExtractorInterface {
    String extractIp(HttpServletRequest request);
}