package com.playmyskay.voxel.render.shaders;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class VoxelTextureAttribute extends Attribute {
	public final static String VoxelTextureAttributeAlias = "voxelTexture";
	public final static long VoxelTextureAttribute = register(VoxelTextureAttributeAlias);
	protected static long Mask = VoxelTextureAttribute;

	public byte tileOffsetX;
	public byte tileOffsetY;
	public byte faceWidth;
	public byte faceHeight;

	/**
	 * Method to check whether the specified type is a valid
	 * VoxelTextureAttribute type
	 */
	public static Boolean is (final long type) {
		return (type & Mask) != 0;
	}

	public VoxelTextureAttribute(final long type) {
		super(type);
		if (!is(type)) throw new GdxRuntimeException("Invalid type specified");
	}

	public VoxelTextureAttribute(final long type, final byte tileOffsetX, final byte tileOffsetY, final byte faceWidth,
			final byte faceHeight) {
		this(type);
		this.tileOffsetX = tileOffsetX;
		this.tileOffsetY = tileOffsetY;
		this.faceWidth = faceWidth;
		this.faceHeight = faceHeight;
	}

	/** copy constructor */
	public VoxelTextureAttribute(VoxelTextureAttribute other) {
		this(other.type, other.tileOffsetX, other.tileOffsetY, other.faceWidth, other.faceHeight);
	}

	@Override
	public Attribute copy () {
		return new VoxelTextureAttribute(this);
	}

	@Override
	public int hashCode () {
		final int prime = 6133;
		final long v = this.tileOffsetX + this.tileOffsetY + this.faceWidth + this.faceHeight;
		return prime * super.hashCode() + (int) (v ^ (v >>> 32));
	}

	@Override
	public int compareTo (Attribute o) {
		if (type != o.type) return type < o.type ? -1 : 1;
		byte otherTileOffsetX = ((VoxelTextureAttribute) o).tileOffsetX;
		byte otherTileOffsetY = ((VoxelTextureAttribute) o).tileOffsetY;
		byte otherFaceWidht = ((VoxelTextureAttribute) o).faceWidth;
		byte otherFaceHeight = ((VoxelTextureAttribute) o).faceHeight;
		if (tileOffsetX != otherTileOffsetX) return tileOffsetX < otherTileOffsetX ? -1 : 1;
		if (tileOffsetY != otherTileOffsetY) return tileOffsetY < otherTileOffsetY ? -1 : 1;
		if (faceWidth != otherFaceWidht) return faceWidth < otherFaceWidht ? -1 : 1;
		if (faceHeight != otherFaceHeight) return faceHeight < otherFaceHeight ? -1 : 1;
		return 0;
	}

}
