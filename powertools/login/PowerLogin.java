package powertools.login;

import powertools.login.command.CommandLogin;
import powertools.login.command.CommandLogout;
import powertools.login.command.CommandRegister;
import powertools.login.common.LoginEvents;
import powertools.login.common.LoginTickHandler;
import powertools.login.status.LoginStatus;
import powertools.main.PowerTools;
import powertools.main.lib.PlayerInfo;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraftforge.common.MinecraftForge;

@Mod(modid = PowerLogin.MODID, name = PowerLogin.NAME, version = PowerLogin.VERSION, dependencies = PowerLogin.DEPENDENCIES)
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class PowerLogin
{
	public static final String	MODID			= "powerlogin";
	public static final String	NAME			= "Power Login";
	public static final String	VERSION			= "1.6.4-1.0.0";
	public static final String	DEPENDENCIES	= "required-after:" + PowerTools.MODID;
	
	@Instance(MODID)
	public static PowerLogin	instance;
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
		{
			MinecraftForge.EVENT_BUS.register(new LoginEvents());
			TickRegistry.registerScheduledTickHandler(new LoginTickHandler(), Side.SERVER);
			
			PowerTools.powerLoginLoaded = true;
			
			PowerTools.commandManager.registerCommand(new CommandLogin());
			PowerTools.commandManager.registerCommand(new CommandLogout());
			PowerTools.commandManager.registerCommand(new CommandRegister());
		}
	}
	
	public static void registerPlayer(String player, String password)
	{
		PlayerInfo info = PowerTools.getPlayerInfo(player);
		info.password = password;
		info.isLoggedIn = true;
	}
	
	public static void setLoginPos(String player, double x, double y, double z)
	{
		PlayerInfo info = PowerTools.getPlayerInfo(player);
		info.loginPosX = x;
		info.loginPosY = y;
		info.loginPosZ = z;
	}
	
	public static String getPassword(String player)
	{
		return PowerTools.getPlayerInfo(player).password;
	}
	
	public static LoginStatus login(String player, String password)
	{
		PlayerInfo info = PowerTools.getPlayerInfo(player);
		
		if (info.isLoggedIn)
		{
			return LoginStatus.ALREADY_LOGGED_IN;
		}
		
		String checkPassword = info.password;
		if (checkPassword == null)
		{
			return LoginStatus.NOT_REGISTERED;
		}
		else if (checkPassword.equals(password))
		{
			info.isLoggedIn = true;
			return LoginStatus.LOGGED_IN;
		}
		return LoginStatus.WRONG_PASSWORD;
	}
	
	public static void logout(String player)
	{
		PowerTools.getPlayerInfo(player).isLoggedIn = false;
	}
}
