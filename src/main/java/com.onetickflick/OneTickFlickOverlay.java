package com.onetickflick;

import javax.inject.Inject;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;

public class OneTickFlickOverlay extends Overlay
{
	private static final int MIN_BAR_HEIGHT = 12;
	private static final int TEXT_SPACE = 20;
	private static final int X_SIZE = 4;
	private static final int TICK_LENGTH = 600;
	private static final Dimension MIN_SIZE = new Dimension(70, MIN_BAR_HEIGHT + TEXT_SPACE);
	private static final Dimension DEFAULT_SIZE = new Dimension(150, MIN_BAR_HEIGHT + TEXT_SPACE);

	private final OneTickFlickPlugin plugin;
	private final OneTickFlickConfig config;
	private final List<Integer> clickOffsets = new CopyOnWriteArrayList<>();
	private volatile boolean visible = true;

	@Inject
	OneTickFlickOverlay(OneTickFlickPlugin plugin, OneTickFlickConfig config)
	{
		this.plugin = plugin;
		this.config = config;
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

		g.setColor(Color.RED);
		g.fillRect(0, 0, width, barHeight);

		int greenX1 = width * config.greenStart() / TICK_LENGTH;
		int greenX2 = width * config.greenEnd() / TICK_LENGTH;

		g.setColor(Color.GREEN);
		g.fillRect(greenX1, 0, greenX2 - greenX1, barHeight);

		long ms = plugin.millisSinceTick();
		int barX = (int) (width * ms / (double) TICK_LENGTH);

		g.setColor(Color.BLACK);
		g.drawLine(barX, 0, barX, barHeight);

		g.drawRect(0, 0, width, barHeight);

		int y1 = barHeight / 2 - X_SIZE;
		int y2 = barHeight / 2 + X_SIZE;
		for (int offset : clickOffsets)
		{
			int x = width * offset / TICK_LENGTH;
			g.drawLine(x - X_SIZE, y1, x + X_SIZE, y2);
			g.drawLine(x - X_SIZE, y2, x + X_SIZE, y1);
		}

		if (config.showCombo())
		{
			g.setColor(Color.WHITE);
			String text = "Combo: " + plugin.getCombo();
			int tx = (width - g.getFontMetrics().stringWidth(text)) / 2;
			int ty = barHeight + g.getFontMetrics().getAscent() + 2;
			g.drawString(text, tx, ty);
		}

		return new Dimension(width, height);
	}
}
