package powertools.login.command;

import powertools.login.PowerLogin;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatMessageComponent;

public class CommandLogout extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "logout";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/logout";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] arguments)
	{
		String playerName = sender.getCommandSenderName();
		EntityPlayer player = (EntityPlayer) sender;
		PowerLogin.setLoginPos(playerName, (int) player.posX, (int) player.posY, (int) player.posZ);
		PowerLogin.logout(playerName);
		sender.sendChatToPlayer(ChatMessageComponent.createFromText("Player '" + playerName + "' logged out"));
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
}