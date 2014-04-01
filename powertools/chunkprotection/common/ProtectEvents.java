package powertools.chunkprotection.common;

import powertools.chunkprotection.ChunkProtection;
import powertools.chunkprotection.lib.ChunkInfo;
import powertools.chunkprotection.status.ClaimStatus;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.PlayerInfo;

import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class ProtectEvents
{
	@ForgeSubscribe
	public void playerInteractEvent(PlayerInteractEvent event)
	{
		String name = event.entityPlayer.getCommandSenderName();
		int dim = event.entityPlayer.dimension;
		int x = PowerTools.toChunkCoordinate(event.x);
		int z = PowerTools.toChunkCoordinate(event.z);
		
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		ChunkInfo cinfo = ChunkProtection.getChunkInfo(dim, x, z);
		ClaimStatus compare = cinfo.compare(dim, x, z, name);
		
		if (!name.isEmpty() && compare == ClaimStatus.CLAIMED_BY_OTHER_PLAYER && !ChunkProtection.isPlayerOp(name))
		{
			if (event.isCancelable())
			{
				event.setCanceled(true);
			}
			
			long time2 = System.currentTimeMillis();
			if (time2 - pinfo.time > 2000)
			{
				event.entityPlayer.addChatMessage(Cc.Yellow + "This land is owned by '" + cinfo.getOwner() + "'.");
				event.entityPlayer.addChatMessage(Cc.Yellow + "You are not allowed to build or break here.");
				pinfo.time = time2;
			}
		}
	}
}
