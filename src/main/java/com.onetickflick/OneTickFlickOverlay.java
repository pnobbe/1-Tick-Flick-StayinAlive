package com.onetickflick;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import lombok.Setter;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class OneTickFlickOverlay extends Overlay
{
	private static final int MIN_BAR_HEIGHT = 12;
	private static final int TEXT_SPACE = 20;
	private static final int X_SIZE = 4;
	private static final int TICK_LENGTH = 600;
	private static final Dimension MIN_SIZE = new Dimension(50, MIN_BAR_HEIGHT + TEXT_SPACE);
	private static final Dimension DEFAULT_SIZE = new Dimension(150, MIN_BAR_HEIGHT + TEXT_SPACE);

	private final OneTickFlickPlugin plugin;
	private final List<Integer> clickOffsets = new CopyOnWriteArrayList<>();
	private volatile boolean visible = true;

	@Setter
	private int greenStart;
	@Setter
	private int greenEnd;
	@Setter
	private boolean showCombo;
	@Setter
	private Color targetZoneColor;
	@Setter
	private Color backgroundColor;
	@Setter
	private Color clickColor;
	@Setter
	private Color swipeLineColor;
	@Setter
	private Color borderColor;
	@Setter
	private Color comboTextColor;


	@Inject
	OneTickFlickOverlay(OneTickFlickPlugin plugin, OneTickFlickConfig config)
	{
		this.plugin = plugin;
		greenStart = config.greenStart();
		greenEnd = config.greenEnd();
		showCombo = config.showCombo();
		targetZoneColor = config.targetZoneColor();
		backgroundColor = config.backgroundColor();
		clickColor = config.clickColor();
		swipeLineColor = config.swipeLineColor();
		borderColor = config.borderColor();
		comboTextColor = config.comboTextColor();

		setPosition(OverlayPosition.BOTTOM_LEFT);
		setPreferredSize(DEFAULT_SIZE);
		setResizable(true);
	}

	void recordClick(int offset)
	{
		clickOffsets.add(offset);
	}

	void newTick()
	{
		clickOffsets.clear();
	}

	void setVisible(boolean v)
	{
		visible = v;
	}

	boolean isVisible()
	{
		return visible;
	}

	@Override
	public Dimension render(Graphics2D g)
	{
		if (!visible)
		{
			return null;
		}

		Rectangle bounds = getBounds();
		Dimension size = getPreferredSize() == null ? DEFAULT_SIZE : getPreferredSize();

		int width = size.width;
		int height = size.height;
		if (bounds != null && bounds.width > 0 && bounds.height > 0)
		{
			width = Math.max(bounds.width, MIN_SIZE.width);
			height = Math.max(bounds.height, MIN_SIZE.height);
		}

		int barHeight = Math.max(MIN_BAR_HEIGHT, height - TEXT_SPACE);

		int greenX1 = width * greenStart / TICK_LENGTH;
		int greenX2 = width * greenEnd / TICK_LENGTH;

		g.setColor(backgroundColor);
		g.fillRect(0, 0, greenX1, barHeight);
		g.fillRect(greenX2, 0, width - greenX2, barHeight);

		g.setColor(targetZoneColor);
		g.fillRect(greenX1, 0, greenX2 - greenX1, barHeight);

		long ms = plugin.millisSinceTick();
		int swipeLineX = (int) (width * ms / (double) TICK_LENGTH);
		swipeLineX = Math.min(swipeLineX, width); // Ensure the swipe line does not go out of the bar

		g.setColor(swipeLineColor);
		g.drawLine(swipeLineX, 0, swipeLineX, barHeight);

		g.setColor(borderColor);
		g.drawRect(0, 0, width, barHeight);

		g.setColor(clickColor);
		int y1 = barHeight / 2 - X_SIZE;
		int y2 = barHeight / 2 + X_SIZE;
		for (int offset : clickOffsets)
		{
			int x = width * offset / TICK_LENGTH;
			g.drawLine(x - X_SIZE, y1, x + X_SIZE, y2);
			g.drawLine(x - X_SIZE, y2, x + X_SIZE, y1);
		}

		if (showCombo)
		{
			g.setColor(comboTextColor);
			String text = "Combo: " + plugin.getCombo();
			int tx = (width - g.getFontMetrics().stringWidth(text)) / 2;
			int ty = barHeight + g.getFontMetrics().getAscent() + 2;
			g.drawString(text, tx, ty);
		}

		return new Dimension(width, height);
	}
}
