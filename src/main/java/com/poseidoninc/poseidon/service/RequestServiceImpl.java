package com.poseidoninc.poseidon.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;

/**
 * Implementation class for RequestService
 *
 * @see RequestService
 *
 * @author olivier morel
 */
@Service
public class RequestServiceImpl implements RequestService {

	@Override
	public String requestToString(WebRequest request) {
		//uri in StringBuffer
		StringBuffer parameters = new StringBuffer(request.getDescription(false)+"?"); 
		//p = parameter key of values v = String[]
		request.getParameterMap().forEach((p,v) -> {
			if (!p.equals("password")) {
				parameters.append(p + "=");
				int i = 0;
				while (i < (v.length - 1)) {
					parameters.append(v[i] + ",");
					i++;
				}
				parameters.append(v[i] + "&");
			}
		});
		int length = parameters.length();
		parameters.delete(length-1, length);
		return parameters.toString();
	}
}