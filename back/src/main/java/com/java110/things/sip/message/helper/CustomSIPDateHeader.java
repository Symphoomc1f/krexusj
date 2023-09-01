package com.java110.things.sip.message.helper;

import com.java110.things.util.DateUtils;
import gov.nist.javax.sip.header.SIPHeader;

import java.util.Date;

public class CustomSIPDateHeader extends SIPHeader{

	@Override
	protected StringBuilder encodeBody(StringBuilder buffer) {
		return buffer.append(DateUtils.getGBFormatDate(new Date()));
	}


}
