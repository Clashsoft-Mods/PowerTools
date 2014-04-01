package powertools.main.command;

import java.util.List;

import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.ChatMode;
import powertools.main.lib.PlayerInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandChat extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "chat";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/chat <arguments>";
	}
	
	@Override
	public List getCommandAliases()
	{
		return null;
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		String playerName = sender.getCommandSenderName();
		PlayerInfo info = PowerTools.getPlayerInfo(playerName);
		ChatMode chatMode = info.chatMode;
		if (args.length > 0) // handle arguments: change chat mode or edit list
		{
			if (args[0].equals("normal"))
			{
				info.chatMode = ChatMode.NORMAL;
				chat(sender, Cc.Yellow + "Chat mode changed to 'normal'");
			}
			if (args[0].equals("player"))
			{
				info.chatMode = ChatMode.PLAYER;
				for (int i = 1; i < args.length; i++)
				{
					info.chatToPlayers.add(args[i]);
				}
				chat(sender, Cc.Yellow + "Chat mode changed to 'player'");
			}
			if (args[0].equals("whisper"))
			{
				info.chatMode = ChatMode.WHISPER;
				chat(sender, Cc.Yellow + "Chat mode changed to 'whisper'");
			}
			if (args[0].equals("clear"))
			{
				info.chatToPlayers.clear();
				chat(sender, Cc.Yellow + "Cleared player list for chat");
			}
			if (args[0].equals("remove"))
			{
				if (args.length > 1)
				{
					for (int i = 1; i < args.length; i++)
					{
						info.chatToPlayers.remove(args[i]);
					}
					chat(sender, Cc.Yellow + "Players removed from chat list");
				}
				else
				{
					chat(sender, PowerTools.Usage + "/chat remove [player1] [player2] ...");
				}
			}
		}
		else
		// display current chat mode
		{
			StringBuilder message = new StringBuilder(Cc.Yellow).append("Current chat mode: ");
			if (chatMode == ChatMode.NORMAL)
			{
				message.append("normal, default chat");
			}
			if (chatMode == ChatMode.PLAYER)
			{
				message.append("player, to selected players only: ").append(Cc.LightBlue).append(info.chatToPlayers);
			}
			if (chatMode == ChatMode.WHISPER)
			{
				message.append("whisper, only to players within range");
			}
			chat(sender, message.toString());
			
			chat(sender, Cc.Yellow + "Use " + Cc.White + "/chat normal");
			chat(sender, Cc.Yellow + "or " + Cc.White + "/chat whisper");
			chat(sender, Cc.Yellow + "or " + Cc.White + "/chat player [player1] [player2] ...");
			if (chatMode == ChatMode.PLAYER)
			{
				chat(sender, Cc.Yellow + "Clear all players from list: " + Cc.White + "/chat clear");
				chat(sender, Cc.Yellow + "Remove player from list: " + Cc.White + "/chat remove [player1] [player2] ...");
			}
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
	
	public static void chat(ICommandSender sender, String message)
	{
		PowerTools.chat(sender, message);
	}
}