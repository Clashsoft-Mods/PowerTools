package powertools.chunkprotection.command;

import java.util.Arrays;
import java.util.List;

import powertools.chunkprotection.ChunkProtection;
import powertools.chunkprotection.lib.ChunkInfo;
import powertools.chunkprotection.status.ClaimStatus;
import powertools.chunkprotection.status.UnclaimStatus;
import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.lib.ChunkPos;
import powertools.main.lib.PlayerInfo;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandChunk extends CommandBase
{
	@Override
	public String getCommandName()
	{
		return "chunk";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/chunk [claim|unclaim|players|info|borderinfo|playerinfo|maxchunks]";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length > 0)
		{
			String name = args.length > 1 ? args[1] : sender.getCommandSenderName();
			
			if (args[0].equals("claim"))
				this.claim(sender, name);
			else if (args[0].equals("unclaim"))
				this.unclaim(sender, name);
			else if (args[0].equals("unclaimall"))
				this.unclaimAll(sender, name);
			else if (args[0].equals("players"))
			{
				if (args[1].equals("add"))
					this.addPlayer(sender, Arrays.copyOfRange(args, 2, args.length));
				else if (args[1].equals("remove"))
					this.removePlayer(sender, Arrays.copyOfRange(args, 2, args.length));
				else if (args[1].equals("clear"))
					this.clearPlayers(sender);
			}
			else if (args[0].equals("info"))
				this.info(sender);
			else if (args[0].equals("borderinfo"))
				this.borderInfo(sender, args);
			else if (args[0].equals("playerinfo"))
				this.playerInfo(sender, name);
			else if (args[0].equals("maxchunks"))
				this.setMaxChunks(sender, args);
		}
		else
		{
			chat(sender, Cc.Brown + "----- Chunk Commands: -----");
			if (canClaim(sender))
			{
				chat(sender, Cc.Yellow + "Claim this chunk: " + Cc.White + "/chunk claim");
			}
			chat(sender, Cc.Yellow + "Unclaim this chunk: " + Cc.White + "/chunk unclaim");
			chat(sender, Cc.Yellow + "Unclaim all your chunks: " + Cc.White + "/chunk unclaim all");
			chat(sender, Cc.Yellow + "Add or remove supporters: " + Cc.White + "/chunk players [add|remove] [player1] [player2] ...");
			if (isOp(sender))
			{
				chat(sender, Cc.Brown + "  Operator Commands:");
				chat(sender, Cc.Yellow + "Claim this chunk for player: " + Cc.White + "/chunk claim [player]");
				chat(sender, Cc.Yellow + "Unclaim this chunk for player: " + Cc.White + "/chunk unclaim [player]");
				chat(sender, Cc.Yellow + "Unclaim all chunks for player: " + Cc.White + "/chunk unclaim all [player]");
				chat(sender, Cc.Yellow + "Display player information: " + Cc.White + "/chunk playerinfo [playername]");
				chat(sender, Cc.Yellow + "Set maximum chunks to claim for player: " + Cc.White + "/chunk maxchunks [playername] [maxchunks]");
			}
			chat(sender, Cc.Yellow + "Display info at border: " + Cc.White + "/chunk borderinfo [on|off]");
			chat(sender, Cc.Brown + "---------------------------");
		}
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender)
	{
		return true;
	}
	
	public static boolean isOp(ICommandSender sender)
	{
		return ChunkProtection.isPlayerOp(sender.getCommandSenderName());
	}
	
	public static boolean canClaim(ICommandSender sender)
	{
		return PowerTools.allPlayersCanClaim || isOp(sender);
	}
	
	public void claim(ICommandSender sender, String name)
	{
		EntityPlayer player = (EntityPlayer) sender;
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo info = ChunkProtection.getChunkInfo(chunkPos);
		
		StringBuilder message = new StringBuilder(Cc.Yellow);
		message.append("Dim: ").append(chunkPos.dim);
		message.append(" x: ").append(chunkPos.x);
		message.append(" z: ").append(chunkPos.z);
		
		ClaimStatus b = ChunkProtection.claimChunk(chunkPos, name);
		
		if (b == ClaimStatus.CLAIMED)
		{
			message.append(" was successfully claimed.");
		}
		else if (b == ClaimStatus.CLAIMED_BY_OTHER_PLAYER || b == ClaimStatus.ALLOWED)
		{
			message.append(" was already claimed by ").append(info.getOwner()).append('.');
		}
		else if (b == ClaimStatus.ALREADY_CLAIMED)
		{
			message.append(" was already claimed by you.");
		}
		else if (b == ClaimStatus.FULL)
		{
			message.append(" can't be claimed by you. You may only claim ").append(pinfo.maxChunks).append(" chunks.");
		}
		
		chat(sender, message.toString());
	}
	
	public void unclaim(ICommandSender sender, String name)
	{
		EntityPlayer player = (EntityPlayer) sender;
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo info = ChunkProtection.getChunkInfo(chunkPos);
		
		StringBuilder message = new StringBuilder(Cc.Yellow);
		message.append("Dim: ").append(chunkPos.dim);
		message.append(" x: ").append(chunkPos.x);
		message.append(" z: ").append(chunkPos.z);
		
		UnclaimStatus b = ChunkProtection.unclaimChunk(chunkPos, name);
		
		if (b == UnclaimStatus.UNCLAIMED)
		{
			message.append(" was successfully unclaimed.");
		}
		if (b == UnclaimStatus.NOT_CLAIMED)
		{
			message.append(" couln't be unclaimed. It wasn't claimed.");
		}
		if (b == UnclaimStatus.CLAIMED_BY_OTHER_PLAYER)
		{
			message.append(" couln't be unclaimed. It was claimed by ").append(info.getOwner()).append('.');
		}
		if (b == UnclaimStatus.UNCLAIMED_BY_OP)
		{
			message.append(" was successfully unclaimed by op.");
		}
		
		chat(sender, message.toString());
	}
	
	public void unclaimAll(ICommandSender sender, String name)
	{
		int i = ChunkProtection.unclaimAllChunks(name);
		
		chat(sender, Cc.Yellow + "Unclaimed " + i + " chunks.");
	}
	
	public void addPlayer(ICommandSender sender, String[] args)
	{
		String name = sender.getCommandSenderName();
		
		EntityPlayer player = (EntityPlayer) sender;
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo cinfo = ChunkProtection.getChunkInfo(chunkPos);
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		
		if (cinfo.isOwner(pinfo) || isOp(sender))
		{
			StringBuilder message = new StringBuilder();
			
			if (isOp(sender))
			{
				message.append(Cc.LightRed).append("Op ");
			}
			message.append(Cc.Yellow).append("Added player(s): ").append(joinNiceString(args));
			
			for (int i = 0; i < args.length; i++)
			{
				ChunkProtection.addAllowedPlayer(chunkPos, name, args[i]);
			}
			
			chat(sender, message.toString());
		}
		else
		{
			chat(sender, Cc.Yellow + "You can only add players to a chunk you own.");
		}
	}
	
	public void removePlayer(ICommandSender sender, String[] args)
	{
		String name = sender.getCommandSenderName();
		
		EntityPlayer player = (EntityPlayer) sender;
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo cinfo = ChunkProtection.getChunkInfo(chunkPos);
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		
		if (cinfo.isOwner(pinfo) || isOp(sender))
		{
			StringBuilder message = new StringBuilder();
			
			if (isOp(sender))
			{
				message.append(Cc.LightRed).append("Op ");
			}
			message.append(Cc.Yellow).append("Removed player(s): ").append(joinNiceString(args));
			
			for (int i = 0; i < args.length; i++)
			{
				ChunkProtection.removeAllowedPlayer(chunkPos, name, args[i]);
			}
			
			chat(sender, message.toString());
		}
		else
		{
			chat(sender, Cc.Yellow + "You can only remove players from a chunk you own.");
		}
	}
	
	public void clearPlayers(ICommandSender sender)
	{
		String name = sender.getCommandSenderName();
		
		EntityPlayer player = (EntityPlayer) sender;
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo cinfo = ChunkProtection.getChunkInfo(chunkPos);
		PlayerInfo pinfo = PowerTools.getPlayerInfo(name);
		
		if (cinfo.isOwner(pinfo) || isOp(sender))
		{
			if (ChunkProtection.clearAllowedPlayers(chunkPos, name))
			{
				StringBuilder message = new StringBuilder();
				if (isOp(sender))
				{
					message.append(Cc.LightRed).append("Op ");
				}
				message.append(Cc.Yellow).append("Cleared player list for this chunk.");
				chat(sender, message.toString());
			}
		}
		else
		{
			chat(sender, Cc.Yellow + "You can only remove players from a chunk you own.");
		}
	}
	
	public void borderInfo(ICommandSender sender, String[] args)
	{
		String player = sender.getCommandSenderName();
		StringBuilder message;
		PlayerInfo info = PowerTools.getPlayerInfo(player);
		
		if (args.length > 1)
		{
			if (args[1].equals("on"))
			{
				info.borderInfo = true;
				chat(sender, "Border info set to ON");
			}
			else if (args[1].equals("off"))
			{
				info.borderInfo = false;
				chat(sender, "Border info set to OFF");
			}
			else
			{
				chat(sender, PowerTools.Usage + "/chunk borderinfo <on|off>");
			}
		}
		else
		{
			message = new StringBuilder(Cc.Yellow).append("Border info is currently set: ").append(info.borderInfo ? "ON" : "OFF");
			chat(sender, message.toString());
		}
	}
	
	public void playerInfo(ICommandSender sender, String name)
	{
		PlayerInfo info = PowerTools.getPlayerInfo(name);
		StringBuilder message = new StringBuilder(Cc.Yellow);
		
		message.append("Info for player '").append(name);
		message.append("': Maximum Chunks Allowed: ").append(info.maxChunks);
		message.append(", Chunks Owned: ").append(info.chunksOwned);
		chat(sender, message.toString());
	}
	
	public void info(ICommandSender sender)
	{
		EntityPlayerMP player = (EntityPlayerMP) sender;
		ChunkPos chunkPos = new ChunkPos(player);
		ChunkInfo info = ChunkProtection.getChunkInfo(chunkPos);
		
		if (!info.isClaimed())
		{
			chat(sender, Cc.Yellow + "This chunk is claimed by: " + info.getOwner());
			if (!info.getPlayers().isEmpty())
			{
				chat(sender, Cc.Yellow + "Players allowed to build here: " + joinNiceString(info.getPlayers().toArray()));
			}
		}
		else
		{
			chat(sender, Cc.Yellow + "This chunk is currently not claimed");
		}
	}
	
	public void setMaxChunks(ICommandSender sender, String[] args)
	{
		if (!isOp(sender))
		{
			chat(sender, Cc.Yellow + "You need to be an OP to use this command.");
			return;
		}
		
		if (args.length >= 2)
		{
			String player;
			int maxChunks;
			
			if (args.length == 3)
			{
				player = args[1];
				maxChunks = parseIntWithMin(sender, args[2], 0);
			}
			else
			{
				player = sender.getCommandSenderName();
				maxChunks = parseIntWithMin(sender, args[1], 0);
			}
			
			StringBuilder message = new StringBuilder(Cc.Yellow);
			PlayerInfo pinfo = PowerTools.getPlayerInfo(player);
			pinfo.maxChunks = maxChunks;
			
			message.append("Player '").append(player);
			message.append("' can now claim ").append(Cc.White).append(maxChunks);
			message.append(Cc.Yellow).append(" chunks.");
			chat(sender, message.toString());
		}
		else
		{
			chat(sender, PowerTools.Usage + "/chunk maxchunks [playername] [maxchunks]");
		}
	}
	
	public static void chat(ICommandSender sender, String message)
	{
		PowerTools.chat(sender, message);
	}
	
	@Override
	public boolean isUsernameIndex(String[] args, int index)
	{
		if (args.length >= 1)
		{
			if ("claim".equals(args[0]) || "unclaim".equals(args[0]) || "unclaimall".equals(args[0]) || "maxchunks".equals(args[0]) || "playerinfo".equals(args[0]))
			{
				return index == 1;
			}
			else if ("players".equals(args[0]))
			{
				return index >= 2;
			}
		}
		return false;
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "claim", "unclaim", "players", "info", "borderinfo", "playerinfo", "maxchunks");
		}
		else if (args.length == 2)
		{
			if ("claim".equals(args[0]) || "unclaim".equals(args[0]) || "unclaimall".equals(args[0]) || "maxchunks".equals(args[0]) || "playerinfo".equals(args[0]))
			{
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
			}
			else if ("players".equals(args[0]))
			{
				return getListOfStringsMatchingLastWord(args, "add", "remove", "clear");
			}
		}
		else if (args.length == 3)
		{
			if ("players".equals(args[0]))
			{
				return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
			}
		}
		return null;
	}
}
