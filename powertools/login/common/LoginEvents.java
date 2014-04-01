package powertools.login.common;

import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.PlayerInfo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class LoginEvents
{
	@ForgeSubscribe
	public void playerEvent(PlayerEvent event)
	{
		String name = event.entityPlayer.getCommandSenderName();
		
		if (!PowerTools.getPlayerInfo(name).isLoggedIn && event.isCancelable())
		{
			event.setCanceled(true);
			notify(event.entityPlayer);
		}
	}
	
	@ForgeSubscribe
	public void commandEvent(CommandEvent event)
	{
		if (event.sender instanceof EntityPlayer)
		{
			String name = event.sender.getCommandSenderName();
			if (!PowerTools.getPlayerInfo(name).isLoggedIn)
			{
				String command = event.command.getCommandName();
				if (!command.equals("login") && !command.equals("register") && !command.equals("help"))
				{
					event.setCanceled(true);
					
					notify((EntityPlayer) event.sender);
				}
			}
		}
	}
	
	@ForgeSubscribe
	public void itemEvent(ItemTossEvent event)
	{
		String name = event.player.getEntityName();
		if (!PowerTools.getPlayerInfo(name).isLoggedIn)
		{
			ItemStack stack = event.entityItem.getEntityItem();
			event.player.inventory.addItemStackToInventory(stack);
			event.setCanceled(true);
			
			notify(event.player);
		}
	}
	
	public static void notify(EntityPlayer player)
	{
		PlayerInfo info = PowerTools.getPlayerInfo(player.getCommandSenderName());
		long time2 = System.currentTimeMillis();
		if (time2 - info.time > 2000)
		{
			player.addChatMessage(Cc.Yellow + "You need to be registerd and logged in to play on this server.");
			player.addChatMessage(Cc.Yellow + "Use " + Cc.White + "/register [new password]" + Cc.Yellow + " or " + Cc.White + "/login [password]");
			info.time = time2;
		}
	}
}
