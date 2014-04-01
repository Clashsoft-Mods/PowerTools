package powertools.main.maplimit;

import net.minecraft.util.Vec3;

public class CylinderMapLimit implements IMapLimit
{
	public int centerX;
	public int centerZ;
	public int radius;
	
	public CylinderMapLimit(int centerX, int centerZ, int radius)
	{
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.radius = radius;
	}

	@Override
	public String update(Vec3 pos)
	{
		double x = pos.xCoord - centerX;
		double z = pos.zCoord - centerZ;
		double distance = Math.sqrt(x * x + z * z);
		if (distance > radius)
		{
			double d1 = radius / distance;
			pos.xCoord *= d1;
			pos.zCoord *= d1;
			
			return "Distance to center > " + radius;
		}
		return null;
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Cylinder at [");
		builder.append(this.centerX).append("; ").append(this.centerZ);
		builder.append("] with radius ").append(this.radius).append("]");
		return builder.toString();
	}
}
