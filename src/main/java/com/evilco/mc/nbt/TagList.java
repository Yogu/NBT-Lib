package com.evilco.mc.nbt;

import com.evilco.mc.nbt.stream.NBTInputStream;
import com.evilco.mc.nbt.stream.NBTOutputStream;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * @auhtor Johannes Donath <johannesd@evil-co.com>
 * @copyright Copyright (C) 2014 Evil-Co <http://www.evil-co.org>
 */
public class TagList extends AbstractTag implements IAnonymousTagContainer {

	/**
	 * Stores the tag values.
	 */
	protected List<ITag> tagList;

	/**
	 * Constructs a new TagList.
	 */
	public TagList (@Nonnull String name) {
		super (name);
		this.tagList = new ArrayList<> ();
	}

	/**
	 * Constructs a new TagList.
	 * @param name
	 * @param tagList
	 */
	public TagList (@Nonnull String name, @Nonnull List<ITag> tagList) {
		super (name);

		// verify arguments
		Preconditions.checkNotNull (tagList, "tagList");

		// save tagList
		this.tagList = tagList;
	}

	/**
	 * Constructs a new TagList.
	 * @param inputStream
	 * @param anonymous
	 * @throws IOException
	 */
	public TagList (@Nonnull NBTInputStream inputStream, boolean anonymous) throws IOException {
		super (inputStream, anonymous);

		// create tagList
		this.tagList = new ArrayList<> ();

		// get type ID
		byte type = inputStream.readByte ();

		// get type
		TagType tagType = TagType.valueOf (type);

		// verify
		if (tagType == TagType.END) throw new IOException ("Malformed NBT data: Unexpected list type: TAG_END.");

		// read size
		int size = inputStream.readInt ();

		// load all elements
		for (int i = 0; i < size; i++) {
			this.addTag (inputStream.readTag (tagType, true));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addTag (@Nonnull ITag tag) {
		this.tagList.add (tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ITag> getTags () {
		return (new ImmutableList.Builder<ITag> ().addAll (this.tagList)).build ();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public byte getTagID () {
		return TagType.LIST.typeID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeTag (@Nonnull ITag tag) {
		this.tagList.remove (tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTag (int i, @Nonnull ITag tag) {
		this.tagList.set (i, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write (NBTOutputStream outputStream, boolean anonymous) throws IOException {
		super.write (outputStream, anonymous);

		// write type
		outputStream.writeByte ((this.tagList.size () > 0 ? this.tagList.get (0).getTagID () : TagType.COMPOUND.typeID));

		// write size
		outputStream.writeInt (this.tagList.size ());

		// write tags
		for (ITag tag : this.tagList) {
			// write data
			tag.write (outputStream, true);
		}
	}
}