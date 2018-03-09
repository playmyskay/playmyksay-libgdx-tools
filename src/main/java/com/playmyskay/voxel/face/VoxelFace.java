package com.playmyskay.voxel.face;

public class VoxelFace {
	public enum Direction {
		top, bottom, front, back, left, right, none
	}

	public final static byte top_bit = 0x01;
	public final static byte bottom_bit = 0x02;
	public final static byte left_bit = 0x04;
	public final static byte right_bit = 0x08;
	public final static byte front_bit = 0x10;
	public final static byte back_bit = 0x20;
	public final static byte none_bit = 0x00;

	public static byte getDirectionBit (Direction direction) {
		switch (direction) {
		case top:
			return top_bit;
		case bottom:
			return bottom_bit;
		case left:
			return left_bit;
		case right:
			return right_bit;
		case front:
			return front_bit;
		case back:
			return back_bit;
		case none:
			return none_bit;
		}
		return 0x00;
	}

	public static Direction getDirection (byte directionBit) {
		switch (directionBit) {
		case top_bit:
			return Direction.top;
		case bottom_bit:
			return Direction.bottom;
		case left_bit:
			return Direction.left;
		case right_bit:
			return Direction.right;
		case front_bit:
			return Direction.front;
		case back_bit:
			return Direction.back;
		}
		return null;
	}

	public static byte addFace (byte faceBits, Direction direction) {
		faceBits |= VoxelFace.getDirectionBit(direction);
		return faceBits;
	}

	public static byte removeFace (byte faceBits, Direction direction) {
		faceBits &= ~VoxelFace.getDirectionBit(direction);
		return faceBits;
	}

	public static boolean hasFace (byte faceBits, Direction direction) {
		byte faceBit = VoxelFace.getDirectionBit(direction);
		return (faceBits & faceBit) == faceBit;
	}

	public static Direction getOpposite (Direction direction) {
		switch (direction) {
		case back:
			return Direction.front;
		case bottom:
			return Direction.top;
		case front:
			return Direction.back;
		case left:
			return Direction.right;
		case right:
			return Direction.left;
		case top:
			return Direction.bottom;
		default:
			break;
		}
		return null;
	}
}
