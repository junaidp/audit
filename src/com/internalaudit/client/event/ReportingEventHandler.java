package com.internalaudit.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ReportingEventHandler extends EventHandler {
	void onReporting(ReportingEvent event);
}
