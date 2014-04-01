package powertools.login.common;

import java.util.EnumSet;

import powertools.main.PowerTools;
import powertools.main.lib.PlayerInfo;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.entity.player.EntityPlayerMP;

public class LoginTickHandler implements IScheduledTickHandler
{
	public void checkPlayerLogin(EntityPlayerMP player)
	{
		String name = player.getCommandSenderName();
		if (!PowerTools.getPlayerInfo(name).isLoggedIn)
		{
			PlayerInfo info = PowerTools.getPlayerInfo(name);
			double x = info.loginPosX;
			double y = info.loginPosY;
			double z = info.loginPosZ;
			player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
		}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		for (Object o : PowerTools.server.getConfigurationManager().playerEntityList)
		{
			this.checkPlayerLogin((EntityPlayerMP) o);
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
		return "PowerLoginTicks";
	}
	
	@Override
	public int nextTickSpacing()
	{
		return 10;
	}
}
