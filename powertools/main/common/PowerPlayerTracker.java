package powertools.main.common;

import powertools.login.PowerLogin;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.PlayerInfo;
import cpw.mods.fml.common.IPlayerTracker;

import net.minecraft.entity.player.EntityPlayer;

public class PowerPlayerTracker implements IPlayerTracker
{
	@Override
	public void onPlayerLogin(EntityPlayer player)
	{
		String playerName = player.getCommandSenderName();
		PlayerInfo info = PowerTools.getPlayerInfo(playerName);
		info.isConnected = true;
		
		if (PowerTools.powerLoginLoaded)
		{
			PowerLogin.setLoginPos(playerName, player.posX, player.posY, player.posZ);
			if (info.isRegistered())
			{
				player.addChatMessage(Cc.Yellow + "Welcome back, " + playerName + ". Use " + Cc.White + "/login <password>");
			}
			else
			{
				player.addChatMessage(Cc.Yellow + "You need to be registerd and logged in to play on this server.");
				player.addChatMessage(Cc.Yellow + "Use " + Cc.White + "/register <new password>" + Cc.Yellow + " or " + Cc.White + "/login <password>");
			}
		}
	}
	
	@Override
	public void onPlayerLogout(EntityPlayer player)
	{
		if (PowerTools.powerLoginLoaded)
		{
			String playerName = player.getCommandSenderName();
			PowerTools.getPlayerInfo(playerName).isConnected = false;
			PowerLogin.logout(playerName);
		}
	}
	
	@Override
	public void onPlayerChangedDimension(EntityPlayer player)
	{
		
	}
	
	@Override
	public void onPlayerRespawn(EntityPlayer player)
	{
		
	}
}
