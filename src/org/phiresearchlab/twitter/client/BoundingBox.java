/**
 * 
 */
package org.phiresearchlab.twitter.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joel M. Rives
 * Aug 18, 2011
 */
public class BoundingBox {

	private int x;
	private int y;
	private int width;
	private int height;
	private List<BoundingBox> contained = new ArrayList<BoundingBox>();
	
	public BoundingBox() { }
	
	public BoundingBox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public boolean isWithin(int x, int y) {
		return (x >= this.x && x < this.x + width && y >= this.y && y < this.y + this.height);
	}
	
	public boolean overlaps(BoundingBox box) {
		if (isWithin(box.getX(), box.getY()) || 
			isWithin(box.getX() + box.getWidth(), box.getY()) ||
			isWithin(box.getX(), box.getY() + box.getHeight()) || 
			isWithin(box.getX() + box.getWidth(), box.getY() + box.getHeight()))
			return true;
		
		if (box.isWithin(x, y) || 
			box.isWithin(x + width, y) ||
			box.isWithin(x, y + height) || 
			box.isWithin(x + width, y + height))
			return true;
		
		return false;
	}
	
	public void placeNotOverlapping(BoundingBox box) {
		int originalX = box.getX();
		int originalY = box.getY();
		boolean reset = false;
		
		while (true) {
			
			BoundingBox overlapped = findOverlap(box);
			
			if (null == overlapped) {
				contained.add(box);
				return;
			}
			
			box.setX(overlapped.getX() + overlapped.getWidth() + 1);
			
			if (box.getX() + box.getWidth() >= this.width) {
				box.setX(0);
				box.setY(overlapped.getY() + overlapped.getHeight() + 1);
			}
			
			if (box.getY() + box.getHeight() >= this.height) {
				box.setY(0);
				reset = true;
			}
		
			if (reset && (box.getX() > originalX) && (box.getY() > originalY))
				throw new RuntimeException("No placeable");
		}
	}
	
	private BoundingBox findOverlap(BoundingBox box) {
		for (BoundingBox placed: contained) {
			if (box.overlaps(placed))
				return placed;
		}
		
		return null;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}

	public List<BoundingBox> getContained() {
		return contained;
	}

	public void setContained(List<BoundingBox> contained) {
		this.contained = contained;
	}
	
}
