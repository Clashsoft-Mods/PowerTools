package powertools.chunkprotection.common;

import java.util.EnumSet;

import powertools.chunkprotection.ChunkProtection;
import powertools.chunkprotection.lib.ChunkInfo;
import powertools.main.PowerTools;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

public class AutoUnclaimTickHandler implements IScheduledTickHandler
{
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (ChunkInfo cinfo : ChunkProtection.chunkInfo.values())
		{
			if (cinfo.isClaimed() && cinfo.getDaysSinceLastVisit() >= PowerTools.autoUnclaimDays)
			{
				cinfo.unclaim();
			}
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData)
	{
	}
	
	@Override
	public EnumSet<TickType> ticks()
	{
		return EnumSet.of(TickType.SERVER);
	}
	
	@Override
	public String getLabel()
	{
		return "ChunkProtectionTicks2";
	}
	
	@Override
	public int nextTickSpacing()
	{
		return 6000; // 5 minutes
	}
}
