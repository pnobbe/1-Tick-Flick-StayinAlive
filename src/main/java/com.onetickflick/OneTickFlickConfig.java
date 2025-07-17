package com.onetickflick;

import net.runelite.client.config.*;

@ConfigGroup("onetickflick")
public interface OneTickFlickConfig extends Config
{
	@Units(Units.MILLISECONDS)
	@ConfigItem(
			keyName = "greenStart",
			name = "Green start",
			description = "How long into the tick the green bar starts. Used as a visual aid and to determine the combo counter.",
			position = 0
	)
	@Range(min = 0, max = 600)
	default int greenStart()
	{
		return 100;
	}

	@Units(Units.MILLISECONDS)
	@ConfigItem(
			keyName = "greenEnd",
			name = "Green end",
			description = "How long into the tick the green bar ends. Used as a visual aid and to determine the combo counter.",
			position = 1
	)
	@Range(min = 0, max = 600)
	default int greenEnd()
	{
		return 500;
	}

	@ConfigItem(
			keyName = "showCombo",
			name = "Show combo counter",
			position = 2,
			description = "Whether to show the combo counter in the overlay. Combo counter is the number of consecutive double clicks within the green bar.")
	default boolean showCombo()
	{
		return true;
	}

	@ConfigItem(
			keyName = "enableTimeout",
			name = "Enable overlay timeout",
			position = 3,
			description = "Whether the overlay should automatically hide after a period without clicking the quick prayer orb.")
	default boolean enableTimeout()
	{
		return false;
	}

	@Units(Units.SECONDS)
	@ConfigItem(
			keyName = "overlayTimeoutSeconds",
			name = "Overlay timeout",
			position = 4,
			description = "How long after last clicking the quick prayer orb the overlay should remain visible")
	@Range(min = 1, max = 600)
	default int overlayTimeoutSeconds()
	{
		return 30;
	}

	@Units(Units.MILLISECONDS)
	@ConfigItem(
			keyName = "clickDelayMilliseconds",
			name = "Click delay - latency",
			position = 5,
			description = "Add a delay to your clicks to account for latency (ping).")
	@Range(min = 0, max = 500)
	default int clickDelayMilliseconds()
	{
		return 0;
	}
}
