package powertools.main.common;

import java.util.EnumSet;

import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.maplimit.IMapLimit;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

public class MapLimitTickHandler implements IScheduledTickHandler
{
	public void checkMapLimit(EntityPlayerMP player)
	{
		int dimension = player.dimension;
		IMapLimit limit = PowerTools.getMapLimit(dimension);
		
		if (limit != null)
		{
			Vec3 pos = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
			
			String message = limit.update(pos);
			
			if (message != null)
			{
				boolean blockFeet = false;
				boolean blockEyes = false;
				player.setPositionAndUpdate(pos.xCoord, pos.yCoord, pos.zCoord);
				player.addChatMessage(Cc.LightRed + "You've reached the limit of this map. " + message);
			}
		}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (Object o : PowerTools.server.getConfigurationManager().playerEntityList)
		{
			this.checkMapLimit((EntityPlayerMP) o);
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
		return "MapLimitTicks";
	}
	
	@Override
	public int nextTickSpacing()
	{
		return 20;
	}
}
