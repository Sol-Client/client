package io.github.solclient.client.mod.impl;

import java.text.*;
import java.util.Date;

import io.github.solclient.client.mod.option.annotation.Option;

public final class ClockMod extends SolClientSimpleHudMod {

	private static final Date PLACEHOLDER = new Date(0);

	// 01:30 pm
	private static final DateFormat TWELVE_HOUR = new SimpleDateFormat("hh:mm a");
	// 13:30
	private static final DateFormat TWENTY_FOUR_HOUR = new SimpleDateFormat("HH:mm");

	// well, Java has no way to determine this based on locale
	@Option
	private boolean twentyFourHour = true;

	@Override
	public String getText(boolean editMode) {
		Date date = PLACEHOLDER;
		if (!editMode)
			date = new Date();

		return (twentyFourHour ? TWENTY_FOUR_HOUR : TWELVE_HOUR).format(date);
	}

	@Override
	public String getId() {
		return "clock";
	}

}
