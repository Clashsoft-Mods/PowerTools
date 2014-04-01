package powertools.main.common;

import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.ChatMode;
import powertools.main.lib.PlayerInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;

public class PowerEvents
{
	@ForgeSubscribe
	public void chatEvent(ServerChatEvent event)
	{
		String playerName = event.username;
		PlayerInfo info = PowerTools.getPlayerInfo(playerName);
		ChatMode chatMode = info.chatMode;
		
		if (chatMode == ChatMode.PLAYER) // Chat to selected players only
		{
			event.setCanceled(true);
			String message = Cc.LightBlue + "<" + playerName + ">" + Cc.White + " " + event.message;
			event.player.addChatMessage(message);
			
			for (String s : info.chatToPlayers)
			{
				PowerTools.sendChatToPlayerName(s, message);
			}
		}
		else if (chatMode == ChatMode.WHISPER)
		{
			event.setCanceled(true);
			String message = Cc.Gray + "<" + playerName + ">" + Cc.White + " " + event.message;
			event.player.addChatMessage(message);
			
			for (Object o : event.player.worldObj.playerEntities)
			{
				EntityPlayer player = (EntityPlayer) o;
				double distance = Math.sqrt(player.getDistanceSqToEntity(event.player));
				if (distance <= PowerTools.whisperDistance)
				{
					player.addChatMessage(message);
				}
			}
		}
	}
}