package powertools.login.command;

import powertools.login.PowerLogin;
import powertools.login.status.LoginStatus;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;

public class CommandLogin extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "login";
	}
	
	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "/login <password>";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			String n = sender.getCommandSenderName();
			LoginStatus status = PowerLogin.login(n, args[0]);
			if (status == LoginStatus.LOGGED_IN)
			{
				PowerTools.chat(sender, Cc.LightGreen + "Successfully logged in!");
			}
			else if (status == LoginStatus.ALREADY_LOGGED_IN)
			{
				PowerTools.chat(sender, Cc.Yellow + "You are already logged in.");
			}
			else if (status == LoginStatus.NOT_REGISTERED)
			{
				PowerTools.chat(sender, Cc.Yellow + "You are not registered. Register using " + Cc.White + "/register <password>");
			}
			else if (status == LoginStatus.WRONG_PASSWORD)
			{
				PowerTools.chat(sender, Cc.LightRed + "Failed to login: Invalid password!");
			}
		}
		else
		{
			sender.sendChatToPlayer(ChatMessageComponent.createFromText(PowerTools.Usage + "/login <password>"));
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
}