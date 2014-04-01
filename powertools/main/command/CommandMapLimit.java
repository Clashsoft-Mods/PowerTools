package powertools.main.command;

import java.util.Arrays;
import java.util.List;

import powertools.main.PowerTools;
import powertools.main.lib.Cc;
import powertools.main.maplimit.CubicMapLimit;
import powertools.main.maplimit.CylinderMapLimit;
import powertools.main.maplimit.IMapLimit;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class CommandMapLimit extends CommandBase
{
	
	@Override
	public String getCommandName()
	{
		return "maplimit";
	}
	
	@Override
	public String getCommandUsage(ICommandSender sender)
	{
		return "/maplimit [dimension] <shape> [args]";
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args)
	{
		if (args.length >= 1)
		{
			int dimension = sender.getEntityWorld().provider.dimensionId;
			IMapLimit mapLimit = null;
			boolean flag = false;
			
			if ("cube".equals(args[0]))
			{
				mapLimit = this.setCubeMapLimit(sender, dimension, Arrays.copyOfRange(args, 1, args.length));
			}
			else if ("cylinder".equals(args[0]))
			{
				mapLimit = this.setCylinderMapLimit(sender, dimension, Arrays.copyOfRange(args, 1, args.length));
			}
			else if ("none".equals(args[0]))
			{
				flag = true;
			}
			else
			{
				if ("overworld".equalsIgnoreCase(args[0]))
				{
					dimension = 0;
				}
				else if ("nether".equalsIgnoreCase(args[0]))
				{
					dimension = -1;
				}
				else if ("end".equalsIgnoreCase(args[0]))
				{
					dimension = 0;
				}
				else
				{
					dimension = parseInt(sender, args[0]);
				}
				
				if ("cube".equals(args[1]))
				{
					mapLimit = this.setCubeMapLimit(sender, dimension, Arrays.copyOfRange(args, 2, args.length));
				}
				else if ("cylinder".equals(args[1]))
				{
					mapLimit = this.setCylinderMapLimit(sender, dimension, Arrays.copyOfRange(args, 2, args.length));
				}
				else if ("none".equals(args[1]))
				{
					flag = true;
				}
				else
				{
					PowerTools.chat(sender, PowerTools.Usage + "/maplimit <dimension> <shape> [args]");
				}
			}
			
			if (flag)
			{
				PowerTools.setMapLimit(dimension, null);
				PowerTools.chat(sender, Cc.Yellow + "Removed map limit for dimension " + Cc.White + dimension);
				
			}
			else if (mapLimit != null)
			{
				PowerTools.setMapLimit(dimension, mapLimit);
				StringBuilder message = new StringBuilder(Cc.Yellow);
				message.append("Set map limit for dimension ").append(Cc.White).append(dimension).append(Cc.Yellow);
				message.append(" to ").append(Cc.White).append(mapLimit);
				PowerTools.chat(sender, message.toString());
			}
		}
		PowerTools.chat(sender, PowerTools.Usage + this.getCommandUsage(sender));
	}
	
	public IMapLimit setCubeMapLimit(ICommandSender sender, int dimension, String[] args)
	{
		int minX = 0;
		int minZ = 0;
		int maxX = 0;
		int maxZ = 0;
		
		if (args.length >= 4)
		{
			minX = parseIntBounded(sender, args[0], -30000000, 0);
			minZ = parseIntBounded(sender, args[1], -30000000, 0);
			maxX = parseIntBounded(sender, args[2], 0, 30000000);
			maxZ = parseIntBounded(sender, args[3], 0, 30000000);
		}
		else if (args.length >= 2)
		{
			int x = parseIntBounded(sender, args[0], 0, 60000000);
			int z = parseIntBounded(sender, args[1], 0, 60000000);
			
			maxX = x / 2;
			minX = -maxX;
			maxZ = z / 2;
			minZ = -maxZ;
		}
		else
		{
			PowerTools.chat(sender, PowerTools.Usage + "/maplimit [dimension] cube <width> <length>" + Cc.Yellow + " or " + Cc.White + "/maplimit [dimension] cube <minX> <minZ> <maxX> <maxZ>");
			return null;
		}
		
		return new CubicMapLimit(minX, minZ, maxX, maxZ);
	}
	
	public IMapLimit setCylinderMapLimit(ICommandSender sender, int dimension, String[] args)
	{
		int centerX = 0;
		int centerZ = 0;
		int radius = 0;
		
		if (args.length >= 3)
		{
			centerX = parseIntBounded(sender, args[0], -30000000, 30000000);
			centerZ = parseIntBounded(sender, args[1], -30000000, 30000000);
			radius = parseIntBounded(sender, args[2], 1, 30000000);
		}
		else if (args.length >= 1)
		{
			radius = parseIntBounded(sender, args[0], 1, 30000000);
		}
		else
		{
			PowerTools.chat(sender, PowerTools.Usage + "/maplimit [dimension] cylinder [centerX] [centerZ] <radius>");
		}
		
		return new CylinderMapLimit(centerX, centerZ, radius);
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			return getListOfStringsMatchingLastWord(args, "cube", "cylinder", "none", "overworld", "nether", "end");
		}
		else if (args.length == 2)
		{
			if (!"cube".equals(args[0]) && !"cylinder".equals(args[0]) && !"none".equals(args[0]))
			{
				return getListOfStringsMatchingLastWord(args, "cube", "cylinder", "none");
			}
		}
		return null;
	}
}
