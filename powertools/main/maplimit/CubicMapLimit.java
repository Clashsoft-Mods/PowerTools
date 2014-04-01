package powertools.main.maplimit;

import net.minecraft.util.Vec3;


public class CubicMapLimit implements IMapLimit
{
	public int		minX, minZ, maxX, maxZ;
	
	public CubicMapLimit(int minX, int minZ, int maxX, int maxZ)
	{
		this.minX = minX;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxZ = maxZ;
	}
	
	@Override
	public String update(Vec3 pos)
	{
		if (pos.xCoord > maxX)
		{
			pos.xCoord = this.maxX;
			return "x > " + this.maxX;
		}
		else if (pos.xCoord < minX)
		{
			pos.xCoord = this.minX;
			return "x < " + this.minX;
		}
		else if (pos.zCoord > maxZ)
		{
			pos.zCoord = this.maxZ;
			return "z > " + this.maxZ;
		}
		else if (pos.zCoord < minZ)
		{
			pos.zCoord = this.minZ;
			return "z < " + this.minZ;
		}
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Cube from [");
		builder.append(this.minX).append("; ").append(this.minZ);
		builder.append("] to [");
		builder.append(this.maxX).append("; ").append(this.maxZ).append("]");
		return builder.toString();
	}
}
