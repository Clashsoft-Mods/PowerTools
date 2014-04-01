package powertools.login.command;

import powertools.login.PowerLogin;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.PlayerInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

public class CommandRegister extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "register";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/register <password>";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			if (PowerTools.checkForValidCharacters(args[0]))
			{
				String name = sender.getCommandSenderName();
				PlayerInfo info = PowerTools.getPlayerInfo(name);
				
				if (!info.isRegistered()) // Player not registered yet, so add new player
				{
					PowerLogin.registerPlayer(name, args[0]);
					chat(sender, Cc.Yellow + "Player '" + name + "' registered");
				}
				else
				// Try to change password
				{
					if (info.isLoggedIn) // Player is logged in, so change password
					{
						info.password = args[0];
						chat(sender, Cc.Yellow + "Player '" + name + "' password changed");
					}
					else
					// Player not logged in, so give message
					{
						chat(sender, Cc.Yellow + "Player '" + name + "' is already registered. Use " + Cc.White + "/login <password>");
						chat(sender, Cc.Yellow + "When logged in, change your password by using " + Cc.White + "/register <new password>");
					}
				}
			}
			else
			// found invalid character
			{
				chat(sender, "Only letters, numbers and '_' are allowed.");
			}
		}
		else
		// to few/many arguments
		{
			chat(sender, "Usage: /register <new password>");
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
	
	public static void chat(ICommandSender sender, String message)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
	}
}
