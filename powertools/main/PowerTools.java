package powertools.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import powertools.main.command.CommandChat;
import powertools.main.command.CommandMapLimit;
import powertools.main.common.MapLimitTickHandler;
import powertools.main.common.PowerEvents;
import powertools.main.common.PowerPlayerTracker;
import powertools.main.lib.Cc;
import powertools.main.lib.PlayerInfo;
import powertools.main.maplimit.IMapLimit;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = PowerTools.MODID, name = PowerTools.NAME, version = PowerTools.VERSION)
@NetworkMod(clientSideRequired = false, serverSideRequired = false)
public class PowerTools
{
	public static final String					MODID					= "powertools";
	public static final String					NAME					= "Power Tools";
	public static final String					VERSION					= "1.6.4-1.0.0";
	
	public static final String					Usage					= Cc.Yellow + "Usage: " + Cc.White;
	
	public static MinecraftServer				server;
	public static ServerCommandManager			commandManager;
	public static File							file;
	public static Configuration					config;
	
	protected static Map<String, PlayerInfo>	playerInfo				= new HashMap();
	protected static Map<Integer, IMapLimit>	mapLimits				= new HashMap();
	
	public static double						whisperDistance			= 16D;
	
	public static boolean						powerLoginLoaded		= false;
	public static boolean						chunkProtectionLoaded	= false;
	
	@Instance(MODID)
	public static PowerTools					instance;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
		{
			config = new Configuration(event.getSuggestedConfigurationFile());
			
			whisperDistance = config.get("chat", "Whisper Distance", whisperDistance).getDouble(whisperDistance);
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		if (event.getSide() == Side.SERVER)
		{
			MinecraftForge.EVENT_BUS.register(new PowerEvents());
			TickRegistry.registerScheduledTickHandler(new MapLimitTickHandler(), Side.SERVER);
			GameRegistry.registerPlayerTracker(new PowerPlayerTracker());
			
			server = MinecraftServer.getServer();
			commandManager = (ServerCommandManager) server.getCommandManager();
			file = new File(getSaveDir(), "powertools.dat");
			
			this.loadPlayerInfo(file);
			
			commandManager.registerCommand(new CommandChat());
			commandManager.registerCommand(new CommandMapLimit());
		}
	}
	
	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event)
	{
		if (file != null)
		{
			this.savePlayerInfo(file);
		}
	}
	
	public static File getSaveDir()
	{
		File f = server.getFile("config");
		if (f.exists())
			f.mkdirs();
		return f;
	}
	
	public static void chat(ICommandSender sender, String message)
	{
		sender.sendChatToPlayer(ChatMessageComponent.createFromText(message));
	}
	
	public static void createFile(File file) throws IOException
	{
		if (!file.exists())
		{
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
	}
	
	public void loadPlayerInfo(File file)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.read(file);
			
			if (nbt != null)
			{
				NBTTagList players = nbt.getTagList("Players");
				for (int i = 0; i < players.tagCount(); i++)
				{
					NBTTagCompound playerNBT = (NBTTagCompound) players.tagAt(i);
					PlayerInfo info = new PlayerInfo();
					info.readFromNBT(playerNBT);
					playerInfo.put(info.name, info);
				}
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void savePlayerInfo(File file)
	{
		try
		{
			createFile(file);
			
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList players = new NBTTagList();
			
			for (String playerName : playerInfo.keySet())
			{
				NBTTagCompound playerNBT = new NBTTagCompound();
				playerInfo.get(playerName).writeToNBT(playerNBT);
				players.appendTag(playerNBT);
			}
			nbt.setTag("Players", players);
			CompressedStreamTools.write(nbt, file);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public static void setMapLimit(int dimension, IMapLimit cubicMapLimit)
	{
		mapLimits.put(Integer.valueOf(dimension), cubicMapLimit);
	}
	
	public static IMapLimit getMapLimit(int dimension)
	{
		return mapLimits.get(Integer.valueOf(dimension));
	}
	
	public static void setPlayerInfo(String player, PlayerInfo info)
	{
		info.name = player;
		playerInfo.put(player, info);
	}
	
	public static PlayerInfo getPlayerInfo(String player)
	{
		PlayerInfo info = playerInfo.get(player);
		if (info == null)
		{
			info = new PlayerInfo(player);
			playerInfo.put(player, info);
		}
		return info;
	}
	
	public static void sendChatToPlayerName(String playerName, String chatMessage)
	{
		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(playerName);
		if (player != null)
			player.addChatMessage(chatMessage);
	}
	
	public static int toChunkCoordinate(int coordinate)
	{
		return coordinate >> 4;
	}
	
	public static boolean checkForValidCharacters(String text)
	{
		for (int i = 0; i < text.length(); i++)
		{
			char c = text.charAt(i);
			if (c < 48)
				return false;
			if (c > 57 && c < 65)
				return false;
			if (c > 90 && c < 95)
				return false;
			if (c == 96)
				return false;
			if (c > 122)
				return false;
		}
		return true;
	}
}
