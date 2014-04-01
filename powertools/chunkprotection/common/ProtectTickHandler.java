package powertools.chunkprotection.common;

import java.util.EnumSet;

import powertools.chunkprotection.ChunkProtection;
import powertools.chunkprotection.lib.ChunkInfo;
import powertools.chunkprotection.status.ClaimStatus;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.ChunkPos;
import powertools.main.lib.PlayerInfo;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import net.minecraft.entity.player.EntityPlayerMP;

public class ProtectTickHandler implements IScheduledTickHandler
{
	public void checkPlayerChunk(EntityPlayerMP player)
	{
		String name = player.getCommandSenderName();
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		
		if (pinfo.borderInfo)
		{
			ChunkPos oldPos = pinfo.chunkPos;
			ChunkPos newPos = new ChunkPos(player);
			pinfo.chunkPos = newPos;
			int oldDim = oldPos.dim;
			int oldX = oldPos.x;
			int oldZ = oldPos.z;
			int newDim = newPos.dim;
			int newX = newPos.x;
			int newZ = newPos.z;
			
			ChunkInfo info = ChunkProtection.getChunkInfo(newDim, newX, newZ);
			
			if (oldDim != newDim || oldX != newX || oldZ != newZ)
			{
				ClaimStatus c = info.compare(newDim, newX, newZ, name);
				if (c == ClaimStatus.NOT_CLAIMED)
				{
					player.addChatMessage(Cc.Yellow + "Unclaimed territory");
				}
				else if (c == ClaimStatus.CLAIMED_BY_OTHER_PLAYER)
				{
					player.addChatMessage(Cc.Yellow + "This land is owned by '" + info.getOwner() + "'");
				}
				else if (c == ClaimStatus.CLAIMED)
				{
					player.addChatMessage(Cc.Yellow + "You own this land");
					info.setLastVisitNow();
				}
				else if (c == ClaimStatus.ALLOWED)
				{
					player.addChatMessage(Cc.Yellow + "You are allowed by '" + info.getOwner() + "' to build here.");
				}
			}
		}
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData)
	{
		int numberOfPlayers = PowerTools.server.getAllUsernames().length;
		EntityPlayerMP player;
		for (int i = 0; i < numberOfPlayers; i++)
		{
			player = ((EntityPlayerMP) PowerTools.server.getConfigurationManager().playerEntityList.get(i));
			this.checkPlayerChunk(player);
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
		return "ChunkProtectionTicks";
	}
	
	@Override
	public int nextTickSpacing()
	{
		return 10;
	}
	
}
